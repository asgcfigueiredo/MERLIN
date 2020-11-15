package codeSliceExtractor.processJimple.codeData;

import codeSliceExtractor.subModProcess.ProcessWebApp;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class FunctionSummary {
    private String id;
    private ArrayList<ArrayList<Instruction>> potentiallyVulnCodes = new ArrayList<ArrayList<Instruction>>();
    private ArrayList<Integer> potVulnCodesIndexes = new ArrayList<Integer>();
    private Var returnVar;
    private HashMap<String, Var>  varThis = new HashMap<String, Var>();
    private ArrayList<Var> auxParamObj = new ArrayList<Var>();
    private HashMap<String, ArrayList<Var>> paramsUserDefFunc = new HashMap<String, ArrayList<Var>>();

    public FunctionSummary(String id) { this.id = id; }

    /**
     *******    Getters and Setters   ******
     */
    public void setVar(Var returnVar) { this.returnVar = returnVar; }

    public void setPotentiallyVulnCodes(ArrayList<ArrayList<Instruction>> potentiallyVulnCodes) { this.potentiallyVulnCodes = potentiallyVulnCodes; }

    public void setPotVulnCodesIndexes(ArrayList<Integer> indexes) { this.potVulnCodesIndexes = indexes; }

    public void setAuxParamObj(ArrayList<Var> auxParamObj) { this.auxParamObj = auxParamObj; }

    public void setParamsUserDefFunc(String fullFuncName, ArrayList<Var> params) { this.paramsUserDefFunc.put(fullFuncName, params); }

    public void addPotentiallyVulnCode(ArrayList<Instruction> potentiallyVulnCodeSlice, int nodeID) {
        potentiallyVulnCodes.add(potentiallyVulnCodeSlice);
        potVulnCodesIndexes.add(nodeID);
    }

    public Var getReturnVar() {
        if (this.returnVar == null) { return new Var(""); }
        else { return this.returnVar; }
    }

    public String getId() {return this.id; }

    public HashMap<String, Var> getAttVars() { return this.varThis; }

    public ArrayList<ArrayList<Instruction>> getPotentiallyVulnCodes() { return potentiallyVulnCodes; }

    public ArrayList<Integer> getPotVulnCodesIndexes() { return potVulnCodesIndexes; }

    public HashMap<String, ArrayList<Var>> getParamsUserDefFunc() { return this.paramsUserDefFunc; }

    public Var getVarThis(String attName) {
        if(this.varThis.get(attName) == null) {
            return new Var(attName, new Instruction(Tag.OBJECT, "@this"));
        }
        if(this.hasParameters(this.varThis.get(attName).getInstructions())) {
            ArrayList<Instruction> newInstructions = new ArrayList<Instruction>();
            for(Instruction singleInst : this.varThis.get(attName).getInstructions()) {
                if(singleInst.getTag() == Tag.PARAMETER) {
                    String[] partsParam = singleInst.getName().split("@parameter");
                    ArrayList<Instruction> auxCodeSliceParams = this.historyParam(this.auxParamObj, partsParam);
                    for(Instruction singleAuxInst : auxCodeSliceParams) {
                        newInstructions.add(singleAuxInst);
                    }
                }
                else { newInstructions.add(singleInst); }
            }
            return new Var(attName, new ArrayList<ArrayList<Object>>(), newInstructions);
        }
        return this.varThis.get(attName);
    }

    public void putVarThis(String varId, Var var) { this.varThis.put(varId, var); }

    /**
     *******    Process Parameters   ******
     */
    public void updateReturnVar(Var var) {
        if(this.returnVar == null) { this.returnVar = var; }
        else {
            this.returnVar.addInstructions(var.getInstructions());
            if(var.getValue() != null) {
                this.returnVar.addValue((ArrayList<Object>) var.getValue());
            }
        }
    }

    public Var getRetVarParam(ArrayList<Var> params, ArrayList<Instruction> objInsts, HashMap<String, Var> attVars) {
        if(this.returnVar == null) { return new Var(""); }
        else {
            ArrayList<Instruction> instsRetVar = this.returnVar.getInstructions();
            ArrayList<Instruction> newRetVarParam = new ArrayList<Instruction>();
            Var var;
            boolean changed = false;
            for(Instruction singleInst : instsRetVar) {
                if(singleInst.getTag() == Tag.PARAMETER) {
                    String[] partsParam = singleInst.getName().split("@parameter");
                    ArrayList<Instruction> auxVarInsts = this.historyParam(params, partsParam);
                    for(Instruction instParam : auxVarInsts) {
                        newRetVarParam.add(instParam);
                    }
                    changed = true;
                }
                else if(singleInst.getTag() == Tag.OBJECT && singleInst.getName().startsWith("@this")) {
                    changed = processThis(objInsts, newRetVarParam, singleInst, changed, attVars);
                }
                else {
                    newRetVarParam.add(singleInst);
                }
            }
            if(changed) {
                var = new Var(this.returnVar.getId(), newRetVarParam.get(0));
                newRetVarParam.remove(0);
                var.addInstructions(newRetVarParam);
            }
            else {
                var = this.returnVar;
            }
            return var;
        }
    }

    public ArrayList<Instruction> historyParam(ArrayList<Var> params, String[] partsParam) {
        ArrayList<Instruction> auxCodeSlice = new ArrayList<Instruction>();
        if(partsParam != null) {
            if(partsParam.length > 1) {
                if(partsParam[1].contains("[")) {
                    String[] splitVarArray = partsParam[1].split("\\[");
                    int index = Integer.parseInt(splitVarArray[0]);
                    if(index < params.size()) {
                        Var paramVar = params.get(index);
                        String[] splitIndex = splitVarArray[1].split("\\]");
                        String sIndex = splitIndex[0];
                        int indexVar = -1;
                        if(StringUtils.isNumeric(sIndex)) {
                            indexVar = Integer.parseInt(sIndex);
                        }
                        auxCodeSlice = (indexVar > -1) ? (ArrayList<Instruction>) paramVar.getIndexHistory(indexVar).clone() : (ArrayList<Instruction>) paramVar.getHistoryArray().clone();
                    }
                }
                else {
                    int index = Integer.parseInt(partsParam[1]);
                    if(index < params.size()) {
                        Var paramVar = params.get(index);
                        auxCodeSlice = paramVar.getInstructions();
                    }
                }
            }
        }
        return auxCodeSlice;
    }

    public ArrayList<Integer> processParams(ArrayList<Var> params, ArrayList<Instruction> objInsts, HashMap<String, Var> attVars) {
        ArrayList<ArrayList<Instruction>> newPotenVulnCode = new ArrayList<ArrayList<Instruction>>();
        ArrayList<Boolean> paramVarPresent = new ArrayList<Boolean>();
        ArrayList<Integer> newPotVulnIndexes = new ArrayList<Integer>();
        for(int j = 0; j < this.potentiallyVulnCodes.size(); j++) {
            ArrayList<Instruction> codeSlice = this.potentiallyVulnCodes.get(j);
            int indexPotVuln = this.potVulnCodesIndexes.get(j);
            boolean paramVarPres = false;
            boolean changedParam = false;
            ArrayList<Instruction> newCodeSlice = new ArrayList<Instruction>();
            ArrayList<Instruction> auxCodeSlice;
            for(int i = 0; i < codeSlice.size(); i++) {
                Instruction singleInst = codeSlice.get(i);
                String[] partsParam = singleInst.getName().split("@parameter");
                if(singleInst.getTag() == Tag.PARAMETER) {
                    auxCodeSlice = this.historyParam(params, partsParam);
                    for(Instruction varInst : auxCodeSlice) {
                        newCodeSlice.add(varInst);
                        if(varInst.getTag() == Tag.PARAMETER) { paramVarPres = true; }
                    }
                    changedParam = true;
                }
                else if(singleInst.getTag() == Tag.OBJECT && singleInst.getName().startsWith("@this")) {
                    changedParam = processThis(objInsts, newCodeSlice, singleInst, changedParam, attVars);
                }
                else {
                    newCodeSlice.add(singleInst);
                }
            }
            if(changedParam) {
                newPotenVulnCode.add(newCodeSlice);
                newPotVulnIndexes.add(indexPotVuln);
                paramVarPresent.add(paramVarPres);
            }
        }
        ArrayList<Integer> potVulParamIndex = new ArrayList<Integer>();
        for(int i = 0; i < newPotenVulnCode.size(); i++) {
            this.potentiallyVulnCodes.add(newPotenVulnCode.get(i));
            this.potVulnCodesIndexes.add(newPotVulnIndexes.get(i));
            if(paramVarPresent.get(i)) { potVulParamIndex.add(this.potentiallyVulnCodes.size()-1); }
        }
        return potVulParamIndex;
    }

    public void replaceParamPotVuln(ArrayList<Var> params, int index) {
        if(index < this.getPotentiallyVulnCodes().size()) {
            ArrayList<Instruction> potVulnCode = this.potentiallyVulnCodes.get(index);
            boolean changedParam = false;
            ArrayList<Instruction> auxCodeSlice;
            ArrayList<Instruction> newCodeSlice = new ArrayList<Instruction>();
            for(int i = 0; i < potVulnCode.size(); i++) {
                Instruction singleInst = potVulnCode.get(i);
                if(singleInst.getTag() == Tag.PARAMETER) {
                    String[] partsParam = singleInst.getName().split("@parameter");
                    auxCodeSlice = this.historyParam(params, partsParam);
                    for (Instruction varInst : auxCodeSlice) {
                        varInst.print();
                        newCodeSlice.add(varInst);
                    }
                    changedParam = true;
                }
                else {
                    newCodeSlice.add(singleInst);
                }
            }
            if(changedParam) { this.potentiallyVulnCodes.set(index, newCodeSlice); }
        }
    }

    public void processVarThis(HashMap<String, Var> vars) {
        for(String varName : vars.keySet()) {
            if(varName.contains(".")) {
                Var objVar = vars.get(varName);
                String[] varParts = varName.split("\\.");
                objVar.setId(varParts[1]);
                if(this.varThis.containsKey(varParts[1])) {
                    for(Instruction inst : this.varThis.get(varParts).getInstructions()) {
                        objVar.addInstruction(inst);
                    }
                }
                this.varThis.put(varParts[1], objVar);
            }
        }
    }

    /**
     *******    User Defined Calls Methods   ******
     */

    public void processUserDefFunc(ArrayList<Var> specificParams, ArrayList<Instruction> objInst) {
        for(String funcKey : this.paramsUserDefFunc.keySet()) {
            ArrayList<Var> newParams = new ArrayList<Var>();
            for(Var paramVar : this.paramsUserDefFunc.get(funcKey)) {
                ArrayList<Instruction> newCodeSlice = new ArrayList<Instruction>();
                ArrayList<ArrayList<Instruction>> newInstVal = new ArrayList<ArrayList<Instruction>>();
                for(Instruction singleInst : paramVar.getInstructions()) {
                    String[] partsParam = singleInst.getName().split("@parameter");
                    if(singleInst.getTag() == Tag.PARAMETER) {
                        ArrayList<Instruction> auxCodeSlice = this.historyParam(specificParams, partsParam);
                        for (Instruction varInst : auxCodeSlice) {
                            newCodeSlice.add(varInst);
                        }
                    }
                    else { newCodeSlice.add(singleInst); }
                }
                if(paramVar.isArray()) {
                    ArrayList<ArrayList<Instruction>> instVal = (ArrayList<ArrayList<Instruction>>) paramVar.getValue();
                    for(ArrayList<Instruction> instIndex : instVal) {
                        ArrayList<Instruction> newInstIndex = new ArrayList<Instruction>();
                        for(Instruction singleInstIndex : instIndex) {
                            String[] partsParam = singleInstIndex.getName().split("@parameter");
                            if(singleInstIndex.getTag() == Tag.PARAMETER) {
                                ArrayList<Instruction> auxCodeSlice = this.historyParam(specificParams, partsParam);
                                for (Instruction varInst : auxCodeSlice) {
                                    newInstIndex.add(varInst);
                                }
                            }
                            else { newInstIndex.add(singleInstIndex); }
                        }
                        newInstVal.add(newInstIndex);
                    }
                }
                Var newVar = new Var(paramVar.getId(), newInstVal, newCodeSlice);
                newParams.add(newVar);
            }
            ProcessWebApp processWebApp = ProcessWebApp.getInstance();
            processWebApp.processFuncSummaryParams(funcKey, newParams, objInst, new HashMap<String, Var>());
            processWebApp.processUserDefFunc(funcKey, newParams, objInst);
        }
    }

    /**
     *******    FunctionSummary Extra Methods   ******
     */
    public FunctionSummary clone() {
        FunctionSummary clonedFunctionSummary = new FunctionSummary(this.id);
        clonedFunctionSummary.setVar(this.returnVar);
        clonedFunctionSummary.setPotentiallyVulnCodes((ArrayList<ArrayList<Instruction>>) this.potentiallyVulnCodes.clone());
        clonedFunctionSummary.setPotVulnCodesIndexes((ArrayList<Integer>) this.potVulnCodesIndexes.clone());
        return clonedFunctionSummary;
    }

    public void merge(FunctionSummary funcSummary2) {
        ArrayList<ArrayList<Instruction>> potVulnCode2 = (ArrayList<ArrayList<Instruction>>) funcSummary2.getPotentiallyVulnCodes().clone();
        ArrayList<Integer> potVulnIndexes = (ArrayList<Integer>) funcSummary2.getPotVulnCodesIndexes().clone();
        for(int i = 0; i < potVulnCode2.size(); i++) {
            ArrayList<Instruction> codeSlice = potVulnCode2.get(i);
            if(!this.potentiallyVulnCodes.contains(codeSlice)) {
                this.potentiallyVulnCodes.add((ArrayList<Instruction>) codeSlice.clone());
                this.potVulnCodesIndexes.add(potVulnIndexes.get(i));
            }
        }
        for(String keyUserDef : funcSummary2.getParamsUserDefFunc().keySet()) {
            this.paramsUserDefFunc.put(keyUserDef, funcSummary2.getParamsUserDefFunc().get(keyUserDef));
        }
        this.updateReturnVar(funcSummary2.getReturnVar());
    }

    /**
     *******    Auxiliary Methods   ******
     */
    public boolean processThis(ArrayList<Instruction> objInsts, ArrayList<Instruction> newCodeSlice, Instruction singleInst,
                               boolean changedParam, HashMap<String, Var> attVars) {
        //if @this do this
        if(singleInst.getName().equals("@this")) {
            if(this.hasSources(objInsts)) {
                for(Instruction objInst : objInsts) {
                    newCodeSlice.add(objInst);
                }
                changedParam = true;
            }
            else { newCodeSlice.add(singleInst); }
        }
        else {
            String[] partThis = singleInst.getName().split("@this-");
            String thisAtt = partThis[1];
            String sIndex = "";
            if(thisAtt.contains("[")) {
                String[] pArrayThis = thisAtt.split("\\[");
                thisAtt = pArrayThis[0];
                String[] partsSIndex = pArrayThis[1].split("\\]");
                sIndex = partsSIndex[0];
            }
            Var attVar = new Var("this", singleInst);
            for(String keyAtts : attVars.keySet()) {
                String attName = keyAtts;
                if(keyAtts.contains(".")) {
                    String[] partKeyAtts = keyAtts.split("\\.");
                    attName = partKeyAtts[1];
                }
                if(attName.equals(thisAtt)) {
                    changedParam = true;
                    attVar = attVars.get(keyAtts);
                    if(attVar.getInstructions().size() == 1) {
                        if(attVar.getInstructions().get(0).getTag() == Tag.OBJECT) { changedParam = false; }
                    }
                    break;
                }
            }
            ArrayList<Instruction> auxCodeSlice;
            if(sIndex.equals("")) {
                auxCodeSlice = attVar.getInstructions();
            }
            else {
                int indexVar = -1;
                if(StringUtils.isNumeric(sIndex)) {
                    indexVar = Integer.parseInt(sIndex);
                }
                auxCodeSlice = (indexVar > -1) ? (ArrayList<Instruction>) attVar.getIndexHistory(indexVar).clone() : (ArrayList<Instruction>) attVar.getHistoryArray().clone();
            }
            for(Instruction inst : auxCodeSlice) {
                if(inst.getTag() == Tag.PARAMETER) {
                    String[] partsParam = inst.getName().split("@parameter");
                    ArrayList<Instruction> auxCodeSliceParams = this.historyParam(this.auxParamObj, partsParam);
                    for(Instruction singleAuxInst : auxCodeSliceParams) {
                        newCodeSlice.add(singleAuxInst);
                    }
                }
                else { newCodeSlice.add(inst); }
            }
        }
        //else check if att exists, get att and put and process with [
        return changedParam;
    }

    public boolean hasSources(ArrayList<Instruction> objInsts) {
        if(objInsts != null) {
            for (Instruction i : objInsts) {
                if (i.getTag() == Tag.SOURCE) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasParameters(ArrayList<Instruction> objInsts) {
        if(objInsts != null) {
            for(Instruction i : objInsts) {
                if(i.getTag() == Tag.PARAMETER) {
                    return  true;
                }
            }
        }
        return false;
    }
}
