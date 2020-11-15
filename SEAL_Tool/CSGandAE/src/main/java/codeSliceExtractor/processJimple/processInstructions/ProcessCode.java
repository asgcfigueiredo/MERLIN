package codeSliceExtractor.processJimple.processInstructions;


import codeSliceExtractor.cfg.GraphProcessing;
import codeSliceExtractor.processJimple.codeData.FunctionSummary;
import codeSliceExtractor.processJimple.codeData.Instruction;
import codeSliceExtractor.processJimple.codeData.Tag;
import codeSliceExtractor.processJimple.codeData.Var;
import codeSliceExtractor.processJimple.processFuncs.JimpFuncInfo;
import codeSliceExtractor.processJimple.processFuncs.ProcessFuncInfo;
import codeSliceExtractor.processJimple.processTrees.*;
import codeSliceExtractor.subModProcess.ProcessWebApp;
import com.sun.prism.shader.Solid_TextureYV12_AlphaTest_Loader;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ProcessCode {
    private Tokenizer tokenizer = new Tokenizer();
    private TreeInfo treeInfo;
    private int funcCount = 0;
    private ArrayList<String> methodsFuncs = new ArrayList<String>(Arrays.asList("java.lang.StringBuilder.append", "java.lang.String.concat",
            "java.lang.StringBuilder.toString", "java.lang.String.split", "java.lang.StringBuilder.substring", "java.lang.String.subSequence",
            "java.lang.String.charAt", "java.util.regex.Matcher.group", "java.lang.String.replace", "java.lang.String.replaceFirst",
            "java.lang.String.replaceAll", "java.util.regex.Matcher.replaceAll", "java.util.regex.Matcher.replaceFirst",
            "java.util.regex.Matcher.appendReplacement", "java.lang.Object.getClass", "java.lang.Class.getName",
            "java.lang.Class.isArray", "java.lang.Class.isInstance", "java.lang.Class.isLocalClass", "java.lang.Class.isPrimitive",
            "java.lang.String.compareTo", "java.lang.String.compareToIgnoreCase", "java.lang.String.equals",
            "java.lang.String.equalsIgnoreCase", "java.lang.String.matches", "java.lang.String.regionMatches",
            "java.lang.String.startsWith", "java.util.regex.Matcher.matches", "java.util.regex.Matcher.lookingAt",
            "java.util.regex.Matcher.find", "java.lang.Integer.doubleValue", "java.lang.Integer.intValue",
            "java.lang.Integer.longValue", "java.lang.Integer.shortValue", "java.sql.ResultSet.getString", "java.sql.ResultSet.getString",
            "javax.servlet.http.Cookie.getValue", "java.util.StringTokenizer.nextToken", "java.lang.String.substring",
            "java.util.StringTokenizer.nextToken", "java.util.StringTokenizer.hasMoreTokens", "java.io.ByteArrayOutputStream.toByteArray",
            "java.io.ObjectInputStream.readObject"));
    private ArrayList<String> abstractListFuncs = new ArrayList<String>(Arrays.asList("java.util.ArrayList.add", "java.util.Vector.add",
            "java.util.Stack.add", "java.util.LinkedList.add", "java.util.ArrayList.get", "java.util.Vector.get", "java.util.Stack.get",
            "java.util.LinkedList.get", "java.util.ArrayList.clear", "java.util.Vector.clear", "java.util.Stack.clear",
            "java.util.LinkedList.clear", "java.util.ArrayList.remove", "java.util.Vector.remove", "java.util.Stack.remove",
            "java.util.LinkedList.remove", "java.util.ArrayList.set", "java.util.Vector.set", "java.util.Stack.set",
            "java.util.LinkedList.set", "java.util.Vector.lastElement", "java.util.Stack.lastElement",
            "java.util.Vector.push", "java.util.Stack.push", "java.util.Vector.pop", "java.util.Stack.pop"));
    private ArrayList<String> hashmapFuncs = new ArrayList<String>(Arrays.asList("java.util.HashMap.put", "java.util.HashMap.get",
            "java.util.HashMap.replace", "java.util.HashMap.ketSet", "java.util.HashMap.remove"));

    public ProcessCode(TreeInfo treeInfo) {
        this.treeInfo = treeInfo;
    }

    /**
     * General Process Function
     * @param instruction
     * @param instructionList
     * @param nodeID
     */
    public void processInstruction(String instruction, ArrayList<String> instructionList, int nodeID) throws Exception{
        //System.out.println("NodeId: " + nodeID);
        ArrayList<String> tokenInstruction;
        /**Because of the label instruction which will be recursive**/
        if (instruction.equals("")) {
            tokenInstruction = instructionList;
        }
        else {
            tokenInstruction = this.tokenizer.splitInstruction(instruction);
        }
        if (tokenInstruction.get(0).equals("if")) {
            /**assuming that after the if there is always a space **/
            tokenInstruction.remove(0);
            tokenInstruction.remove(0);
            processConditional(tokenInstruction);
        }
        else if (tokenInstruction.get(0).startsWith("return")) {
            tokenInstruction.remove(0);
            if(!tokenInstruction.isEmpty()) {
                tokenInstruction.remove(0);
                String returnVar = tokenInstruction.get(0);
                if(this.tokenizer.isConstant(returnVar)) { this.treeInfo.updateReturnVarFuncSum(""); }
                else { this.treeInfo.updateReturnVarFuncSum(returnVar); }
            }
            Instruction exitInstruction = new Instruction(Tag.FUNCTION, "return");
            this.treeInfo.addExitInstContext(exitInstruction);
            FunctionSummary functionSummary = this.treeInfo.getFuncSummary();
            ProcessWebApp processWebApp = ProcessWebApp.getInstance();
            processWebApp.updateFunctionSummary(functionSummary.getId(), functionSummary);
            String fullFuncId = this.treeInfo.getFuncSummary().getId();
            String[] partsFuncID = fullFuncId.split("\\(");
            int lastIndexSlash = partsFuncID[0].lastIndexOf("\\");
            String funcID = partsFuncID[0].substring(lastIndexSlash + 1);
            if(funcID.equals("[init]")) {
                processWebApp.processVarThis(fullFuncId, this.treeInfo.getVars());
            }
        }
        else if(tokenInstruction.get(0).startsWith("throw")) {
            Instruction inst = new Instruction(Tag.FUNCTION, "throw");
            this.treeInfo.addExitInstContext(inst);
        }
        else if(tokenInstruction.get(0).equals("goto")) {
            return;
        }
        else if(tokenInstruction.get(0).contains("label")) {
            tokenInstruction.remove(0);
            tokenInstruction.remove(0);
            processInstruction("", tokenInstruction, nodeID);
        }

        else {
            if (tokenInstruction.get(0).equals("specialinvoke")) {
                tokenInstruction.remove(0);
                tokenInstruction.remove(0);
            }
            String substring = "";
            for (int i = 0; i < tokenInstruction.size(); i++) {
                if (tokenInstruction.get(i).equals("=") || tokenInstruction.get(i).equals(":=")) {
                    String left_var = substring;
                    List<String> restInstructionAux = tokenInstruction.subList(i, tokenInstruction.size());
                    substring = "";
                    ArrayList<String> restInstruction = new ArrayList<String>(restInstructionAux);
                    restInstruction.remove(0);
                    restInstruction.remove(0);
                    processAssignment(left_var, restInstruction, nodeID);
                    break;
                }
                else if (tokenInstruction.get(i).equals("(")) {
                    i++;
                    List<String> parametersAux = tokenInstruction.subList(i, tokenInstruction.size());
                    ArrayList<String> parameters = new ArrayList<String>(parametersAux);
                    this.processFunction(parameters, nodeID);
                    break;
                }
                else if (!tokenInstruction.get(i).equals(" ")) {
                    substring += tokenInstruction.get(i);
                }
            }
        }
    }

    /**
     * Process Conditional
     * @param tokenInstruction
     */
    public void processConditional(ArrayList<String> tokenInstruction) throws Exception {
        ArrayList<String> vars = new ArrayList<String>();
        String substring = "";
        boolean presentNull = false;
        for (int i = 0; i < tokenInstruction.size(); i++) {
            //Supposedly this if is useless, but just for safety reasons we are going to leave it here
            if (this.tokenizer.getListOperators().contains(tokenInstruction.get(i))) {
                if (!substring.equals("")) {
                    vars.add(substring);
                    substring = "";
                }
            }
            else if (tokenInstruction.get(i).equals("goto")) {
                if (!substring.equals("")) {
                    vars.add(substring);
                }
                break;
            }
            else if(this.tokenizer.isConstant(tokenInstruction.get(i))) {
                if(tokenInstruction.get(i).equals("null")) { presentNull = true; }
            }
            else if (!tokenInstruction.get(i).equals(" ")) {
                substring += tokenInstruction.get(i);
            }
        }
        //update vars (This only works if this function only processes the if)
        for(String var: vars) {
            Instruction instruction = new Instruction(Tag.IF);
            if (this.treeInfo.getHistoryVar(var) != null) {
                if((this.treeInfo.getHistoryVar(var).get(0).getTag() == Tag.SOURCE) && (this.treeInfo.getHistoryVar(var).size() == 1) &&
                    presentNull)  {
                    Instruction sourceInst = new Instruction(Tag.FUNCTION, "isSetSource");
                    this.treeInfo.addInstructionVar(var, sourceInst);
                }
                this.treeInfo.addInstructionVar(var, instruction);
                this.treeInfo.setLastIfVar(var);
            }
        }

    }

    /**
     *******    Process Assignment Methods   ******
     */
    public void processAssignment(String left_var, ArrayList<String> tokenInstructions, int nodeID) throws Exception{
        ArrayList<String> varsDep = new ArrayList<String>();
        ArrayList<Instruction> instAssign = new ArrayList<Instruction>();
        HashMap<String, ArrayList<Instruction>> funcParams = new HashMap<String, ArrayList<Instruction>>();
        String substring = "";
        boolean functionPresent = false;

        if(tokenInstructions.get(0).equals("newarray")) { this.processNewArray(left_var, tokenInstructions); }
        else {
            if (tokenInstructions.get(0).equals("(")) {
                Instruction instruction = new Instruction(Tag.CAST);
                instAssign.add(instruction);
                boolean finishCast = false;
                while (!finishCast) {
                    tokenInstructions.remove(0);
                    if (tokenInstructions.get(0).equals(")")) {
                        tokenInstructions.remove(0);
                        finishCast = true;
                    }
                }
            }
            if(tokenInstructions.get(0).equals("new")) {
                //if something like this a = new ola, it is classified as a FUNCNAME
                tokenInstructions.remove(0);
                tokenInstructions.remove(0);
            }

            String varId = left_var;
            int index = -1;
            if(left_var.contains("[")) {
                HashMap<String, String> varInfo = this.tokenizer.splitArray(left_var);
                varId = varInfo.get("var");
                String stringIndex = varInfo.get("index");
                if (StringUtils.isNumeric(stringIndex)) {
                    index = Integer.parseInt(stringIndex);
                }

            }
            String token;
            for (int k = 0; k < tokenInstructions.size(); k++) {
                token = tokenInstructions.get(k);
                if (this.tokenizer.getListOperators().contains(token)) {
                    Instruction aux_instruction;
                    if(!token.equals("instanceof")) { aux_instruction = new Instruction(Tag.OPERATION); }
                    else { aux_instruction = new Instruction(Tag.OPERATION, "instanceof"); }
                    if(!instAssign.contains(aux_instruction)) {
                        instAssign.add(aux_instruction);
                    }
                    if (!substring.equals("")) {
                        varsDep.add(substring);
                        substring = "";
                    }
                }
                else if(token.equals("(")) {
                    functionPresent = true;
                    k++;
                    List<String> parametersAux = tokenInstructions.subList(k, tokenInstructions.size());
                    ArrayList<String> parameters = new ArrayList<String>(parametersAux);
                    HashMap<String, ArrayList<Instruction>> historyParam = this.processFunction(parameters, nodeID);
                    //Replace
                    funcParams.putAll(historyParam);
                    substring = "";
                    break;
                }
                else if(!this.tokenizer.isConstant(token)) {
                    substring += token;
                }
            }
            //Out of for, it is necessary to consider the last var
            if (!substring.equals("") && !this.tokenizer.isConstant(substring)) {
                if(substring.startsWith("@parameter")) {
                    Instruction instruction = new Instruction(Tag.PARAMETER, substring);
                    instAssign.add(instruction);
                }
                else if(substring.equals("@this")) {
                    Instruction instruction = new Instruction(Tag.OBJECT, substring);
                    instAssign.add(instruction);
                }
                //ola.cenas
                else if(substring.contains(".")) {
                    String[] partsSubstring = substring.split("\\.");
                    if(partsSubstring.length < 3) {
                        String fullFuncId = this.treeInfo.getFuncSummary().getId();
                        String[] partsFuncID = fullFuncId.split("\\(");
                        int lastIndexSlash = partsFuncID[0].lastIndexOf("\\");
                        String funcID = partsFuncID[0].substring(lastIndexSlash + 1);
                        if(!funcID.equals("[init]")) {
                            String funcInit = partsFuncID[0].substring(0, lastIndexSlash) + "\\[init](";
                            ProcessWebApp processWebApp = ProcessWebApp.getInstance();
                            String fullInitFunc = processWebApp.getFullInitFuncSummary(funcInit);
                            if(!fullInitFunc.equals("")) {
                                Var attVar = processWebApp.getInstVarThis(fullInitFunc, partsSubstring[1]);
                                if(attVar.getInstructions().get(0).getTag() == Tag.OBJECT && attVar.getInstructions().get(0).getName().equals("@this") && attVar.getInstructions().size() == 1) {
                                    this.treeInfo.putVar(partsSubstring[1], new Var(partsSubstring[1], new Instruction(Tag.OBJECT, "@this-" + partsSubstring[1])));
                                }
                                else {
                                    if(attVar.getValue() == null) { this.treeInfo.putVar(partsSubstring[1], attVar);}
                                    else { this.treeInfo.newVarArray(partsSubstring[1], (ArrayList<Object>) attVar.getValue(), (ArrayList<Instruction>) attVar.getInstructions().clone()); }
                                }
                                varsDep.add(partsSubstring[1]);
                            }
                        }
                        if(treeInfo.getVars().containsKey(partsSubstring[0])) { varsDep.add(partsSubstring[0]); }
                    }

                    else { varsDep.add(substring); }
                }
                else { varsDep.add(substring); }
            }

            if (varsDep.isEmpty()  && !functionPresent) {
                if(!instAssign.isEmpty()) {
                    this.treeInfo.newVar(left_var, instAssign.get(0));
                    for(int i = 1; i < instAssign.size(); i++) {
                        this.treeInfo.addInstructionVar(left_var, instAssign.get(i));
                    }
                }
                else {
                    Instruction instruction = new Instruction(Tag.CONSTANT, substring);
                    if(left_var.contains("[")) {
                        ArrayList<Instruction> instructions = new ArrayList<Instruction>();
                        instructions.add(instruction);
                        this.treeInfo.updateIndexHistoryVar(varId, index, instructions);
                    }
                    else {
                        this.treeInfo.newVar(left_var, instruction);
                    }
                }
            }
            else {
                this.updateVarsAss(left_var.contains("["), varId, index, varsDep, instAssign, funcParams);
                ArrayList<Instruction> sourceInsts = this.treeInfo.getVar(varId).getSourceHist();
                if(!sourceInsts.isEmpty()) {
                    ArrayList<Instruction> lastVarsIf = this.treeInfo.getLastVarsIf();
                    if(this.treeInfo.containsSources(lastVarsIf, sourceInsts)) {
                        for(Instruction inst : lastVarsIf) {
                            this.treeInfo.addInstructionVar(varId, inst);
                        }
                    }
                }
            }
        }
        if(left_var.contains(".")) {
            this.treeInfo.putAttVar(left_var, this.treeInfo.getVar(left_var));
        }
    }

    public void processNewArray(String left_var, ArrayList<String> tokenInstructions) throws Exception{
        int j;
        for (j = 0; j < tokenInstructions.size(); j++) {
            if (tokenInstructions.get(j).equals(")")) { break; }
        }
        j++;
        HashMap<String, String> tokensArray = this.tokenizer.splitArray(tokenInstructions.get(j));
        int size = Integer.parseInt(tokensArray.get("index"));
        if(tokenInstructions.get(3).contains("[")) {
            ArrayList<Object> arrayObjs = new ArrayList<Object>();
            Object dummyObject = new Object();
            for (int i = 0; i < size; i++) {
                arrayObjs.add(dummyObject);
            }
            this.treeInfo.newVarArray(left_var, arrayObjs, new ArrayList<Instruction>(Arrays.asList(new Instruction(Tag.ARRAY))));
        }
        else {
            ArrayList<Object> arrayInstructions = new ArrayList<Object>();
            ArrayList<Instruction> dummyInstructions = new ArrayList<Instruction>();
            for (int i = 0; i < size; i++) {
                arrayInstructions.add(dummyInstructions);
            }
            this.treeInfo.newVarArray(left_var, arrayInstructions, new ArrayList<Instruction>(Arrays.asList(new Instruction(Tag.ARRAY))));
        }
    }

    /**
     * Takes into account everything that can appear in assignment, from vars to constants to functions
     **/
    public void updateVarsAss(boolean leftVarArray, String varId, int index, ArrayList<String> varsDep, ArrayList<Instruction> instAssign,
                              HashMap<String, ArrayList<Instruction>> funcHistory) throws Exception{
        boolean varCreated = false;
        ArrayList<Instruction> history = new ArrayList<Instruction>();
        for (String varDep : varsDep) {
            varCreated = this.updateSingleVar(varDep, leftVarArray, varId, index, varCreated, history);
        }
        for (String key : funcHistory.keySet()) {
            history = funcHistory.get(key);
            varCreated = this.updateSingleVar(key, leftVarArray, varId, index, varCreated, history);
        }
        if(!instAssign.isEmpty()) {
            int j = 1;
            if(!varCreated) { this.treeInfo.newVar(varId, instAssign.get(0)); }
            else { j = 0; }
            for (int i = j; i < instAssign.size(); i++) {
                this.treeInfo.addInstructionVar(varId, instAssign.get(i));
            }
        }
    }

    public boolean updateSingleVar(String rightVar, boolean leftVarArray, String varId, int index, boolean varCreated,
                                   ArrayList<Instruction> history) throws Exception{
        boolean isArray = false;
        ArrayList<Object> objs = new ArrayList<Object>();
        //if instruction a = b[2]
        if (rightVar.contains("[")) {
            HashMap<String, String> varInfo = this.tokenizer.splitArray(rightVar);
            String varDepId = varInfo.get("var");
            String sIndexVarDep = varInfo.get("index");
            if(this.treeInfo.getHistoryVar(varDepId).get(0).getTag() == Tag.PARAMETER) {
                String namePam = this.treeInfo.getHistoryVar(varDepId).get(0).getName() + "[" + sIndexVarDep + "]";
                Instruction instAux = new Instruction(Tag.PARAMETER, namePam);
                history = new ArrayList<Instruction>(Arrays.asList(instAux));
            }
            else {
                int indexVarDep = -1;
                if (StringUtils.isNumeric(sIndexVarDep)) {
                    indexVarDep = Integer.parseInt(sIndexVarDep);
                }
                if(this.treeInfo.getValueVar(varDepId) != null) {
                    //is it multidimensional??
                    if (this.treeInfo.isIndexArray(varDepId)) {
                        isArray = true;
                        objs = (indexVarDep != -1) ?  (ArrayList<Object>) this.treeInfo.getValueVarIndex(varDepId, indexVarDep) : (ArrayList<Object>) this.treeInfo.getValueVar(varDepId);
                        if(history.isEmpty()) {
                            Instruction arrayInst = new Instruction(Tag.ARRAY);
                            history = new ArrayList<Instruction>(Arrays.asList(arrayInst));
                        }
                    }
                    else {
                        history = (indexVarDep != -1) ? (ArrayList<Instruction>) this.treeInfo.getHistoryVarIndex(varDepId, indexVarDep).clone() : (ArrayList<Instruction>) this.treeInfo.getHistoryArrayVar(varDepId).clone();
                    }
                }
                else {
                    if(history == null || history.isEmpty()) { history = this.treeInfo.getHistoryVar(varDepId); }
                }
            }
        }
        else if(history.isEmpty()) {
            history = this.treeInfo.getHistoryVar(rightVar);
            ProcessWebApp processWebApp = ProcessWebApp.getInstance();
            history = processWebApp.additionalProcessAss(history, rightVar);
            if (history == null) {
                String objName = rightVar;
                if(rightVar.contains(".")) {
                    String[] splitRightVar = rightVar.split("\\.");
                    String objVarName = splitRightVar[0];
                    ArrayList<Instruction> objInsts = this.treeInfo.getHistoryVar(objVarName);
                    if(objInsts != null) {
                        Instruction objAuxInstruction = new Instruction(Tag.OBJECT, "@this");
                        if(objInsts.contains(objAuxInstruction)) {
                            objName = "@this";
                        }
                    }
                }
                history = new ArrayList<Instruction>();
                Instruction instructionAux = new Instruction(Tag.OBJECT, objName);
                history.add(instructionAux);
            }
        }

        if (history.get(0).getTag() == Tag.ARRAY && !NumberUtils.isCreatable(rightVar) && !rightVar.contains("[") &&
                !(history.get(0).getName().contains("[") && history.size() > 1)) {
            isArray = true;
            objs = (ArrayList<Object>) this.treeInfo.getValueVar(rightVar);
        }

        if (isArray) {
            //is it a[0] = something
            if (leftVarArray) {
                //If the index is a var, which we do not know the value. It will update in all indexes
                if(objs != null) {
                    if (index == -1){ this.treeInfo.updateArrayValueVar(varId, objs); }
                    else { this.treeInfo.updateValueIndexVar(varId, index, objs); }
                }
            }
            else {
                if(!varCreated) {
                    this.treeInfo.newVarArray(varId, objs, history);
                    varCreated = true;
                }
                else {
                    this.treeInfo.addValueHist(objs, varId);
                }
            }
        }
        else {
            //is it a[0] = something
            if (leftVarArray) {
                if (index == -1) { this.treeInfo.updateArrayHistoryVar(varId, history); }
                else { this.treeInfo.updateIndexHistoryVar(varId, index, history); }
            }
            else {
                if (!history.isEmpty()) { varCreated = this.assignVar(varCreated, history.get(0), varId); }
                for (int i = 1; i < history.size(); i++) {
                    this.treeInfo.addInstructionVar(varId, history.get(i));
                }
            }
        }
        return varCreated;
    }

    public boolean assignVar(boolean varCreated, Instruction inst, String varId) {
        if (!varCreated) {
            this.treeInfo.newVar(varId, inst);
            varCreated = true;
        }
        else {
            this.treeInfo.addInstructionVar(varId, inst);
        }
        return varCreated;
    }


    /**
     *******    Process Function Methods   ******
     */
    public HashMap<String, ArrayList<Instruction>> processFunction(ArrayList<String> parameters, int nodeID) throws Exception {
        ArrayList<ArrayList<Object>> valueFunc = new ArrayList<ArrayList<Object>>();
        ArrayList<String> params = new ArrayList<String>();
        HashMap<String, ArrayList<Instruction>> historyFuncParams = this.processParams(parameters, valueFunc, params);
        String functionName = this.treeInfo.getFuncName(nodeID);
        //It needs to be here before, because some functions we just want to delete it completely
        Instruction instruction = new Instruction(Tag.FUNCTION, functionName);
        historyFuncParams.put(Integer.toString(this.funcCount), new ArrayList<Instruction>(Arrays.asList(instruction)));
        StringBuilder funcNameAux = new StringBuilder();
        ProcessWebApp processWebApp = ProcessWebApp.getInstance();
        this.treeInfo = processWebApp.additionalProcessFunc(functionName, params, nodeID, historyFuncParams, valueFunc , this.treeInfo, funcNameAux);
        if(!(funcNameAux.length() == 0)) { functionName = funcNameAux.toString(); }
        this.extraProcessingFunc(nodeID, functionName, params, historyFuncParams, valueFunc);
        this.processInterpro(functionName, nodeID, params, historyFuncParams);
        return historyFuncParams;
    }

    public HashMap<String, ArrayList<Instruction>> processParams(ArrayList<String> parameters, ArrayList<ArrayList<Object>> valueFunc,
                                                                 ArrayList<String> params) throws Exception {
        HashMap<String, ArrayList<Instruction>> historyFuncParams = new HashMap<String, ArrayList<Instruction>>();
        ArrayList<Instruction> historyFunc = new ArrayList<Instruction>();
        String parameter = "";
        String subParam = "";
        int i = 0;
        boolean cast = false;
        while(!subParam.equals(")") && !cast && (i < parameters.size())) {
            subParam = parameters.get(i);
            if((subParam.equals(",") || subParam.equals(" "))) {
                if(!parameter.equals("")) {
                    //get parameter history
                    ArrayList<Instruction> paramHistory = this.treeInfo.getHistoryVar(parameter);
                    if (paramHistory != null) {
                        historyFuncParams.put(parameter, paramHistory);
                        historyFunc.addAll(paramHistory);
                        params.add(parameter);
                        //In case the parameter is an array
                        if (paramHistory.get(0).getTag() == Tag.ARRAY) {
                            valueFunc = (ArrayList<ArrayList<Object>>) this.treeInfo.getValueVar(parameter);
                        }
                    }
                    parameter = "";
                }
            }
            else if((this.tokenizer.getListOperators().contains(subParam))) {
                if(!parameter.equals("")) {
                    //get parameter history
                    ArrayList<Instruction> paramHistory = this.treeInfo.getHistoryVar(parameter);
                    if (paramHistory != null) {
                        params.add(parameter);
                        historyFunc.addAll(paramHistory);
                        historyFuncParams.put(parameter, paramHistory);
                    }
                    parameter = "";
                }
                Instruction instruction;
                if(!subParam.equals("instanceof")) { instruction = new Instruction(Tag.OPERATION); }
                else { instruction = new Instruction(Tag.OPERATION, "instanceof"); }
                if (!historyFunc.contains(instruction)) {
                    historyFunc.add(instruction);
                    historyFuncParams.put(Integer.toString(this.funcCount), new ArrayList<Instruction>(Arrays.asList(instruction)));
                    this.funcCount++;
                }
            }
            else if(subParam.equals("(")) {
                if (parameter.equals("")) {
                    Instruction instruction = new Instruction(Tag.CAST);
                    historyFunc.add(instruction);
                    historyFuncParams.put(Integer.toString(this.funcCount), new ArrayList<Instruction>(Arrays.asList(instruction)));
                    this.funcCount++;
                    cast = true;
                }

            }
            else if(this.tokenizer.isConstant(subParam)) {
                Instruction instruction = new Instruction(Tag.CONSTANT, subParam);
                params.add(subParam);
                if (!historyFunc.contains(instruction)) {
                    historyFunc.add(instruction);
                    historyFuncParams.put(Integer.toString(this.funcCount), new ArrayList<Instruction>(Arrays.asList(instruction)));
                    this.funcCount++;
                }
            }
            else if (!subParam.equals(")")){
                parameter += subParam;
            }
            if (subParam.equals(")") && cast) {
                cast = false;
            }
            i++;
        }
        if (!parameter.equals("")) {
            ArrayList<Instruction> paramHistory = this.treeInfo.getHistoryVar(parameter);
            if (paramHistory != null) {
                params.add(parameter);
                historyFuncParams.put(parameter, paramHistory);
                if (paramHistory.get(0).getTag() == Tag.ARRAY) {
                    ArrayList<ArrayList<Object>> auxValue = (ArrayList<ArrayList<Object>>) this.treeInfo.getValueVar(parameter);
                    if(auxValue != null) {
                        valueFunc.addAll(auxValue);
                    }
                }
            }
        }
        return historyFuncParams;
    }

    public void extraProcessingFunc(int nodeID, String functionName, ArrayList<String> params, HashMap<String, ArrayList<Instruction>> historyFuncParams,
                                    ArrayList<ArrayList<Object>> valueFunc) throws Exception {
        String vulnerabilityName = this.treeInfo.vulnerabilityName(functionName);
        if (!vulnerabilityName.equals("none")) {
            this.processSensitiveSinks(functionName, historyFuncParams, nodeID);
        }
        else if(this.abstractListFuncs.contains(functionName)) {
            this.processAbstractList(functionName, nodeID, params, historyFuncParams, valueFunc);
        }
        else if(this.hashmapFuncs.contains(functionName)) {
            this.processingHashMaps(functionName, nodeID, params, historyFuncParams, valueFunc);
        }
        else if(functionName.equals("java.util.ArrayList.iterator") || functionName.equals("java.util.Vector.iterator") ||
                functionName.equals("java.util.Stack.iterator") || functionName.equals("java.util.Set.iterator") ||
                functionName.equals("java.util.LinkedList.iterator")) {
            String varName = this.treeInfo.getVarFunc(nodeID);
            ArrayList<Instruction> instsVar = this.treeInfo.getHistoryVar(varName);
            if(instsVar != null) { historyFuncParams.put(varName, instsVar); }
        }
        else if(functionName.equals("java.util.Iterator.next")) {
            String varName = this.treeInfo.getVarFunc(nodeID);
            ArrayList<Instruction> instructions = this.treeInfo.getHistoryArrayVar(varName);
            if(!instructions.isEmpty()) { historyFuncParams.put("-1", instructions); }
            else {
                instructions = this.treeInfo.getHistoryVar(varName);
                if(instructions != null) { historyFuncParams.put("-1", instructions); }
            }
        }
        else if(this.methodsFuncs.contains(functionName)) {
            String varFuncAppend = this.treeInfo.getVarFunc(nodeID);
            /**                     Debug
            System.out.println("-------------------\nAditional func:: " + this.treeInfo.getFuncName(nodeID));
            System.out.println("NodeID:: " + nodeID);
            System.out.println("VarAppend:: " + varFuncAppend);
             **/
            ArrayList<Instruction> varFuncHistory = this.treeInfo.getHistoryVar(varFuncAppend);
            if(varFuncHistory != null) { historyFuncParams.put(varFuncAppend, varFuncHistory); }
        }
        else if(functionName.equals("java.lang.StringBuilder.insert") || functionName.equals("java.lang.StringBuffer.insert")) {
            String varFuncAppend = this.treeInfo.getVarFunc(nodeID);
            ArrayList<Instruction> varFuncHistory = this.treeInfo.getHistoryVar(varFuncAppend);
            historyFuncParams.put(varFuncAppend, varFuncHistory);
            String nameLastArg = params.get(1);
            if(!this.tokenizer.isConstant(nameLastArg)) {
                if(historyFuncParams.get(nameLastArg).get(0).getTag() == Tag.ARRAY) {
                    ArrayList<Instruction> storyParams = this.processRealParamsVal(nameLastArg);
                    historyFuncParams.put(nameLastArg, storyParams);
                }
            }
        }
        else if(functionName.equals("java.lang.String.format") || functionName.equals("java.lang.String.join") ||
                functionName.equals("org.apache.commons.lang3.StringUtils.appendIfMissing") ||
                functionName.equals("org.apache.commons.lang3.StringUtils.appendIfMissingIgnoreCase") ||
                functionName.equals("org.apache.commons.lang3.StringUtils.prependIfMissing") ||
                functionName.equals("org.apache.commons.lang3.StringUtils.prependIfMissingIgnoreCase")) {
            String args = params.get(params.size()-1);
            historyFuncParams.remove(args);
            ArrayList<Instruction> storyParams = this.processRealParamsVal(args);
            historyFuncParams.put(args, storyParams);
        }
        else if(functionName.equals("org.apache.commons.lang3.StringUtils.join") ||
                functionName.equals("org.apache.commons.lang.StringUtils.join")) {
            String args = params.get(0);
            ArrayList<Instruction> storyParams = this.processRealParamsVal(args);
            historyFuncParams.put(args, storyParams);
        }
        else if(functionName.equals("org.apache.commons.lang3.StringUtils.replaceEach") ||
                functionName.equals("org.apache.commons.lang3.StringUtils.replaceEachRepeatedly") ||
                functionName.equals("org.apache.commons.lang.StringUtils.replaceEach") ||
                functionName.equals("org.apache.commons.lang.StringUtils.replaceEachRepeatedly")) {
            String arg = params.get(1);
            historyFuncParams.remove(arg);
            arg = params.get(2);
            historyFuncParams.remove(arg);
        }
        else if(functionName.equals("java.lang.System.exit")) {
            Instruction exitInst = new Instruction(Tag.FUNCTION, "java.lang.System.exit");
            this.treeInfo.addExitInstContext(exitInst);
        }
        else if(functionName.endsWith(".clone")) {
            String varName = this.treeInfo.getVarFunc(nodeID);
            ArrayList<Instruction> instructions = this.treeInfo.getHistoryVar(varName);
            historyFuncParams.clear();
            historyFuncParams.put(varName, instructions);
        }
        else if(functionName.contains(".<init>")) {
            this.processInitMethod(nodeID, functionName, params, historyFuncParams);
        }
        else if(functionName.equals("java.io.ObjectOutput.writeObject")) {
            String varName = this.treeInfo.getVarFunc(nodeID);
            ArrayList<Instruction> instructions = this.treeInfo.getHistoryVar(varName);
            if(!instructions.isEmpty()) {
                if(instructions.get(0).getTag() == Tag.OBJECT) {
                    String mirrorVar = instructions.get(0).getName();
                    if(this.treeInfo.getVars().containsKey(mirrorVar)) {
                        for(String key : historyFuncParams.keySet()) {
                            for(Instruction inst : historyFuncParams.get(key)) {
                                this.treeInfo.addInstructionVar(mirrorVar, inst);
                            }
                        }
                    }
                }
            }
        }
        if(this.treeInfo.isSource(functionName)) {
            String parameterName = "";
            for(String key : historyFuncParams.keySet()) {
                for(Instruction inst : historyFuncParams.get(key)) {
                    if(inst.getTag() != Tag.FUNCTION) {
                        parameterName += inst.getName();
                    }
                }
            }
            historyFuncParams.clear();
            String sourceFullname = functionName + "(" + parameterName + ")";
            Instruction newInst = new Instruction(Tag.SOURCE, sourceFullname);
            historyFuncParams.put(Integer.toString(this.funcCount), new ArrayList<Instruction>(Arrays.asList(newInst)));
        }
    }

    public void processSensitiveSinks(String functionName, HashMap<String, ArrayList<Instruction>> historyFuncParams, int nodeID)
            throws Exception {
        ArrayList<Instruction> potCodeVuln = new ArrayList<Instruction>();
        boolean hasSource = false;
        ArrayList<Instruction> sourceInsts = new ArrayList<Instruction>();
        for(String k : historyFuncParams.keySet()) {
            for (Instruction i : historyFuncParams.get(k)) {
                if(!(i.getName().equals(functionName) && i.getTag() == Tag.FUNCTION)) {
                    potCodeVuln.add(i);
                }
                if(i.getTag() == Tag.SOURCE) {
                    hasSource = true;
                    sourceInsts.add(i);
                }
            }
        }
        //It will cause duplicated code but it is not worrying since we will check whether the attribute is present or not
        //instead of counting occurrences of attributes
        if(hasSource) {
            ArrayList<Instruction> lastIfVars = this.treeInfo.getLastVarsIf();
            if(this.treeInfo.containsSources(lastIfVars, sourceInsts)) {
                for(Instruction inst : lastIfVars) {
                    potCodeVuln.add(inst);
                }
            }
        }
        Instruction instructionAux = new Instruction(Tag.FUNCTION, functionName);
        potCodeVuln.add(instructionAux);
        this.treeInfo.addPotentiallyVulnCode(potCodeVuln, nodeID);
    }

    /**
     *******    Processing Abstract List Methods   ******
     */
    public HashMap<String, ArrayList<Instruction>> processAbstractList(String functionName, int nodeID, ArrayList<String> params, HashMap<String, ArrayList<Instruction>> historyFuncParams,
                                    ArrayList<ArrayList<Object>> valueFunc) throws Exception {
        if(functionName.equals("java.util.ArrayList.add") || functionName.equals("java.util.Vector.add") ||
           functionName.equals("java.util.Stack.add") || functionName.equals("java.util.LinkedList.add")) {
            String varName = this.treeInfo.getVarFunc(nodeID);
            Instruction insAux = new Instruction(Tag.FUNCTION, functionName);
            historyFuncParams.values().remove(new ArrayList<Instruction>(Arrays.asList(insAux)));
            //normal add to the last
            int index = -2;
            ArrayList<Object> histAux = new ArrayList<Object>();
            if(params.size() == 2) {
                String stringIndex = params.get(0);
                index = -1;
                if (StringUtils.isNumeric(stringIndex)) {
                    index = Integer.parseInt(stringIndex);
                }
            }
            histAux = this.extraProcessingValues(valueFunc, historyFuncParams, histAux);
            if(index == -2) {
                treeInfo.addValueHist(histAux, varName);
            }
            else if(index == -1) {
                treeInfo.updateArrayValueVar(varName, histAux);
                treeInfo.addValueHist(histAux, varName);
            }
            else {
                if(functionName.equals("java.util.LinkedList.add")) {
                    int lastIndex = this.treeInfo.lastIndexArray(varName);
                    if(index > lastIndex) {
                        for(int k = lastIndex; k <= index; k++) {
                            this.treeInfo.addValueHist(new ArrayList<Object>(), varName);
                        }
                    }
                }
                treeInfo.addValueHist(histAux, varName, index);
                ArrayList<Object> valHist = treeInfo.getValueVarIndex(varName, index);
                for(Object a : valHist) {
                    if(a instanceof Instruction) {
                        Instruction i = (Instruction) a;
                    }
                }
            }
        }
        else if(functionName.equals("java.util.ArrayList.get") || functionName.equals("java.util.Vector.get") ||
                functionName.equals("java.util.Stack.get") || functionName.equals("java.util.LinkedList.get")) {
            String varName = this.treeInfo.getVarFunc(nodeID);
            if(!params.isEmpty()) {
                String index = params.get(0);
                historyFuncParams.clear();
                ArrayList<Instruction> instructions = new ArrayList<Instruction>();
                instructions.add(new Instruction(Tag.ARRAY));
                String newVar = varName + "[" + index + "]";
                historyFuncParams.put(newVar, instructions);
            }
        }
        else if(functionName.equals("java.util.ArrayList.clear") || functionName.equals("java.util.Vector.clear") ||
                functionName.equals("java.util.Stack.clear") || functionName.equals("java.util.LinkedList.clear")) {
            String varName = this.treeInfo.getVarFunc(nodeID);
            this.treeInfo.clearHist(varName);
        }
        else if(functionName.equals("java.util.ArrayList.remove") || functionName.equals("java.util.Vector.remove") ||
                functionName.equals("java.util.Stack.remove") || functionName.equals("java.util.LinkedList.remove")) {
            String varName = this.treeInfo.getVarFunc(nodeID);
            if(!params.isEmpty()) {
                String stringIndex = params.get(0);
                ArrayList<Instruction> removedValue = new ArrayList<Instruction>();
                if (StringUtils.isNumeric(stringIndex)) {
                    int index = Integer.parseInt(stringIndex);
                    removedValue = this.treeInfo.removeHist(varName, index);
                }
                if(!removedValue.isEmpty()) { historyFuncParams.put("-1", removedValue); }
                else {
                    ArrayList<Instruction> instructions = new ArrayList<Instruction>();
                    instructions.add(new Instruction(Tag.ARRAY));
                    String newVar = varName + "[" + stringIndex + "]";
                    historyFuncParams.put(newVar, instructions);
                }
            }
        }
        else if(functionName.equals("java.util.ArrayList.set") || functionName.equals("java.util.Vector.set") ||
                functionName.equals("java.util.Stack.set") || functionName.equals("java.util.LinkedList.set")) {
            String varName = this.treeInfo.getVarFunc(nodeID);
            if(!params.isEmpty()) {
                String stringIndex = params.get(0);
                if (StringUtils.isNumeric(stringIndex)) {
                    int index = Integer.parseInt(stringIndex);
                    ArrayList<Object> histAux = new ArrayList<Object>();
                    histAux = this.extraProcessingValues(valueFunc, historyFuncParams, histAux);
                    ArrayList<Instruction> oldValue = this.treeInfo.setHist(varName, index, histAux);
                    historyFuncParams.put("-1", oldValue);
                }
            }
        }
        else if(functionName.equals("java.util.Vector.lastElement") || functionName.equals("java.util.Stack.lastElement")) {
            String varName = this.treeInfo.getVarFunc(nodeID);
            int index = this.treeInfo.lastIndexArray(varName);
            if(index != -1) {
                historyFuncParams.clear();
                ArrayList<Instruction> instructions = new ArrayList<Instruction>();
                instructions.add(new Instruction(Tag.ARRAY));
                String newVar = varName + "[" + index + "]";
                historyFuncParams.put(newVar, instructions);
            }
        }
        else if(functionName.equals("java.util.Vector.push") || functionName.equals("java.util.Stack.push")) {
            String varName = this.treeInfo.getVarFunc(nodeID);
            ArrayList<Object> histAux = new ArrayList<Object>();
            histAux = this.extraProcessingValues(valueFunc, historyFuncParams, histAux);
            treeInfo.addValueHist(histAux, varName);
        }
        else if(functionName.equals("java.util.Vector.pop") || functionName.equals("java.util.Stack.pop")) {
            String varName = this.treeInfo.getVarFunc(nodeID);
            int index = this.treeInfo.lastIndexArray(varName);
            if(index != -1) {
                ArrayList<Instruction> removedValue = this.treeInfo.removeHist(varName, index);
                historyFuncParams.put("-1", removedValue);
            }
        }
        return historyFuncParams;
    }

    public ArrayList<Object> extraProcessingValues(ArrayList<ArrayList<Object>> valueFunc, HashMap<String, ArrayList<Instruction>> historyFuncParams,
                                                   ArrayList<Object> histAux) throws Exception {
        if(valueFunc.isEmpty()) {
            for (String var : historyFuncParams.keySet()) {
                for (Instruction i : historyFuncParams.get(var)) {
                    histAux.add(i);
                }
            }
        }
        else {
            for (ArrayList<Object> val : valueFunc) {
                Object o = val;
                histAux.add(val);
            }
        }
        return histAux;
    }

    /**
     *******    Processing HashMap Methods   ******
     */
    public HashMap<String, ArrayList<Instruction>> processingHashMaps(String functionName, int nodeID, ArrayList<String> params,
                                                                      HashMap<String, ArrayList<Instruction>> historyFuncParams,
                                                                      ArrayList<ArrayList<Object>> valueFunc) throws Exception {
        if(functionName.equals("java.util.HashMap.put")) {
            String varName = this.treeInfo.getVarFunc(nodeID);
            if(!params.isEmpty()) {
                String key = params.get(0);
                ArrayList<Object> histAux = new ArrayList<Object>();
                int index = this.extraProcessingHashMaps(varName, key, historyFuncParams, histAux, valueFunc);
                if(index == -1) {
                    this.treeInfo.addInstructionVar(varName, new Instruction(Tag.CONSTANT, key));
                    this.treeInfo.addValueHist(histAux, varName);
                }
                //Check if something is not null
                else if(index != -2){
                    this.treeInfo.setHist(varName, index - 1, histAux);
                }
            }
        }
        else if(functionName.equals("java.util.HashMap.get")) {
            String varName = this.treeInfo.getVarFunc(nodeID);
            if(!params.isEmpty()) {
                String key = params.get(0);
                int index = this.extraProcessingHashMaps(varName, key, historyFuncParams, new ArrayList<Object>(), valueFunc);
                //Something is null
                if(index != -2) {
                    index--;
                    historyFuncParams.clear();
                    ArrayList<Instruction> instructions = new ArrayList<Instruction>();
                    instructions.add(new Instruction(Tag.ARRAY));
                    String newVar = varName + "[" + index + "]";
                    historyFuncParams.put(newVar, instructions);
                }
            }
        }
        else if(functionName.equals("java.util.HashMap.replace")) {
            String varName = this.treeInfo.getVarFunc(nodeID);
            if(!params.isEmpty()) {
                String key = params.get(0);
                ArrayList<Object> histAux = new ArrayList<Object>();
                int index = this.extraProcessingHashMaps(varName, key, historyFuncParams, histAux, valueFunc);
                //it is not null and it is not unknown
                if(!(index == -1 || index == -2)) {
                    ArrayList<Instruction> oldValue = this.treeInfo.setHist(varName, index - 1 , histAux);
                    historyFuncParams.put("-1", oldValue);
                }
            }
        }
        else if(functionName.equals("java.util.HashMap.ketSet")) {
            String varName = this.treeInfo.getVarFunc(nodeID);
            ArrayList<Instruction> keysNameInst = this.treeInfo.getHistoryVar(varName);
            ArrayList<Instruction> keysInsts = new ArrayList<Instruction>();
            for(Instruction inst : keysNameInst) {
                if(inst.getTag() == Tag.CONSTANT) {
                    String key = inst.getName();
                    if(this.tokenizer.isConstant(key)) {
                        Instruction instConstant = new Instruction(Tag.CONSTANT, key);
                        keysInsts.add(instConstant);
                    }
                    else {
                        ArrayList<Instruction> keyInstructions = this.treeInfo.getHistoryVar(varName);
                        if(keyInstructions != null) {
                            for(Instruction keyInstruction : keyInstructions) {
                                keysInsts.add(keyInstruction);
                            }
                        }
                    }
                }
            }
            historyFuncParams.put("-1", keysInsts);
        }
        else if(functionName.equals("java.util.HashMap.remove")) {
            String varName = this.treeInfo.getVarFunc(nodeID);
            if(!params.isEmpty()) {
                String key = params.get(0);
                ArrayList<Object> histAux = new ArrayList<Object>();
                int index = this.extraProcessingHashMaps(varName, key, historyFuncParams, histAux, valueFunc);
                //it is not null and it is not unknown
                if (!(index == -1 || index == -2)) {
                    this.treeInfo.removeInstructionVar(varName, index);
                    ArrayList<Instruction> removedValue = this.treeInfo.removeHist(varName, index - 1);
                    historyFuncParams.put("-1", removedValue);
                }
            }
        }
        return historyFuncParams;
    }

    public int extraProcessingHashMaps(String varName, String key, HashMap<String, ArrayList<Instruction>> historyFuncParams,
                                       ArrayList<Object> histAux, ArrayList<ArrayList<Object>> valueFunc) throws Exception {
        ArrayList<Instruction> keyInst = historyFuncParams.get(key);
        if(keyInst == null) { keyInst = new ArrayList<Instruction>(Arrays.asList(new Instruction(Tag.CONSTANT, key))); }
        else { historyFuncParams.remove(key); }
        histAux = this.extraProcessingValues(valueFunc, historyFuncParams, histAux);
        ArrayList<Instruction> keySetName = this.treeInfo.getHistoryVar(varName);
        int index = -1;
        if(keySetName != null) {
            for (int i = 0; i < keySetName.size(); i++) {
                if (keySetName.get(i).getTag() == Tag.CONSTANT) {
                    if (this.tokenizer.isConstant(keySetName.get(i).getName())) {
                        if (keyInst.equals(new ArrayList<Instruction>(Arrays.asList(keySetName.get(i))))) {
                            index = i;
                            break;
                        }
                    } else {
                        ArrayList<Instruction> keyInstVar = this.treeInfo.getHistoryVar(keySetName.get(i).getName());
                        if (keyInstVar != null) {
                            if (keyInst.equals(keyInstVar)) {
                                index = i;
                                break;
                            }
                        }
                    }
                }
            }
            return index;
        }
        else {
            return -2;
        }
    }

    public void processInitMethod(int nodeID, String functionName, ArrayList<String> params,
                                  HashMap<String, ArrayList<Instruction>> historyFuncParams) throws Exception{
        if(functionName.equals("java.lang.StringBuilder.<init>") || functionName.equals("java.lang.StringBuffer.<init>") ||
                functionName.equals("java.lang.String.<init>")  || functionName.equals("java.lang.Integer.<init>")) {
            String varName = this.treeInfo.getVarFunc(nodeID);
            for (String key : historyFuncParams.keySet()) {
                for (Instruction inst : historyFuncParams.get(key)) {
                    if (!inst.getName().equals(functionName)) {
                        treeInfo.addInstructionVar(varName, inst);
                    }
                }
            }
        }
        else if(functionName.equals("java.util.ArrayList.<init>") || functionName.equals("java.util.Vector.<init>") ||
                functionName.equals("java.util.Stack.<init>") || functionName.equals("java.util.HashMap.<init>") ||
                functionName.equals("java.util.LinkedList.<init>")) {
            String varName = this.treeInfo.getVarFunc(nodeID);
            Instruction arrayInstruction = new Instruction(Tag.ARRAY);
            this.treeInfo.newVar(varName, arrayInstruction);
            if(params.size() == 1) {
                String stringIndex = params.get(0);
                if (StringUtils.isNumeric(stringIndex)) {
                    int index = Integer.parseInt(stringIndex);
                    for(int i = 0; i <= index; i++) {
                        this.treeInfo.addValueHist(new ArrayList<Object>(), varName);
                    }
                }
            }
        }
        else if(functionName.equals("java.io.ObjectOutputStream.<init>")) {
            if(params.size() > 0) {
                String varName = this.treeInfo.getVarFunc(nodeID);
                String paramMirror = params.get(0);
                this.treeInfo.newVar(varName, new Instruction(Tag.OBJECT, paramMirror));
            }
        }
        else if(functionName.endsWith("<init>")) {
            String varName = this.treeInfo.getVarFunc(nodeID);
            ArrayList<Instruction> objInstructions = this.treeInfo.getHistoryVar(varName);
            String[] partsFunctionName = functionName.split(".<init>");
            String possSensitiveSink = partsFunctionName[0];
            String vulnerabilityName = this.treeInfo.vulnerabilityName(possSensitiveSink);
            if (!vulnerabilityName.equals("none")) {
                this.processSensitiveSinks(possSensitiveSink, historyFuncParams, nodeID);
            }
            else if(objInstructions != null) {
                if(objInstructions.get(0).getTag() == Tag.OBJECT) {
                    ArrayList<String> keySource = new ArrayList<String>();
                    for(String key : historyFuncParams.keySet()) {
                        for(Instruction inst : historyFuncParams.get(key)) {
                            if(inst.getTag() == Tag.SOURCE || inst.getTag() == Tag.PARAMETER) {
                                keySource.add(key);
                            }
                        }
                    }
                    if(!keySource.isEmpty()) {
                        boolean createdVar = false;
                        for(String keyVar : keySource) {
                            for(Instruction inst : historyFuncParams.get(keyVar)) {
                                if(createdVar) { this.treeInfo.addInstructionVar(varName, inst); }
                                else {
                                    this.treeInfo.newVar(varName, inst);
                                    createdVar = true;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public ArrayList<Instruction> processRealParamsVal(String varName) throws Exception {
        ArrayList<ArrayList<Object>> params = (ArrayList<ArrayList<Object>>) this.treeInfo.getValueVar(varName);
        ArrayList<Instruction> storyParams = new ArrayList<Instruction>();
        for (ArrayList<Object> singleParam : params) {
            for (Object par : singleParam) {
                if (par instanceof Instruction) {
                    Instruction inst = (Instruction) par;
                    storyParams.add(inst);
                }
            }
        }
        return storyParams;
    }

    public String getFullCfgFilename(String dir, String cfgFilename) {
        File dirFile = new File(dir);
        String[] files = dirFile.list();
        String fullFilename = cfgFilename;
        if(files != null) {
            for(String file : files) {
                String[] pFile = file.split(" ");
                if(pFile.length > 1) {
                    if(pFile[1].endsWith(cfgFilename)) {
                        fullFilename = file;
                        break;
                    }
                }
            }
        }
        return dir + fullFilename;
    }

    /**Interprocedural Processing**/
    public void processInterpro(String functionName, int nodeID, ArrayList<String> params, HashMap<String,
            ArrayList<Instruction>> historyFuncParams) throws Exception {
        ProcessWebApp processWebApp = ProcessWebApp.getInstance();
        String className = functionName;
        String classNameDots = functionName;
        String realFuncName = functionName;
        if(functionName.contains(".")) {
            int p = functionName.lastIndexOf(".");
            //com.sample.GetName.doPost -> com.sample.GetName
            classNameDots = functionName.substring(0, p);
            //com.sample.GetName -> com\sample\GetName
            className = classNameDots.replace(".", "\\");
            realFuncName = functionName.substring(p+1);
        }
        //com\sample\GetName -> com\sample\GetName.java
        String ext = processWebApp.getFileType();
        className = className + "." + ext;
        String fullClassName = processWebApp.containsFileToProcess(className);
        if(!fullClassName.equals("")) {
            //doPost(java.lang.String)
            String functionID = realFuncName + "(" + this.treeInfo.getParamsFunc(nodeID) + ")";
            functionID = functionID.replace("<", "[");
            functionID = functionID.replace(">", "]");
            //com\sample\GetName.java -> com\sample\GetName.java\doPost(java.lang.String)
            String fullFuncName = fullClassName + "\\" + functionID;
            //check if it is not a recursive call
            String currentFuncId = this.treeInfo.getFuncSummary().getId();
            if(!fullFuncName.equals(currentFuncId)) {
                //check if the function was allready processed
                boolean processParam = true;
                boolean realTimeProc = false;
                int j = classNameDots.lastIndexOf(".");
                ArrayList<Var> paramsList = this.treeInfo.getVarParams(params);
                //com.sample.GetName -> GetName or result ->result
                String javaName = (j == -1) ? classNameDots : classNameDots.substring(j+1);
                if(!processWebApp.containsFuncSummary(fullFuncName)) {
                    String baseDir = processWebApp.getBaseDir();
                    //d:"something"\com\sample\GetName.java\\sootOutput\\GetName.jimple
                    String jimpFileName = baseDir + "\\" + fullClassName + "\\sootOutput\\" + javaName + ".jimple";
                    File jimpFile = new File(jimpFileName);
                    //d:"something"\com\sample\GetName.java\\sootOutput\\GetName\\doPost(java.lang.String).dot
                    String stripWhiteSpace = this.treeInfo.getParamsFunc(nodeID).replace(" ", "");
                    String cfgFileName;
                    if(realFuncName.equals("<init>")) {
                        cfgFileName = this.getFullCfgFilename(baseDir + "\\" + fullClassName + "\\sootOutput\\" + javaName + "\\", "[init](" + stripWhiteSpace + ").dot");
                    }
                    else {
                        cfgFileName = this.getFullCfgFilename(baseDir + "\\" + fullClassName + "\\sootOutput\\" + javaName + "\\", realFuncName + "(" + stripWhiteSpace + ").dot");
                    }
                    File cfgFile = new File(cfgFileName);
                    if(jimpFile.exists() && cfgFile.exists()) {
                        ProcessFuncInfo processFuncInfo = new ProcessFuncInfo(jimpFileName);
                        HashMap<String, ArrayList<JimpFuncInfo>> funcsNames = processFuncInfo.process();
                        String realFuncId = functionID.replace(",", ", ");
                        ArrayList<JimpFuncInfo> funcNames = funcsNames.get(realFuncId);
                        ArrayList<Var> processedParamsList = processWebApp.processSendParamList((ArrayList<Var>) paramsList.clone(), this.treeInfo.getParamVars());
                        GraphProcessing graphProcessing = new GraphProcessing(cfgFileName, funcNames, fullFuncName, processedParamsList);
                        graphProcessing.processing();
                        realTimeProc = true;
                    }
                    else { processParam = false; }
                }
                if(processParam && !realFuncName.equals("<init>")) {
                    String varName = this.treeInfo.getVarFunc(nodeID);
                    ArrayList<Instruction> objInst = this.treeInfo.getHistoryVar(varName);
                    ArrayList<Instruction> resultVarObj = this.treeInfo.getHistoryVar(varName);
                    int lastIndexBackslashes = currentFuncId.lastIndexOf("\\");
                    currentFuncId = currentFuncId.substring(0, lastIndexBackslashes);
                    ArrayList<Integer> paramPot = (currentFuncId.equals(fullClassName)) ? processWebApp.processFuncSummaryParams(fullFuncName, paramsList, objInst, this.treeInfo.getAttVars()):
                                                    processWebApp.processFuncSummaryParams(fullFuncName, paramsList, objInst, new HashMap<String, Var>());
                    for(Integer indexPot : paramPot) {
                        processWebApp.replacePotVuln(fullFuncName, this.treeInfo.getParamVars(), indexPot);
                    }
                    if(!realTimeProc) {
                        processWebApp.processUserDefFunc(fullFuncName, paramsList, objInst);
                    }
                    historyFuncParams.clear();
                    Var var = (currentFuncId.equals(fullClassName)) ? processWebApp.returnVarFuncSummary(fullFuncName, paramsList, objInst, this.treeInfo.getAttVars()) :
                                processWebApp.returnVarFuncSummary(fullFuncName, paramsList, objInst, new HashMap<String, Var>());
                    ArrayList<Var> varProcessed = processWebApp.processSendParamList(new ArrayList<Var>(Arrays.asList(var)), this.treeInfo.getParamVars());
                    var = varProcessed.get(0);
                    ArrayList<Instruction> varInsts = var.getInstructions();
                    historyFuncParams.put("0temp", varInsts);
                    if(!varInsts.isEmpty()) {
                        if(varInsts.get(0).getTag() == Tag.ARRAY) {
                            this.treeInfo.newVar("0temp", varInsts.get(0));
                            this.treeInfo.setValueVar((ArrayList<ArrayList<Object>>) var.getValue(), "0temp");
                        }
                    }
                }
                else {
                    processWebApp.setAuxObjParam(fullFuncName, paramsList);
                }
                String varName = this.treeInfo.getVarFunc(nodeID);
                ArrayList<Instruction> varNameInsts = this.treeInfo.getHistoryVar(varName);
                if(varNameInsts != null) {
                    if(!varNameInsts.isEmpty()) {
                        if(varNameInsts.get(0).getTag() == Tag.OBJECT && !varNameInsts.get(0).getName().equals("")) {
                            String varFunction = varNameInsts.get(0).getName() + "." +  realFuncName;
                            if(!varFunction.equals(functionName)) {
                                this.processInterpro(varFunction, nodeID, params, historyFuncParams);
                            }
                        }
                    }
                }
            }
        }
    }


}
