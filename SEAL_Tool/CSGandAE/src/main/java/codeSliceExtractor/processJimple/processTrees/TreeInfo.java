package codeSliceExtractor.processJimple.processTrees;

import codeSliceExtractor.VulnerabilityInfo;
import codeSliceExtractor.processJimple.codeData.FunctionSummary;
import codeSliceExtractor.processJimple.codeData.Instruction;
import codeSliceExtractor.processJimple.codeData.Tag;
import codeSliceExtractor.processJimple.codeData.Var;
import codeSliceExtractor.processJimple.processFuncs.JimpFuncInfo;
import codeSliceExtractor.subModProcess.ProcessWebApp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class TreeInfo {
    private HashMap<String, Var> vars = new HashMap<String, Var>();
    private HashMap<String, Var> attVars = new HashMap<String, Var>();
    private Stack<Context> contexts = new Stack<Context>();
    private Stack<Integer> labelReturns = new Stack<Integer>();
    private ArrayList<JimpFuncInfo> funcInfo;
    private FunctionSummary funcSummary;
    private ArrayList<String> instructionsString = new ArrayList<String>();
    private ArrayList<Var> paramVars;
    private String lastIfVar = "";
    private int id;

    public TreeInfo(ArrayList<JimpFuncInfo> funcInfo, String funcName, ArrayList<String> instructionsString, ArrayList<Var> paramVars) {
        this.funcInfo = funcInfo;
        this.id = 0;
        this.funcSummary = new FunctionSummary(funcName);
        this.instructionsString = instructionsString;
        this.paramVars = paramVars;
    }

    public TreeInfo(ArrayList<JimpFuncInfo> funcInfo, HashMap<String, Var> memVars, String funcName, ArrayList<String> instructionsString, ArrayList<Var> paramVars) {
        this(funcInfo, funcName, instructionsString, paramVars);
        this.vars = memVars;
    }

    /**
     *******    Getters and Setters   ******
     */
    public HashMap<String, Var> getVars() {
        return this.vars;
    }

    public boolean containsVar(String varName) {
        if(!this.contexts.empty()) { return this.contexts.lastElement().getVars().containsKey(varName); }
        else { return this.vars.containsKey(varName); }
    }

    public Var getVar(String varName) {
        Var var;
        if(varName.equals("")) {
            var = new Var("", new ArrayList<ArrayList<Object>>(), new Instruction(Tag.CONSTANT));
        }
        else {
            if(!this.contexts.empty()) { var = this.contexts.lastElement().getVar(varName); }
            else { var = this.vars.get(varName); }
        }
        return var;
    }

    public int getInstructionStringSize() { return this.instructionsString.size(); }

    public String getInstructionString(int nodeID) { return this.instructionsString.get(nodeID); }

    public ArrayList<Var> getParamVars() { return this.paramVars; }

    public HashMap<String, Var> getAttVars() { return this.attVars; }

    public Var getParamVarIndex(int index) {
        if(index < this.paramVars.size()) { return this.paramVars.get(index); }
        else { return new Var("Param"); }
    }

    public void setLastIfVar(String lastIfVar) { this.lastIfVar = lastIfVar; }

    public void putVar(String varId, Var var) { this.vars.put(varId, var); }

    public void putAttVar(String varId, Var var) { this.attVars.put(varId, var); }

    /**
     *******    Function Summary   ******
     */

    public void addPotentiallyVulnCode(ArrayList<Instruction> potVulnCode, int nodeID) { this.funcSummary.addPotentiallyVulnCode(potVulnCode, nodeID); }

    public FunctionSummary getFuncSummary() { return this.funcSummary; }

    public void updateReturnVarFuncSum(String returnVar) {
        Var var = this.getVar(returnVar);
        this.funcSummary.updateReturnVar(var);
    }

    public ArrayList<Var> getVarParams(ArrayList<String> varNames) {
        ArrayList<Var> paramsList = new ArrayList<Var>();
        for(String varName : varNames) {
            Var var = this.getVar(varName);
            if(var == null) { paramsList.add(new Var(varName, new Instruction(Tag.CONSTANT))); }
            else { paramsList.add(var); }
        }
        return paramsList;
    }

    /**
     *******    Context Methods   ******
     */
    public void pushNewContext() {
        ArrayList<Instruction> lastIfVarHist = (this.getHistoryVar(this.lastIfVar) == null) ? new ArrayList<Instruction>() : (ArrayList<Instruction>) this.getHistoryVar(this.lastIfVar).clone();
        Context context = (this.contexts.isEmpty()) ? new Context(this.vars, this.id, lastIfVarHist) : new Context(this.contexts.lastElement().getVars(), this.id, lastIfVarHist);
        this.id++;
        this.contexts.push(context);
    }

    public Context getContextLastElement() { return this.contexts.lastElement(); }

    public void popContext() { this.contexts.pop(); }

    public void updateContexts(ArrayList<Context> contextsNode) {
        for (Context contextFinal : contextsNode) {
            for (String varID : contextFinal.getAffectedVars().keySet()) {
                if (this.vars.containsKey(varID)) {
                    Var var = this.vars.get(varID);
                    Var affectedVar = contextFinal.getAffectedVars().get(varID);
                    if (affectedVar.getInstructions().get(0).getTag() == Tag.ARRAY) {
                        //if it was an object or constant previously
                        if(var.getInstructions().get(0).getTag() != Tag.ARRAY) {
                            var = new Var(varID, (ArrayList<ArrayList<Object>>) affectedVar.getValue(), new Instruction(Tag.ARRAY));
                        }
                        else { var.addValueIndex((ArrayList<ArrayList<Object>>) affectedVar.getValue()); }
                    }
                    else {
                        for (Instruction instructionAux : contextFinal.getAffectedVars().get(varID).getInstructions()) {
                            var.addInstruction(instructionAux);
                        }
                    }
                    this.vars.put(varID, var);
                }
                else {
                    Var var = contextFinal.getVar(varID);
                    this.vars.put(varID, var);
                }
            }
            ArrayList<Instruction> exitInsts = contextFinal.getExitInsts();
            if(!exitInsts.isEmpty()) {
                ArrayList<Instruction> lastVarIfInsts = contextFinal.getLastIfVarHist();
                ArrayList<String> varNamesIf = new ArrayList<String>();
                for(String varName : this.vars.keySet()) {
                    ArrayList<Instruction> sourceInsts = this.vars.get(varName).getSourceHist();
                    if(sourceInsts != null) {
                        if(this.containsSources(lastVarIfInsts, sourceInsts)) {
                            varNamesIf.add(varName);
                        }
                    }
                }
                for(String varNameIf :  varNamesIf) {
                    for(Instruction instVarIf : lastVarIfInsts) {
                        this.addInstructionVar(varNameIf, instVarIf);
                    }
                    for(Instruction instExit : exitInsts) {
                        this.addInstructionVar(varNameIf, instExit);
                    }
                }
            }
        }
    }

    public ArrayList<Instruction> getLastVarsIf() {
        ArrayList<Instruction> lastVarsIfHist = new ArrayList<Instruction>();
        for(Context context : this.contexts) {
            ArrayList<Instruction> contextLasVarIf = context.getLastIfVarHist();
            for(Instruction inst : contextLasVarIf) {
                lastVarsIfHist.add(inst);
            }
        }
        return lastVarsIfHist;
    }

    /**
     *******    Label Methods   ******
     */
    public void pushNewLabel(Integer label) { this.labelReturns.push(label); }

    public Integer getLabelLastElement() { return this.labelReturns.lastElement(); }

    public void popLabel() { this.labelReturns.pop(); }

    public boolean isLabelReturnsEmpty() { return this.labelReturns.empty(); }

    public void printVars() {
        for (String key: vars.keySet()) {
            System.out.println("Vars available " + key);
            vars.get(key).printInstructions();
        }
    }

    /**
     *******    Simple Var (No array) Methods   ******
     */
    public void newVar (String varId, Instruction instruction) {
        if (!this.contexts.empty()) {
            this.contexts.lastElement().newVar(varId, instruction);
        }
        else {
            Var var = new Var(varId, instruction);
            this.vars.put(varId, var);
        }
    }

    public void addInstructionVar(String varId, Instruction instruction) {
        if (!this.contexts.empty()) {
            this.contexts.lastElement().addInstructionVar(varId, instruction);
        }
        else {
            Var aux_var = this.vars.get(varId);
            vars.remove(varId);
            aux_var.addInstruction(instruction);
            vars.put(varId, aux_var);
        }
    }

    public void removeInstructionVar(String varId, int index) {
        if(!this.contexts.empty()) {
            this.contexts.lastElement().removeInstructionVar(varId, index);
        }
        else {
            Var auxVar = this.vars.get(varId);
            vars.remove(varId);
            auxVar.removeInstruction(index);
            vars.put(varId, auxVar);
        }
    }

    public ArrayList<Instruction> getHistoryVar(String varID) {
        if (!this.contexts.empty()) {
            return this.contexts.lastElement().getHistoryVar(varID);
        }
        else {
            if (this.vars.containsKey(varID)) {
                return this.vars.get(varID).getInstructions();
            }
            else {
                return null;
            }
        }
    }

    /**
     *******    Var Array Methods   ******
     ** The Arrays instructions are saved in the value attribute on Var **
     */
    public void newVarArray (String varID, ArrayList<Object> value, ArrayList<Instruction> arrayInst) {
        if (!this.contexts.empty()) {
            this.contexts.lastElement().newVarArray(varID, value, arrayInst);
        }
        else {
            Var var = new Var(varID, value, arrayInst);
            this.vars.put(varID, var);
        }
    }

    public void updateIndexHistoryVar (String varID, int index, ArrayList<Instruction> instructions) {
        if(!this.contexts.empty()) {
            this.contexts.lastElement().updateIndexHistoryVar(varID, index, instructions);
        }
        else {
            Var var = this.vars.get(varID);
            var.setHistoryIndex(index, instructions);
            this.vars.put(varID, var);
        }
    }

    public void updateArrayHistoryVar (String varID, ArrayList<Instruction> instructions) {
        if(!this.contexts.empty()) {
            this.contexts.lastElement().updateArrayHistoryVar(varID, instructions);
        }
        else {
            Var var = this.vars.get(varID);
            var.setArrayHistoryVar((ArrayList<Instruction>) instructions.clone());
            this.vars.put(varID, var);
        }
    }

    public ArrayList<Instruction> getHistoryVarIndex (String varId, int index) {
        if (!this.contexts.empty()) {
            return this.contexts.lastElement().getHistoryVarIndex(varId, index);
        }
        else {
            Var var = this.vars.get(varId);
            return var.getIndexHistory(index);
        }
    }

    public ArrayList<Instruction> getHistoryArrayVar(String varId) {
        if(!this.contexts.empty()) {
            return this.contexts.lastElement().getHistoryArrayVar(varId);
        }
        else {
            Var var = this.vars.get(varId);
            return var.getHistoryArray();
        }
    }

    public void setValueVar(ArrayList<ArrayList<Object>> value, String varId) {
        if(!this.contexts.empty()) {
            this.contexts.lastElement().setValueVar(value, varId);
        }
        else {
            Var var = this.vars.get(varId);
            var.setValue(value);
            this.vars.put(varId, var);
        }
    }


    /**
     *******    Multidimensional Var Array Methods   ******
     */
    public void updateValueIndexVar (String varID, int index, ArrayList<Object> value) {
        if (!this.contexts.empty()) {
            this.contexts.lastElement().updateValueIndexVar(varID, index, value);
        }
        else {
            Var var = this.vars.get(varID);
            var.setValueIndex(index, (ArrayList<Object>) value.clone());
            this.vars.put(varID, var);
        }
    }

    /** Updates all indexes with the value provided **/
    public void updateArrayValueVar (String varID, ArrayList<Object> value) {
        if(!this.contexts.empty()) {
            this.contexts.lastElement().updateArrayValueVar(varID, value);
        }
        else {
            Var var = this.vars.get(varID);
            var.setArrayValueVar(value);
            this.vars.put(varID, var);
        }
    }

    /** Is it multidimensional array **/
    public boolean isIndexArray (String varId) {
        if (!this.contexts.empty()) {
            return this.contexts.lastElement().isIndexArray(varId);
        }
        else {
            Var var = this.vars.get(varId);
            return var.isArrayofArrays();
        }
    }

    public ArrayList<Object> getValueVarIndex (String varId, int index) {
        if(!this.contexts.empty()) {
            return this.contexts.lastElement().getValueVarIndex(varId, index);
        }
        else {
            Var var = this.vars.get(varId);
            return var.getIndexValue(index);
        }
    }

    public Object getValueVar(String varId) {
        if (!this.contexts.empty()) {
            return this.contexts.lastElement().getValueVar(varId);
        }
        else {
            return this.vars.get(varId).getValue();
        }
    }

    //add("ola");
    public void addValueHist(ArrayList<Object> value, String varId) {
        if (!this.contexts.empty()) {
            this.contexts.lastElement().addValueHist(value, varId);
        }
        else {
            Var var = this.vars.get(varId);
            var.addValue(value);
            this.vars.put(varId, var);
        }
    }

    //add(0, "ola");
    public void addValueHist(ArrayList<Object> value, String varId, int index) {
        if(!this.contexts.empty()) {
            this.contexts.lastElement().addValueHist(value, varId, index);
        }
        else {
            Var var = this.vars.get(varId);
            var.addValue(value, index);
            this.vars.put(varId, var);
        }
    }

    public void clearHist(String varId) {
        if(!this.contexts.empty()) {
            this.contexts.lastElement().clearHist(varId);
        }
        else {
            Var var = this.vars.get(varId);
            var.clearValue();
            this.vars.put(varId, var);
        }
    }

    public int lastIndexArray(String varId) {
        if(!this.contexts.empty()) {
            return this.contexts.lastElement().lastIndexArray(varId);
        }
        else {
            return this.vars.get(varId).lastIndexOfArray();
        }
    }

    public ArrayList<Instruction> removeHist(String varId, int index) {
        if(!this.contexts.empty()) {
            return this.contexts.lastElement().removeHist(varId, index);
        }
        else {
            Var var = this.vars.get(varId);
            ArrayList<Instruction> removedValue = var.removeValue(index);
            this.vars.put(varId, var);
            return removedValue;
        }
    }

    public ArrayList<Instruction> setHist(String varId, int index, ArrayList<Object> value) {
        if(!this.contexts.empty()) {
            return this.contexts.lastElement().setHist(varId, index, value);
        }
        else {
            Var var = this.vars.get(varId);
            ArrayList<Instruction> oldValue = var.setValue(index, value);
            this.vars.put(varId, var);
            return oldValue;
        }
    }

    /**
     *******    Context ifs and exit methods    *******
     */

    public void addExitInstContext(Instruction inst) {
        if(!this.contexts.empty()) {
            this.contexts.lastElement().addExitInst(inst);
        }
    }

    /**
     *******    Function Information Methods   ******
     */
    public String getFuncName(int nodeID) {
        return this.funcInfo.get(nodeID).getFuncName();
    }

    public String getVarFunc(int nodeID) { return this.funcInfo.get(nodeID).getVarFunc(); }

    public String getParamsFunc(int nodeID) { return this.funcInfo.get(nodeID).getParamsString(); }

    public void setParamsFunc(int nodeID, String paramsFunc) { this.funcInfo.get(nodeID).setParamsString(paramsFunc);}

    /**
     *******    Extra Methods   ******
     */
    public String vulnerabilityName (String funcName) {
        HashMap<String, VulnerabilityInfo> infoVulns = ProcessWebApp.getInstance().getInfoVulns();
        for (String vulnerability : infoVulns.keySet()) {
            if (infoVulns.get(vulnerability).getSinks().contains(funcName)) {
                return vulnerability;
            }
        }
        return "none";
    }

    public boolean isSource(String funcName) {
        HashMap<String, VulnerabilityInfo> infoVulns = ProcessWebApp.getInstance().getInfoVulns();
        for (String vulnerability : infoVulns.keySet()) {
            if (infoVulns.get(vulnerability).getSources().contains(funcName)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsSources(ArrayList<Instruction> lastVarIf, ArrayList<Instruction> sources) {
        for(Instruction source : sources) {
            if(lastVarIf.contains(source)) {
                return true;
            }
        }
        return false;
    }

    /**
     * CompareHist
     * @return true if the history are equals, false if they are not equal
     */
    public boolean compareHist(ArrayList<Instruction> history1, ArrayList<Instruction> history2) {
        for(int i = 0; i < history1.size(); i++) {
            if(i < history2.size()) {
                if((history1.get(i).getTag() != history2.get(i).getTag()) || (!history1.get(i).getName().equals(history2.get(i).getName()))) {
                    return false;
                }
            }
            else {
                return false;
            }
        }
        return true;
    }
}
