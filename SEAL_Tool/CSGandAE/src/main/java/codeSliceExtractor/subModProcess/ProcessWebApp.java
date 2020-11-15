package codeSliceExtractor.subModProcess;

import codeSliceExtractor.VulnerabilityInfo;
import codeSliceExtractor.cfg.GraphProcessing;
import codeSliceExtractor.processJimple.codeData.FunctionSummary;
import codeSliceExtractor.processJimple.codeData.Instruction;
import codeSliceExtractor.processJimple.codeData.Tag;
import codeSliceExtractor.processJimple.codeData.Var;
import codeSliceExtractor.processJimple.processFuncs.JimpFuncInfo;
import codeSliceExtractor.processJimple.processFuncs.ProcessFuncInfo;
import codeSliceExtractor.processJimple.processTrees.TreeInfo;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/** Core Class
 * Extend Class if necessary to create new module for extra processing
 */
public class ProcessWebApp {
    private ArrayList<String> files;
    private String fileType;
    private String baseDir;
    protected static ProcessWebApp single_instance = null;
    private HashMap<String, VulnerabilityInfo> infoVulns;
    private ArrayList<String> noProcessFuncs = new ArrayList<String>();
    private HashMap<String, FunctionSummary> funcSummaries = new HashMap<String, FunctionSummary>();

    protected ProcessWebApp () { }

    public static  ProcessWebApp getInstance() {
        if (single_instance == null) {
            single_instance = new ProcessWebApp();
        }
        return single_instance;
    }

    public void init(ArrayList<String> files, HashMap<String, VulnerabilityInfo> sensitiveSinks, String baseDir) {
        this.infoVulns = sensitiveSinks;
        this.files = files;
        this.baseDir = baseDir;
    }

    public void init(ArrayList<String> files, HashMap<String, VulnerabilityInfo> sensitiveSinks, ArrayList<String> noProcessFuncs, String baseDir) {
        this.files = files;
        this.infoVulns = sensitiveSinks;
        this.noProcessFuncs = noProcessFuncs;
        this.baseDir = baseDir;
    }

    /**
     *******    Getters and Setters   ******
     */
    public String getFileType() { return this.fileType; }

    public ArrayList<String> getNoProcessFuncs () { return this.noProcessFuncs; }

    public HashMap<String, VulnerabilityInfo> getInfoVulns() { return this.infoVulns; }

    public HashMap<String, FunctionSummary> getFuncSummaries() { return this.funcSummaries; }

    public FunctionSummary getFunctionSummary(String funcName) { return this.funcSummaries.get(funcName); }

    public String getBaseDir() { return this.baseDir; }

    public ArrayList<String> getFiles() { return this.files; }

    public void setFileType(String fileType) { this.fileType = fileType; }

    public void setNoProcessFuncs(ArrayList<String> noProcessFuncs) { this.noProcessFuncs = noProcessFuncs; }

    /**
     *******    Function Summary Methods   ******
     */
    public void addFunctionSummary(String funcName, FunctionSummary funcSummary) { this.funcSummaries.put(funcName, funcSummary); }

    public boolean containsFuncSummary(String funcName) { return this.funcSummaries.containsKey(funcName); }

    public String getFullInitFuncSummary(String startInitFunc) {
        for(String funcKey : this.funcSummaries.keySet()) {
            if(funcKey.startsWith(startInitFunc)) {
                return funcKey;
            }
        }
        return "";
    }

    public void processVarThis(String funcName, HashMap<String, Var> vars) {
        FunctionSummary functionSummary = this.funcSummaries.get(funcName);
        functionSummary.processVarThis(vars);
        this.funcSummaries.put(funcName, functionSummary);
    }

    public Var getInstVarThis(String funcName, String attName) {
        if(this.funcSummaries.get(funcName) != null) { return this.funcSummaries.get(funcName).getVarThis(attName); }
        else { return new Var(attName, new Instruction(Tag.OBJECT, "@this")); }
    }

    public ArrayList<Integer> processFuncSummaryParams(String funcName, ArrayList<Var> params, ArrayList<Instruction> objInst,
                                                       HashMap<String, Var> attVars) {
        FunctionSummary funcSum = this.funcSummaries.get(funcName);
        ArrayList<Integer> paramPotPres = funcSum.processParams(params, objInst, attVars);
        this.funcSummaries.put(funcName, funcSum);
        return paramPotPres;
    }

    public void replacePotVuln(String funcName, ArrayList<Var> params, int index) {
        FunctionSummary funcSum = this.funcSummaries.get(funcName);
        funcSum.replaceParamPotVuln(params, index);
        this.funcSummaries.put(funcName, funcSum);
    }

    public void processUserDefFunc(String funcName, ArrayList<Var> params, ArrayList<Instruction> objInst) {
        FunctionSummary funcSum = this.funcSummaries.get(funcName);
        funcSum.processUserDefFunc(params, objInst);
        this.funcSummaries.put(funcName, funcSum);
    }

    public Var returnVarFuncSummary(String funcName, ArrayList<Var> params, ArrayList<Instruction> objInst, HashMap<String, Var> attVars) {
        return this.funcSummaries.get(funcName).getRetVarParam(params, objInst, attVars);
    }

    public void setAuxObjParam(String funcName, ArrayList<Var> params) {
        FunctionSummary funcSum = this.funcSummaries.get(funcName);
        if(funcSum != null) {
            funcSum.setAuxParamObj(params);
            this.funcSummaries.put(funcName, funcSum);
        }
    }

    public HashMap<String, ArrayList<Instruction>> getPottentiallyVulnCodes() {
        HashMap<String,ArrayList<Instruction>> potVulnCodes = new HashMap<String, ArrayList<Instruction>>();
        for(String funcName : this.funcSummaries.keySet()) {
            int version = 0;
            for(int i = 0; i < this.funcSummaries.get(funcName).getPotentiallyVulnCodes().size(); i++) {
                ArrayList<Instruction> instsCodeSlice = this.funcSummaries.get(funcName).getPotentiallyVulnCodes().get(i);
                int indexVulnCode = this.funcSummaries.get(funcName).getPotVulnCodesIndexes().get(i);
                ArrayList<Instruction> codeSlice = new ArrayList<Instruction>();
                for (Instruction inst : instsCodeSlice) {
                    codeSlice.add(inst);
                }
                String newFuncName = funcName + "_" + version + "_" + indexVulnCode;
                potVulnCodes.put(newFuncName, codeSlice);
                version++;
            }
        }
        return potVulnCodes;
    }

    public void updateFunctionSummary(String funcName, FunctionSummary updatedFuncSummary) {
        if(this.funcSummaries.containsKey(funcName)) {
            FunctionSummary auxFuncSummary = this.funcSummaries.get(funcName);
            auxFuncSummary.merge(updatedFuncSummary);
            this.funcSummaries.put(funcName, auxFuncSummary);
        }
        else {
            this.funcSummaries.put(funcName, updatedFuncSummary.clone());
        }
    }

    /**
     * General processing of all files. It can be personalized depending on the tool used to convert to bytecode.
     */
    public void process() {
        ArrayList<String> files = this.files;
        for (String file : files) {
            String filenamesSoot = this.baseDir  + file + "\\sootOutput\\";
            File dirNameJimp = new File(filenamesSoot);
            String[] dirJimpFiles = dirNameJimp.getAbsoluteFile().list();
            if(dirJimpFiles == null) {
                System.out.println("File " + file + " was not processed. Please provide the sootOutput with the jimple code and control flow graph");
            }
            else {
                for(String dirJimpFile : dirJimpFiles) {
                    if(dirJimpFile.endsWith(".jimple")) {
                        String[] pfilenameJimp = dirJimpFile.split("\\.");
                        String filenameJimp = pfilenameJimp[0];
                        String dirCfg = filenamesSoot + filenameJimp;
                        File dirNameCfg = new File(dirCfg);
                        String[] dirCfgFiles = dirNameCfg.getAbsoluteFile().list();
                        ProcessFuncInfo processFuncInfo = new ProcessFuncInfo(filenamesSoot + "\\" + dirJimpFile);
                        HashMap<String, ArrayList<JimpFuncInfo>> funcsNames = processFuncInfo.process();
                        String initFunc = this.getInitKey(funcsNames.keySet());
                        if(!initFunc.equals("")) {
                            String originalInitFunc = initFunc;
                            initFunc = initFunc.replaceFirst("<", "[");
                            initFunc = initFunc.replaceFirst(">", "]");
                            String initCfgFilename = this.processCfgFilename(dirCfgFiles, initFunc, dirCfg);
                            if(!initCfgFilename.equals("")) {
                                ArrayList<JimpFuncInfo> funcNames = funcsNames.get(originalInitFunc);
                                String fullFuncName = file + "\\" + initFunc;
                                fullFuncName = fullFuncName.replace(", ", ",");
                                if(!this.funcSummaries.containsKey(fullFuncName)) {
                                    System.out.println("Starting " + fullFuncName + " processment");
                                    GraphProcessing graphProcessing = new GraphProcessing(initCfgFilename, funcNames, fullFuncName);
                                    graphProcessing.processing();
                                    System.out.println("Ending " + fullFuncName + " processment\n");
                                }
                            }
                        }
                        //check if it exists init - hashmap and file, get init
                        for(String keyFunc : funcsNames.keySet()) {
                            //FullFuncName will have this format: functions\com\sample\GetNameServlet.java\doPost(java.lang.String)
                            String fullFuncName = file + "\\" + keyFunc;
                            if(!this.funcSummaries.containsKey(fullFuncName) && !keyFunc.startsWith("<init>")) {
                                fullFuncName = fullFuncName.replace(", ", ",");
                                fullFuncName = fullFuncName.replace("<", "[");
                                fullFuncName = fullFuncName.replace(">", "]");
                                System.out.println("Starting " + fullFuncName + " processment");
                                String filenameCfg =  this.processCfgFilename(dirCfgFiles, keyFunc, dirCfg);
                                if(!filenameCfg.equals("")) {
                                    ArrayList<JimpFuncInfo> funcNames = funcsNames.get(keyFunc);
                                    GraphProcessing graphProcessing = new GraphProcessing(filenameCfg, funcNames, fullFuncName);
                                    graphProcessing.processing();
                                    System.out.println("Ending " + fullFuncName + " processment\n");
                                }
                                else {
                                    System.out.println("Function " + keyFunc + " from file " + file + " was not processed.");
                                    System.out.println("Please provide the control flow graph for this function (in the folder with the name of the file - " +
                                                        "For instance Example.java->sootOutput->Example.java->cfg_function.dot)");
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.println("\n...........This are the functionSummaries...........");
        for(String s : this.funcSummaries.keySet()) {
            System.out.println("\n  Processed func: " + s);
            for (ArrayList<Instruction> ints : this.funcSummaries.get(s).getPotentiallyVulnCodes()) {
                System.out.println("----- Potentially Vuln code -----");
                for(Instruction i : ints) {
                    i.print();
                }
            }
        }
    }

    /**
     * If it is necessary any additional processing to the function due to the tool used or the way the webapp was designed,
     * use this method to add it.
     * @param funcName - function Name
     * @param nameParams - the parameters names (vars) inside the function
     * @param nodeID -  The ID number of the instruction being processed
     * @param historyInst - The history of the parameters (constant, historyof vars, ...) and the function
     * @param value - If it has an array as parameter and its value
     * @param treeInfo - The information regarding the tree with all its contexts and vars processed
     * @param funcNameAux - If it is necessary to change the function Name to other
     * @return
     */
    public TreeInfo additionalProcessFunc(String funcName, ArrayList<String> nameParams, int nodeID, HashMap<String, ArrayList<Instruction>> historyInst, ArrayList<ArrayList<Object>>  value,
                                          TreeInfo treeInfo, StringBuilder funcNameAux) throws Exception {
                                                return treeInfo;
    }

    /**
     * If it is necessary any additional processing to the assignment due to the tool used or the way the webapp designed,
     * use this method to do it.
     * @param history - The resulting history of the var
     * @param varDep - The var
     * @return
     */
    public ArrayList<Instruction> additionalProcessAss(ArrayList<Instruction> history, String varDep) throws Exception { return history; }

    /**
     *******    Auxilary Methods   ******
     */
    public String containsFileToProcess(String filename) {
        for(String file : this.files) {
            if(file.endsWith(filename)) { return file; }
        }
        return "";
    }

    public String getInitKey(Set<String> funcs) {
        for(String func : funcs) {
            if(func.startsWith("<init>")) {
                return func;
            }
        }
        return "";
    }

    public String processCfgFilename(String[] dirCfgFiles, String keyFunc, String dirCfg) {
        String filenameCfg = "";
        if(dirCfgFiles != null) {
            for(String cfg : dirCfgFiles) {
                String[] cfgNameP1 = cfg.split(" ", 2);
                String cfgNameWext = cfgNameP1[1];
                int p = cfgNameWext.lastIndexOf(".");
                String cfgName = cfgNameWext.substring(0, p);
                String auxKeyFunc = keyFunc.replace(" ", "");
                if(cfgName.equals(auxKeyFunc)) {
                    filenameCfg = dirCfg + "\\" + cfg;
                    break;
                }
            }
        }
        return filenameCfg;
    }

    public ArrayList<Var> processSendParamList(ArrayList<Var> paramsList, ArrayList<Var> paramsTree) {
        ArrayList<Var> processedParamsList = new ArrayList<Var>();
        for(Var paramVar : paramsList) {
            ArrayList<Instruction> newInstructions = new ArrayList<Instruction>();
            boolean changedParam = false;
            for(Instruction singleInst : paramVar.getInstructions()) {
                if(singleInst.getTag() == Tag.PARAMETER) {
                    String[] partsParam = singleInst.getName().split("@parameter");
                    changedParam = true;
                    FunctionSummary fakeFuncSum = new FunctionSummary("fake");
                    ArrayList<Instruction> auxCodeSlice = fakeFuncSum.historyParam(paramsTree, partsParam);
                    for (Instruction varInst : auxCodeSlice) {
                        varInst.print();
                        newInstructions.add(varInst);
                    }
                }
                else if(singleInst.getTag() == Tag.ARRAY) {
                    newInstructions = paramVar.getInstructions();
                    Object oldValueObj = paramVar.getValue();
                    try {
                        if(oldValueObj != null) {
                            ArrayList<ArrayList<Object>> oldValue = (ArrayList<ArrayList<Object>>) oldValueObj;
                            ArrayList<ArrayList<Object>> newValue = new ArrayList<ArrayList<Object>>();
                            boolean notInstruction = false;
                            for(ArrayList<Object> indexOldValue : oldValue) {
                                ArrayList<Object> indexNewValue = new ArrayList<Object>();
                                for(Object possInst : indexOldValue) {
                                    if(possInst instanceof Instruction) {
                                        Instruction singleInstVal = (Instruction) possInst;
                                        if(singleInstVal.getTag() == Tag.PARAMETER) {
                                            changedParam = true;
                                            String[] partsParam = singleInst.getName().split("@parameter");
                                            FunctionSummary fakeFuncSum = new FunctionSummary("fake");
                                            ArrayList<Instruction> auxCodeSlice = fakeFuncSum.historyParam(paramsTree, partsParam);
                                            for (Instruction varInst : auxCodeSlice) {
                                                varInst.print();
                                                indexNewValue.add(varInst);
                                            }
                                        }
                                    }
                                    else { notInstruction = true; }
                                }
                                newValue.add(indexNewValue);
                                if(notInstruction) { break; }
                            }
                            if(changedParam) {
                                paramVar.setValue(newValue);
                            }
                        }
                    }
                    catch (Exception e) { /**do nothing**/ }
                    break;
                }
                else {
                    newInstructions.add(singleInst);
                }
            }
            if(changedParam) {
                paramVar.setInstructions(newInstructions);
            }
            processedParamsList.add(paramVar);
        }
        return processedParamsList;
    }
}
