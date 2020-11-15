package codeSliceExtractor.subModProcess;

import codeSliceExtractor.VulnerabilityInfo;
import codeSliceExtractor.cfg.GraphProcessing;
import codeSliceExtractor.processJimple.codeData.Instruction;
import codeSliceExtractor.processJimple.codeData.Tag;
import codeSliceExtractor.processJimple.codeData.Var;
import codeSliceExtractor.processJimple.processFuncs.JimpFuncInfo;
import codeSliceExtractor.processJimple.processFuncs.ProcessFuncInfo;
import codeSliceExtractor.processJimple.processInstructions.Tokenizer;
import codeSliceExtractor.processJimple.processTrees.TreeInfo;
import org.apache.commons.lang3.math.NumberUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ProcessPhpWebApp extends ProcessWebApp {
    private static final String PHP_RUNTIME = "jphp.runtime.";
    private static final String HASHMAP = "hashmap";
    private static final String REFPROP = "RefProp-";
    private ArrayList<String> funcsConsts = new ArrayList<String>(Arrays.asList(PHP_RUNTIME + "memory.StringMemory.valueOf", PHP_RUNTIME + "memory.LongMemory.valueOf",
            PHP_RUNTIME + "memory.DoubleMemory.valueOf", PHP_RUNTIME + "memory.TrueMemory.valueOf", PHP_RUNTIME + "Memory.NULL"));
    private ArrayList<String> funcOperators = new ArrayList<String>(Arrays.asList(PHP_RUNTIME + "Memory.minus", PHP_RUNTIME + "OperatorUtils.mul",
            PHP_RUNTIME + "OperatorUtils.plus", PHP_RUNTIME + "Memory.div", PHP_RUNTIME + "OperatorUtils.mod", PHP_RUNTIME + "Memory.pow",
            PHP_RUNTIME + "Memory.equal", PHP_RUNTIME + "Memory.identical", PHP_RUNTIME + "Memory.notEqual", PHP_RUNTIME + "Memory.notIdentical",
            PHP_RUNTIME + "Memory.greater", PHP_RUNTIME + "Memory.smaller", PHP_RUNTIME + "Memory.greaterEq", PHP_RUNTIME + "Memory.smallerEq",
            PHP_RUNTIME + "Memory.xor", PHP_RUNTIME + "Memory.not", PHP_RUNTIME + "Memory.plus", PHP_RUNTIME + "Memory.inc"));
    private ArrayList<String> casts = new ArrayList<String>(Arrays.asList(PHP_RUNTIME + "Memory.toLong", PHP_RUNTIME + "Memory.toString", PHP_RUNTIME + "Memory.toBoolean",
            PHP_RUNTIME + "Memory.toDouble"));
    private ArrayList<String> assOps = new ArrayList<String>(Arrays.asList(PHP_RUNTIME + "Memory.assignPlus", PHP_RUNTIME + "Memory.assignMinus",
            PHP_RUNTIME + "Memory.assignMul", PHP_RUNTIME + "Memory.assignDiv", PHP_RUNTIME + "Memory.assignMod"));
    private ArrayList<String> fileStringFuncs = new ArrayList<String>(Arrays.asList(PHP_RUNTIME + "ext.core.LangFunctions.trigger_error",
            "org.develnext.jphp.zend.ext.standard.FileFunctions.file_put_contents", "org.develnext.jphp.zend.ext.standard.FileFunctions.file_get_contents",
            "org.develnext.jphp.zend.ext.standard.FileFunctions.fopen", "org.develnext.jphp.zend.ext.standard.FileFunctions.file",
            "org.develnext.jphp.zend.ext.standard.FileFunctions.copy", "org.develnext.jphp.zend.ext.standard.FileFunctions.readfile",
            "org.develnext.jphp.zend.ext.standard.StringFunctions.htmlentities", "org.develnext.jphp.zend.ext.standard.StringFunctions.htmlspecialchars",
            "org.develnext.jphp.zend.ext.standard.StringFunctions.substr_replace", "org.develnext.jphp.zend.ext.standard.StringFunctions.strpos",
            "org.develnext.jphp.zend.ext.standard.StringFunctions.stristr", "org.develnext.jphp.zend.ext.standard.StringFunctions.strstr",
            PHP_RUNTIME + "ext.core.LangFunctions.user_error"));

    private ArrayList<String> extraFiles = new ArrayList<String>();

    private ProcessPhpWebApp() {
        super();
    }

    public static ProcessPhpWebApp getInstance() {
        if (single_instance == null) {
            single_instance = new ProcessPhpWebApp();
        }
        return (ProcessPhpWebApp) single_instance;
    }

    @Override
    public void init(ArrayList<String> files, HashMap<String, VulnerabilityInfo> sensitiveSinks, String baseDir) {
        super.init(files, sensitiveSinks, baseDir);
        this.setNoProcessFuncs(new ArrayList<String>(Arrays.asList("<init>(" + PHP_RUNTIME + "env.Environment, jphp.runtime.reflection.ClassEntity)")));
    }

    @Override
    public void process() {
        for(String file : super.getFiles()) {
            String filenamesSoot = super.getBaseDir()  + file + "\\sootOutput\\";
            File dirNameJimp = new File(filenamesSoot);
            String[] dirJimpFiles = dirNameJimp.getAbsoluteFile().list();
            if(dirJimpFiles == null) {
                System.out.println("File " + file + " was not processed. Please provide the sootOutput with the jimple code and control flow graph");
            }
            else {
                for(String dirJimpFile : dirJimpFiles) {
                    if (dirJimpFile.endsWith(".jimple")) {
                        //check if it is index???
                        // check if it was allready processed
                        int lastIndex = dirJimpFile.lastIndexOf(".");
                        String filenameJimp = dirJimpFile.substring(0,lastIndex);
                        this.processAux(file, filenameJimp, "", new ArrayList<Var>());
                    }
                }
            }
        }
        for(String s : super.getFuncSummaries().keySet()) {
            System.out.println("Processed func: " + s);
            for (ArrayList<Instruction> ints : super.getFuncSummaries().get(s).getPotentiallyVulnCodes()) {
                System.out.println("----- Potentially Vuln code -----");
                for(Instruction i : ints) {
                    i.print();
                }
            }
        }
    }

    public void processAux(String dirFilename, String funcName, String methodName, ArrayList<Var> paramsList) {
        //change this auxFuncName
        String filenamesSoot = super.getBaseDir()  + dirFilename + "\\sootOutput\\";
        String funcID = (methodName.equals("")) ? dirFilename + "\\" + funcName : dirFilename + "\\" + funcName + "\\" + methodName;
        if(!super.getFuncSummaries().containsKey(funcID)) {
            System.out.println("Start " + funcID + " processment");
            String cfgClinitFilename = filenamesSoot + funcName + "\\void [clinit]().dot";
            String cfgFilenameInclude = filenamesSoot + funcName + "\\" + PHP_RUNTIME + "Memory __include(jphp.runtime.env.Environment,jphp.runtime.Memory[],jphp.runtime.memory.ArrayMemory).dot";
            String cfgFilenameInvoke = filenamesSoot + funcName + "\\" + PHP_RUNTIME + "Memory __invoke(jphp.runtime.env.Environment,jphp.runtime.Memory[],jphp.runtime.memory.ArrayMemory).dot";
            File cfgClinit = new File(cfgClinitFilename);
            File cfgInclude = new File(cfgFilenameInclude);
            File cfgInvoke = new File(cfgFilenameInvoke);
            if(cfgClinit.exists() && (cfgInclude.exists() || cfgInvoke.exists() || !methodName.equals(""))) {
                ProcessFuncInfo processFuncInfo = new ProcessFuncInfo(filenamesSoot + "\\" + funcName + ".jimple");
                HashMap<String, ArrayList<JimpFuncInfo>> funcsNames = processFuncInfo.process();
                ArrayList<JimpFuncInfo> funcNames = funcsNames.get("<clinit>()");
                GraphProcessing graphProcessing = new GraphProcessing(cfgClinitFilename, funcNames, funcID);
                HashMap<String, Var> vars = graphProcessing.processing();
                if(cfgInclude.exists()) {
                    funcNames = funcsNames.get("__include(" + PHP_RUNTIME + "env.Environment, jphp.runtime.Memory[], jphp.runtime.memory.ArrayMemory)");
                    graphProcessing = new GraphProcessing(cfgFilenameInclude, funcNames, vars, funcID, paramsList);
                    graphProcessing.processing();
                }
                else if(cfgInvoke.exists()){
                    funcNames = funcsNames.get("__invoke(" + PHP_RUNTIME + "env.Environment, jphp.runtime.Memory[], jphp.runtime.memory.ArrayMemory)");
                    graphProcessing = new GraphProcessing(cfgFilenameInvoke, funcNames, vars, funcID, paramsList);
                    graphProcessing.processing();
                }
                else {
                    String classDirname = filenamesSoot + funcName;
                    File classDir = new File(classDirname);
                    String[] localMethods = classDir.getAbsoluteFile().list();
                    String beginningMethod = PHP_RUNTIME + "Memory " + methodName;
                    String methodNameCfg = "";
                    for(String method : localMethods) {
                        if(method.startsWith(beginningMethod)) {
                            methodNameCfg = method;
                            break;
                        }
                    }
                    if(!methodNameCfg.equals("")) {
                        String cfgMethodName = classDirname + "\\" + methodNameCfg;
                        String[] partsFuncName = methodNameCfg.split(" ");
                        String singlePartFuncname = partsFuncName[1];
                        partsFuncName = singlePartFuncname.split("\\(");
                        String realMethodName = partsFuncName[0];
                        funcNames = funcsNames.get(realMethodName + "(" + PHP_RUNTIME + "env.Environment, " + PHP_RUNTIME +
                                "Memory[], " + PHP_RUNTIME + "memory.ArrayMemory)");
                        graphProcessing = new GraphProcessing(cfgMethodName, funcNames, vars, funcID, paramsList);
                        graphProcessing.processing();
                    }
                }
                System.out.println("Finished " + funcID + " processment");
            }
            else {
                System.out.println("Function " + funcID + " from file " + dirFilename + " was not processed.");
                System.out.println("Please provide the control flow graph for this function (in the folder with the name of the file - " +
                        "For instance Example.java->sootOutput->Example.java->cfg_function.dot)");
            }
        }
    }

    @Override
    public TreeInfo additionalProcessFunc(String funcName,  ArrayList<String> nameParams, int nodeID, HashMap<String, ArrayList<Instruction>> historyInst,
                                          ArrayList<ArrayList<Object>> value, TreeInfo treeInfo, StringBuilder funcNameAux) throws Exception {
        if (this.isConst(funcName)) {
            historyInst = this.processConstant(funcName, nameParams, historyInst, funcNameAux);
        }
        else if(this.isOperator(funcName)) {
            historyInst = this.processOperations(funcName, nodeID, historyInst, treeInfo);
            Instruction op = new Instruction(Tag.OPERATION);
            historyInst.put("-1", new ArrayList<Instruction>(Arrays.asList(op)));
        }
        else if(funcName.equals(PHP_RUNTIME + "OperatorUtils.concat") || funcName.equals(PHP_RUNTIME + "Memory.concat") || funcName.equals(PHP_RUNTIME + "Memory.concatRight")) {
            historyInst = this.processOperations(funcName, nodeID, historyInst, treeInfo);
            Instruction instName = new Instruction(Tag.FUNCTION, "concat");
            historyInst.put("-1", new ArrayList<Instruction>(Arrays.asList(instName)));
        }
        else if(this.isCast(funcName)) {
            String varName = treeInfo.getVarFunc(nodeID);
            ArrayList<Instruction> varInsts = treeInfo.getHistoryVar(varName);
            historyInst.put(varName, varInsts);
            Instruction auxInst = new Instruction(Tag.FUNCTION, funcName);
            historyInst.values().remove(new ArrayList<Instruction>(Arrays.asList(auxInst)));
            Instruction op = new Instruction(Tag.CAST);
            historyInst.put("0", new ArrayList<Instruction>(Arrays.asList(op)));
        }
        else if(this.isAssOp(funcName)) {
            this.processAssOps(funcName, nodeID, historyInst, treeInfo);
        }
        else if (funcName.equals(PHP_RUNTIME + "Memory.assign") || funcName.equals(PHP_RUNTIME + "Memory.assignRight") ||
                 funcName.equals(PHP_RUNTIME + "Memory.assignConcat")) {
            treeInfo = this.processAssign(nodeID, funcName, historyInst, nameParams, value, treeInfo);
        }
        else if(funcName.equals(PHP_RUNTIME + "memory.ArrayMemory.createListed") || funcName.equals(PHP_RUNTIME + "memory.ArrayMemory.createHashed") ||
                funcName.equals(PHP_RUNTIME + "memory.ArrayMemory.add") || funcName.equals(PHP_RUNTIME + "memory.ArrayMemory.put")) {
            treeInfo = this.processArrayMemory(funcName, nodeID, nameParams, historyInst, value, treeInfo);
        }
        else if(funcName.equals(PHP_RUNTIME + "env.Environment.__getIterator") || funcName.equals(PHP_RUNTIME + "lang.ForeachIterator.getMemoryKey") ||
                funcName.equals(PHP_RUNTIME + "lang.ForeachIterator.getValue") || funcName.equals(PHP_RUNTIME + "lang.ForeachIterator.getValue")) {
            treeInfo = this.processIterator(funcName, nodeID, nameParams, historyInst, treeInfo);
        }
        else if(funcName.equals(PHP_RUNTIME + "Memory.__static_fast_toImmutable")) {
            Instruction instFunc = new Instruction(Tag.FUNCTION, PHP_RUNTIME + "Memory.__static_fast_toImmutable");
            historyInst.values().remove(new ArrayList<Instruction>(Arrays.asList(instFunc)));
        }
        else if(funcName.equals(PHP_RUNTIME + "Memory.toValue")) {
            String varName = treeInfo.getVarFunc(nodeID);
            ArrayList<Instruction> historyVar = treeInfo.getHistoryVar(varName);
            historyInst.put(varName, historyVar);
            Instruction instFunc = new Instruction(Tag.FUNCTION, PHP_RUNTIME + "Memory.toValue");
            historyInst.values().remove(new ArrayList<Instruction>(Arrays.asList(instFunc)));
        }
        else if(funcName.equals(PHP_RUNTIME + "Memory.valueOfIndex")) {
            String varName = treeInfo.getVarFunc(nodeID);
            //the index parameter is in the second argument
            String index = nameParams.get(1);
            ArrayList<Instruction> varNameInst = treeInfo.getHistoryVar(varName);
            boolean doneProcessing = false;
            if(varNameInst != null) {
                if(varNameInst.size() == 1) {
                    if(varNameInst.get(0).getTag() == Tag.SOURCE) {
                        String sourceName = varNameInst.get(0).getName() + "(" + index + ")";
                        historyInst.clear();
                        Instruction resultInst = new Instruction(Tag.SOURCE, sourceName);
                        historyInst.put(varName, new ArrayList<Instruction>(Arrays.asList(resultInst)));
                        doneProcessing = true;
                    }
                }
            }
            if(!doneProcessing) { this.processValueOfIndex(index, varName, historyInst, treeInfo); }
        }
        else if(funcName.equals(PHP_RUNTIME + "Memory.refOfIndex")) {
            Instruction instFunc = new Instruction(Tag.FUNCTION, funcName);
            historyInst.values().remove(new ArrayList<Instruction>(Arrays.asList(instFunc)));
            String varName = treeInfo.getVarFunc(nodeID);
            String index = nameParams.get(nameParams.size()-1);
            treeInfo = this.processRefOfIndex(index, varName, historyInst, treeInfo);
        }
        else if(funcName.equals(PHP_RUNTIME + "Memory.refOfPush")) {
            String varName = treeInfo.getVarFunc(nodeID);
            ArrayList<Object> histAux = new ArrayList<Object>();
            ArrayList<Instruction> dummyInstruction = new ArrayList<Instruction>(Arrays.asList(new Instruction(Tag.CONSTANT)));
            for (Instruction i : dummyInstruction) {
                histAux.add(i);
            }
            treeInfo.addValueHist(histAux, varName);
            int indexAux = treeInfo.lastIndexArray(varName);
            if(indexAux != -1) {
                historyInst.clear();
                ArrayList<Instruction> instructions = new ArrayList<Instruction>();
                String newVar = varName + "[" + indexAux + "]";
                instructions.add(new Instruction(Tag.ARRAY, newVar));
                historyInst.put("-1", instructions);
            }
        }
        else if(funcName.equals(PHP_RUNTIME + "env.Environment.getOrCreateStatic")) {
            ArrayList<String> toRemove = new ArrayList<String>();
            for (String key : historyInst.keySet()) {
                if (NumberUtils.isCreatable(key)) {
                    toRemove.add(key);
                }
            }
            for(String key : toRemove) {
                historyInst.remove(key);
            }
        }
        else if(funcName.equals("org.develnext.jphp.zend.ext.standard.StringFunctions.sprintf") ||
                funcName.equals("org.develnext.jphp.zend.ext.standard.StringFunctions.implode")){
            String realFuncName = this.cropRealNameFunc(funcName);
            if(nameParams.size() > 3) {
                String delParam = nameParams.get(0);
                historyInst.remove(delParam);
                delParam = nameParams.get(1);
                historyInst.remove(delParam);
                delParam = nameParams.get(3);
                historyInst.remove(delParam);
                this.processRealParamsVal(nameParams.get(3), historyInst, treeInfo, false);
            }
            Instruction instFunc = new Instruction(Tag.FUNCTION, funcName );
            historyInst.values().remove(new ArrayList<Instruction>(Arrays.asList(instFunc)));
            funcNameAux.append(realFuncName);
            Instruction instruction = new Instruction(Tag.FUNCTION, realFuncName);
            historyInst.put("-1", new ArrayList<Instruction>(Arrays.asList(instruction)));
        }
        else if(funcName.equals(PHP_RUNTIME + "invoke.InvokeHelper.call")) {
            String[] auxRealFunc = nameParams.get(3).split("\"");
            String realFuncName = auxRealFunc[1];
            historyInst.clear();
            this.processRealParamsVal(nameParams.get(4), historyInst, treeInfo, false);
            Instruction instruction = new Instruction(Tag.FUNCTION, realFuncName);
            historyInst.put("0", new ArrayList<Instruction>(Arrays.asList(instruction)));
            funcNameAux.append(realFuncName);
            treeInfo = this.processInter(realFuncName, historyInst, nameParams.get(4), treeInfo, "", new ArrayList<Instruction>());
            if(realFuncName.equals("exec")) { treeInfo = this.processParamAssign(realFuncName, nodeID, treeInfo); }
        }
        else if(funcName.equals(PHP_RUNTIME + "env.Environment.__newObject") || funcName.equals(PHP_RUNTIME + "invoke.ObjectInvokeHelper.invokeMethod") ||
                funcName.equals(PHP_RUNTIME + "invoke.ObjectInvokeHelper.getProperty") || funcName.equals(PHP_RUNTIME + "invoke.ObjectInvokeHelper.assignProperty") ||
                funcName.equals(PHP_RUNTIME + "invoke.ObjectInvokeHelper.getRefProperty") || funcName.equals(PHP_RUNTIME + "invoke.InvokeHelper.callStatic")) {
            treeInfo = this.processObject(funcName, nodeID, nameParams, funcNameAux, historyInst, treeInfo);
        }
        else if(funcName.equals(PHP_RUNTIME + "ext.core.OutputFunctions.print") || funcName.equals(PHP_RUNTIME + "ext.core.OutputFunctions.printf")) {
            if(!nameParams.isEmpty()) {
                String delParam = nameParams.get(0);
                historyInst.remove(delParam);
            }
            Instruction auxInst = new Instruction(Tag.FUNCTION, funcName);
            historyInst.values().remove(new ArrayList<Instruction>(Arrays.asList(auxInst)));
            funcNameAux.append(this.cropRealNameFunc(funcName));
            Instruction funcInst = new Instruction(Tag.FUNCTION, funcNameAux.toString());
            historyInst.put("-1", new ArrayList<Instruction>(Arrays.asList(funcInst)));
        }
        else if(this.fileStringFuncs.contains(funcName)) {
            if(nameParams.size() > 1) {
                String delParam = nameParams.get(0);
                historyInst.remove(delParam);
                delParam = nameParams.get(1);
                historyInst.remove(delParam);
            }
            Instruction auxInst = new Instruction(Tag.FUNCTION, funcName);
            historyInst.values().remove(new ArrayList<Instruction>(Arrays.asList(auxInst)));
            funcNameAux.append(this.cropRealNameFunc(funcName));
            Instruction funcInst = new Instruction(Tag.FUNCTION, funcNameAux.toString());
            historyInst.put("-1", new ArrayList<Instruction>(Arrays.asList(funcInst)));
        }
        else if(funcName.equals("org.develnext.jphp.zend.ext.standard.StringFunctions.str_replace")) {
            if(nameParams.size() > 4) {
                String delParam = nameParams.get(0);
                historyInst.remove(delParam);
                delParam = nameParams.get(1);
                historyInst.remove(delParam);
                if(historyInst.get(nameParams.get(2)).get(0).getTag() == Tag.ARRAY) {
                    this.processRealParamsVal(nameParams.get(2), historyInst, treeInfo, true);
                    ArrayList<Instruction> firstArg = historyInst.get("params");
                    historyInst.put("params0", firstArg);
                }
                if(historyInst.get(nameParams.get(3)).get(0).getTag() == Tag.ARRAY) {
                    this.processRealParamsVal(nameParams.get(3), historyInst, treeInfo, true);
                    ArrayList<Instruction> secondArg = historyInst.get("params");
                    historyInst.put("params1", secondArg);
                }
                if(historyInst.get(nameParams.get(4)).get(0).getTag() == Tag.ARRAY) {
                    this.processRealParamsVal(nameParams.get(4), historyInst, treeInfo, true);
                }
                Instruction auxInst = new Instruction(Tag.FUNCTION, funcName);
                historyInst.values().remove(new ArrayList<Instruction>(Arrays.asList(auxInst)));
                funcNameAux.append("str_replace");
                Instruction funcInst = new Instruction(Tag.FUNCTION, funcNameAux.toString());
                historyInst.put("-1", new ArrayList<Instruction>(Arrays.asList(funcInst)));
            }
        }
        else if(funcName.equals(PHP_RUNTIME + "ext.core.LangFunctions.eval")) {
            if(!nameParams.isEmpty()) {
                String param = nameParams.get(nameParams.size()-1);
                ArrayList<Instruction> instParam = historyInst.get(param);
                historyInst.clear();
                funcNameAux.append("eval");
                Instruction funcInst = new Instruction(Tag.FUNCTION, funcNameAux.toString());
                historyInst.put("-1", new ArrayList<Instruction>(Arrays.asList(funcInst)));
                historyInst.put(param, instParam);
            }
        }
        else if(funcName.equals(PHP_RUNTIME + "env.Environment.__require") || funcName.equals(PHP_RUNTIME + "env.Environment.__requireOnce") ||
                funcName.equals(PHP_RUNTIME + "env.Environment.__include") || funcName.equals((PHP_RUNTIME + "env.Environment.__includeOnce"))) {
            if(nameParams.size() > 2) {
                String delParam = nameParams.get(nameParams.size()-1);
                historyInst.remove(delParam);
                delParam = nameParams.get(nameParams.size()-2);
                historyInst.remove(delParam);
            }
            Tokenizer tokenizer = new Tokenizer();
            if(tokenizer.isConstant(nameParams.get(0))) {
                String[] auxIncFile = nameParams.get(0).split("\"");
                this.extraFiles.add(auxIncFile[1]);
            }
            Instruction auxInst = new Instruction(Tag.FUNCTION, funcName);
            historyInst.values().remove(new ArrayList<Instruction>(Arrays.asList(auxInst)));
            if(funcName.equals(PHP_RUNTIME + "env.Environment.__require")) { funcNameAux.append("require"); }
            else if(funcName.equals(PHP_RUNTIME + "env.Environment.__requireOnce")) { funcNameAux.append("require_once"); }
            else if(funcName.equals(PHP_RUNTIME + "env.Environment.__include")) { funcNameAux.append("include"); }
            else { funcNameAux.append("include_once"); }
            Instruction funcInst = new Instruction(Tag.FUNCTION, funcNameAux.toString());
            historyInst.put("-1", new ArrayList<Instruction>(Arrays.asList(funcInst)));
        }
        else if(funcName.equals(PHP_RUNTIME + "OperatorUtils.isset")) {
            String delParam = nameParams.get(0);
            historyInst.remove(delParam);
            this.processRealParamsVal(delParam, historyInst, treeInfo, true);
            boolean hasSource = false;
            Instruction funcInst = new Instruction(Tag.FUNCTION, funcName);
            historyInst.values().remove(new ArrayList<Instruction>(Arrays.asList(funcInst)));
            for(Instruction auxInst : historyInst.get("params")) {
                if(auxInst.getTag() == Tag.SOURCE) {
                    funcNameAux.append("isset_source");
                    hasSource = true;
                    break;
                }
            }
            if(!hasSource) {
                funcNameAux.append("isset");
            }
            funcInst = new Instruction(Tag.FUNCTION, funcNameAux.toString());
            historyInst.put("-1", new ArrayList<Instruction>(Arrays.asList(funcInst)));
        }
        else if(funcName.equals(PHP_RUNTIME + "env.Environment.__throwException")) {
            historyInst.clear();
            funcNameAux.append("throw Exception");
            Instruction funcInst = new Instruction(Tag.FUNCTION, funcNameAux.toString());
            treeInfo.addExitInstContext(funcInst);
            historyInst.put("-1", new ArrayList<Instruction>(Arrays.asList(funcInst)));
        }
        else if(funcName.equals(PHP_RUNTIME + "env.Environment.__shellExecute")) { funcNameAux.append("shell_exec"); }
        else {
            this.transformFuncName(funcName, funcNameAux);
            if(!(funcNameAux.length() == 0)) {
                Instruction auxInst = new Instruction(Tag.FUNCTION, funcName);
                historyInst.values().remove(new ArrayList<Instruction>(Arrays.asList(auxInst)));
                Instruction funcInst = new Instruction(Tag.FUNCTION, funcNameAux.toString());
                historyInst.put("-1", new ArrayList<Instruction>(Arrays.asList(funcInst)));
            }
        }
        return treeInfo;
    }

    /**
     * Process Functions that represent Constants
     * @param funcName
     * @param nameParams
     * @param historyInst
     * @param funcNameAux
     * @return
     * @throws Exception
     */
    public HashMap<String, ArrayList<Instruction>> processConstant(String funcName, ArrayList<String> nameParams, HashMap<String, ArrayList<Instruction>> historyInst,
                                StringBuilder funcNameAux) throws Exception {
        if (NumberUtils.isCreatable(nameParams.get(0))) {
            Instruction inst = new Instruction(Tag.CONSTANT);
            //It works like this, it will change in other function
            //Magic of references   ¯\_(ツ)_/¯
            historyInst.clear();
            historyInst.put("0", new ArrayList<Instruction>(Arrays.asList(inst)));
        }
        else {
            Instruction auxInst = new Instruction(Tag.FUNCTION, funcName);
            historyInst.values().remove(new ArrayList<Instruction>(Arrays.asList(auxInst)));
        }
        if(funcName.equals(PHP_RUNTIME + "memory.LongMemory.valueOf") || funcName.equals(PHP_RUNTIME + "memory.DoubleMemory.valueOf")) {
            if (funcName.equals(PHP_RUNTIME + "memory.LongMemory.valueOf")) {
                funcNameAux.append("intval");
            } else {
                funcNameAux.append("floatval");
            }
            Instruction auxInst = new Instruction(Tag.FUNCTION, funcName);
            historyInst.values().remove(new ArrayList<Instruction>(Arrays.asList(auxInst)));
            auxInst = new Instruction(Tag.FUNCTION, funcNameAux.toString());
            historyInst.put("-1", new ArrayList<Instruction>(Arrays.asList(auxInst)));
        }
        return  historyInst;
    }

    /**
     * Process Functions that represent Operations
     * @param funcName
     * @param nodeID
     * @param historyInst
     * @param treeInfo
     * @return
     * @throws Exception
     */
    public HashMap<String, ArrayList<Instruction>> processOperations(String funcName, int nodeID,
                                                                     HashMap<String, ArrayList<Instruction>> historyInst, TreeInfo treeInfo)
                                                                    throws Exception {
        String varName = treeInfo.getVarFunc(nodeID);
        if (!varName.equals(" ")) {
            ArrayList<Instruction> varInsts = treeInfo.getHistoryVar(varName);
            historyInst.put(varName, varInsts);
        }
        Instruction auxInst = new Instruction(Tag.FUNCTION, funcName);
        historyInst.values().remove(new ArrayList<Instruction>(Arrays.asList(auxInst)));
        return historyInst;
    }

    public void processAssOps(String funcName, int nodeID, HashMap<String, ArrayList<Instruction>> historyInst,
                                                                 TreeInfo treeInfo) throws Exception {
        String varName = treeInfo.getVarFunc(nodeID);
        for (String key : historyInst.keySet()) {
            for (Instruction inst : historyInst.get(key)) {
                if (!inst.getName().equals(funcName)) {
                    treeInfo.addInstructionVar(varName, inst);
                }
            }
        }
        Instruction opInst = new Instruction(Tag.OPERATION);
        treeInfo.addInstructionVar(varName, opInst);
        Instruction auxInst = new Instruction(Tag.FUNCTION, funcName);
        historyInst.values().remove(new ArrayList<Instruction>(Arrays.asList(auxInst)));
    }

    /**
     * Process Functions that represent Assignments
     * @param nodeID
     * @param funcName
     * @param historyInst
     * @param nameParams
     * @param value
     * @param treeInfo
     * @return
     * @throws Exception
     */
    public  TreeInfo processAssign(int nodeID, String funcName, HashMap<String, ArrayList<Instruction>> historyInst,
                                   ArrayList<String> nameParams, ArrayList<ArrayList<Object>> value, TreeInfo treeInfo) throws Exception {
        if (funcName.equals(PHP_RUNTIME + "Memory.assign")) {
            String varName = treeInfo.getVarFunc(nodeID);
            ArrayList<Instruction> varNameHist = treeInfo.getHistoryVar(varName);
            boolean refOfIndex = false;
            if(varNameHist != null) {
                if(!varNameHist.isEmpty()) {
                    if(varNameHist.get(0).getTag() == Tag.ARRAY && varNameHist.get(0).getName().contains("[") && varNameHist.get(0).getName().contains("]")) {
                        String[] partsRealname = varNameHist.get(0).getName().split("\\[");
                        String realVarname = partsRealname[0];
                        ArrayList<Instruction> hist = new ArrayList<Instruction>();
                        for (String key : historyInst.keySet()) {
                            for (Instruction inst : historyInst.get(key)) {
                                if (!inst.getName().equals(PHP_RUNTIME + "Memory.assign") && !(inst.getTag() == Tag.CAST)) {
                                    hist.add(inst);
                                }
                            }
                        }
                        String[] partIndex = partsRealname[1].split("\\]");
                        if(NumberUtils.isCreatable(partIndex[0])) {
                            refOfIndex = true;
                            int index = Integer.parseInt(partIndex[0]);
                            if (index == -1) {
                                if(realVarname.startsWith(REFPROP)) {
                                    String[] pRefProp = realVarname.split(REFPROP);
                                    String refProp = pRefProp[1];
                                    Var varRefProp = super.getFuncSummaries().get(treeInfo.getFuncSummary().getId()).getVarThis(refProp);
                                    varRefProp.setArrayHistoryVar(hist);
                                    super.getFuncSummaries().get(treeInfo.getFuncSummary().getId()).putVarThis(refProp, varRefProp);
                                }
                                else { treeInfo.updateArrayHistoryVar(realVarname, hist); }
                            }
                            else {
                                if(realVarname.startsWith(REFPROP)) {
                                    String[] pRefProp = realVarname.split(REFPROP);
                                    String refProp = pRefProp[1];

                                    Var varRefProp = super.getFuncSummaries().get(treeInfo.getFuncSummary().getId()).getVarThis(refProp);
                                    varRefProp.setHistoryIndex(index, hist);
                                    super.getFuncSummaries().get(treeInfo.getFuncSummary().getId()).putVarThis(refProp, varRefProp);
                                }
                                else {
                                    treeInfo.updateIndexHistoryVar(realVarname, index, hist);
                                }
                            }
                        }
                    }
                }
            }
            if(!refOfIndex) {
                boolean varCreated = false;
                for (String key : historyInst.keySet()) {
                    for (Instruction inst : historyInst.get(key)) {
                        if (!inst.getName().equals(PHP_RUNTIME + "Memory.assign")) {
                            if (varCreated) { treeInfo.addInstructionVar(varName, inst); }
                            else {
                                treeInfo.newVar(varName, inst);
                                varCreated = true;
                            }
                        }
                    }
                }
                this.processValue(value, varName, treeInfo);
            }
            Instruction auxInst = new Instruction(Tag.FUNCTION, PHP_RUNTIME + "Memory.assign");
            historyInst.values().remove(new ArrayList<Instruction>(Arrays.asList(auxInst)));
        }
        else if(funcName.equals(PHP_RUNTIME + "Memory.assignRight")) {
            if(nameParams.size() > 1) {
                String toBeCopied = nameParams.get(0);
                String varName = nameParams.get(1);
                ArrayList<Instruction> instructions = historyInst.get(toBeCopied);
                treeInfo.newVar(varName, instructions.get(0));
                for(int i = 1; i < instructions.size(); i++) {
                    treeInfo.addInstructionVar(varName, instructions.get(i));
                }
                this.processValue(value, varName, treeInfo);
            }
        }
        else if(funcName.equals(PHP_RUNTIME + "Memory.assignConcat")) {
            this.processAssOps(funcName, nodeID, historyInst, treeInfo);
            Instruction auxInst = new Instruction(Tag.FUNCTION, "assignConcat");
            historyInst.put("-1", new ArrayList<Instruction>(Arrays.asList(auxInst)));
        }
        return treeInfo;
    }

    /**
     * Functions that manipulate ArrayMemory Structures
     * @param funcName
     * @param nodeID
     * @param nameParams
     * @param historyInst
     * @param value
     * @param treeInfo
     * @return
     * @throws Exception
     */
    public TreeInfo processArrayMemory(String funcName, int nodeID, ArrayList<String> nameParams,
                                       HashMap<String, ArrayList<Instruction>> historyInst, ArrayList<ArrayList<Object>> value,
                                       TreeInfo treeInfo) throws Exception {
        if(funcName.equals(PHP_RUNTIME + "memory.ArrayMemory.createListed")) {
            historyInst.clear();
            Instruction instruction = new Instruction(Tag.ARRAY);
            historyInst.put("0", new ArrayList<Instruction>(Arrays.asList(instruction)));
        }
        else if(funcName.equals(PHP_RUNTIME + "memory.ArrayMemory.createHashed")) {
            historyInst.clear();
            Instruction instruction = new Instruction(Tag.ARRAY, HASHMAP);
            historyInst.put("0", new ArrayList<Instruction>(Arrays.asList(instruction)));
        }
        else if(funcName.equals(PHP_RUNTIME + "memory.ArrayMemory.add")) {
            String varName = treeInfo.getVarFunc(nodeID);
            //in this case, we are not adding an array for sure
            Instruction insAux = new Instruction(Tag.FUNCTION, PHP_RUNTIME + "memory.ArrayMemory.add");
            historyInst.values().remove(new ArrayList<Instruction>(Arrays.asList(insAux)));
            if (value.isEmpty()) {
                ArrayList<Object> histAux = new ArrayList<Object>();
                for (String var : historyInst.keySet()) {
                    for (Instruction i : historyInst.get(var)) {
                        histAux.add(i);
                    }
                }
                treeInfo.addValueHist(histAux, varName);
            }
            else {
                ArrayList<Object> realValue = new ArrayList<Object>();
                for (ArrayList<Object> val : value) {
                    Object o = val;
                    realValue.add(val);
                }
                treeInfo.addValueHist(realValue, varName);
            }
        }
        else if(funcName.equals(PHP_RUNTIME + "memory.ArrayMemory.put")) {
            String varName = treeInfo.getVarFunc(nodeID);
            String key = nameParams.get(0);
            ArrayList<Instruction> keyInst = historyInst.get(key);
            if(keyInst == null) { keyInst = new ArrayList<Instruction>(Arrays.asList(new Instruction(Tag.CONSTANT, key))); }
            else { historyInst.remove(key); }
            ArrayList<Object> histAux = new ArrayList<Object>();
            if(value.isEmpty()) {
                for (String var : historyInst.keySet()) {
                    for (Instruction i : historyInst.get(var)) {
                        histAux.add(i);
                    }
                }
            }
            else {
                for (ArrayList<Object> val : value) {
                    Object o = val;
                    histAux.add(val);
                }
            }
            ArrayList<Instruction> keySetName = treeInfo.getHistoryVar(varName);
            int index = -1;
            if(keySetName != null) {
                Tokenizer tokenizer = new Tokenizer();
                for(int i = 0; i < keySetName.size(); i++) {
                    if(keySetName.get(i).getTag() == Tag.CONSTANT) {
                        if(tokenizer.isConstant(keySetName.get(i).getName())) {
                            if(keyInst.equals(new ArrayList<Instruction>(Arrays.asList(keySetName.get(i))))) {
                                index = i;
                                break;
                            }
                        }
                        else {
                            ArrayList<Instruction> keyInstVar = treeInfo.getHistoryVar(keySetName.get(i).getName());
                            if(keyInstVar != null) {
                                if(keyInst.equals(keyInstVar)) {
                                    index = i;
                                    break;
                                }
                            }
                        }
                    }
                }
                if(index != -1) {
                    treeInfo.setHist(varName, index - 1, histAux);
                }
                else {
                    treeInfo.addInstructionVar(varName, new Instruction(Tag.CONSTANT, key));
                    treeInfo.addValueHist(histAux, varName);
                }
            }

        }
        return treeInfo;
    }

    /**
     * Functions related with the Iterator
     * @param funcName
     * @param nodeID
     * @param nameParams
     * @param historyInst
     * @param treeInfo
     * @return
     * @throws Exception
     */
    public TreeInfo processIterator(String funcName, int nodeID, ArrayList<String> nameParams,
                                       HashMap<String, ArrayList<Instruction>> historyInst, TreeInfo treeInfo) throws Exception {
        if(funcName.equals(PHP_RUNTIME + "env.Environment.__getIterator")) {
            if(nameParams.size() > 1) {
                String varName = nameParams.get(1);
                historyInst.clear();
                ArrayList<Instruction> instsVar = treeInfo.getHistoryVar(varName);
                if(instsVar != null) { historyInst.put(varName, instsVar); }
            }
        }
        else if(funcName.equals(PHP_RUNTIME + "lang.ForeachIterator.getMemoryKey")) {
            historyInst.clear();
            String varName = treeInfo.getVarFunc(nodeID);
            ArrayList<Instruction> keysNameInst = treeInfo.getHistoryVar(varName);
            ArrayList<Instruction> keysInsts = new ArrayList<Instruction>();
            Tokenizer tokenizer = new Tokenizer();
            for(Instruction inst : keysNameInst) {
                if(inst.getTag() == Tag.CONSTANT) {
                    String key = inst.getName();
                    if(tokenizer.isConstant(key)) {
                        Instruction instConstant = new Instruction(Tag.CONSTANT, key);
                        keysInsts.add(instConstant);
                    }
                    else {
                        ArrayList<Instruction> keyInstructions = treeInfo.getHistoryVar(varName);
                        if(keyInstructions != null) {
                            for(Instruction keyInstruction : keyInstructions) {
                                keysInsts.add(keyInstruction);
                            }
                        }
                    }
                }
            }
            historyInst.put("-1", keysInsts);
        }
        else if(funcName.equals(PHP_RUNTIME + "lang.ForeachIterator.getValue")) {
            String varName = treeInfo.getVarFunc(nodeID);
            historyInst.clear();
            ArrayList<Instruction> instructions = treeInfo.getHistoryArrayVar(varName);
            if(!instructions.isEmpty()) { historyInst.put("-1", instructions); }
            else {
                instructions = treeInfo.getHistoryVar(varName);
                if(instructions != null) { historyInst.put("-1", instructions); }
            }
        }
        else if(funcName.equals(PHP_RUNTIME + "lang.ForeachIterator.getValue")) {
            String varName = treeInfo.getVarFunc(nodeID);
            ArrayList<Instruction> instructions = treeInfo.getHistoryVar(varName);
            historyInst.put(varName, instructions);
            Instruction instFunc = new Instruction(Tag.FUNCTION, PHP_RUNTIME + "lang.ForeachIterator.getValue");
            historyInst.values().remove(new ArrayList<Instruction>(Arrays.asList(instFunc)));
        }
        return treeInfo;
    }

    /**
     * Process functions related with manipulation of objects
     * @param funcName
     * @param nodeID
     * @param nameParams
     * @param funcNameAux
     * @param historyInst
     * @param treeInfo
     * @return
     * @throws Exception
     */
    public TreeInfo processObject(String funcName, int nodeID, ArrayList<String> nameParams, StringBuilder funcNameAux,
                                  HashMap<String, ArrayList<Instruction>> historyInst, TreeInfo treeInfo) throws Exception {
        if(funcName.equals(PHP_RUNTIME + "env.Environment.__newObject")) {
            String realFuncName = funcName;
            if(nameParams.get(0).contains("\"")) {
                String[] auxRealFunc = nameParams.get(0).split("\"");
                realFuncName = auxRealFunc[1];
            }
            this.processRealParamsVal(nameParams.get(3), historyInst, treeInfo, false);
            ArrayList<Instruction> objInsts  = new ArrayList<Instruction>();
            Instruction instruction = new Instruction(Tag.OBJECT, realFuncName);
            objInsts.add(instruction);
            funcNameAux.append(realFuncName);
            treeInfo = this.processInter(realFuncName, historyInst, nameParams.get(3), treeInfo, "__construct", new ArrayList<Instruction>());
            historyInst.clear();
            historyInst.put("0", objInsts);
        }
        else if(funcName.equals(PHP_RUNTIME + "invoke.ObjectInvokeHelper.invokeMethod")) {
            ArrayList<Instruction> objectInsts = treeInfo.getHistoryVar(nameParams.get(0));
            String objName = "";
            if(objectInsts != null) {
                if(!objectInsts.isEmpty()) {
                    if(objectInsts.get(0).getTag() == Tag.OBJECT) {
                        objName = objectInsts.get(0).getName();
                    }
                }
            }
            String[] auxRealMethod = nameParams.get(1).split("\"");
            //The zero is just a little mark to identify the function as the object name
            String realMethodName = auxRealMethod[1];
            historyInst.clear();
            this.processRealParamsVal(nameParams.get(5), historyInst, treeInfo, false);
            Instruction instruction = objName.equals("") ?  new Instruction(Tag.FUNCTION, realMethodName) : new Instruction(Tag.FUNCTION, objName + "::" + realMethodName);
            historyInst.put("0", new ArrayList<Instruction>(Arrays.asList(instruction)));
            treeInfo = this.processInter(objName, historyInst, nameParams.get(5), treeInfo, realMethodName, objectInsts);
            if(!objName.equals("")) { funcNameAux.append(objName + "::" + realMethodName); }
            else { funcNameAux.append(realMethodName); }
        }
        else if(funcName.equals(PHP_RUNTIME + "invoke.InvokeHelper.callStatic")) {
            String objName = nameParams.get(2).replace("\"", "");
            String realMethodName = nameParams.get(3).replace("\"", "");
            historyInst.clear();
            this.processRealParamsVal(nameParams.get(6), historyInst, treeInfo, false);
            Instruction instruction = new Instruction(Tag.FUNCTION, realMethodName);
            historyInst.put("0", new ArrayList<Instruction>(Arrays.asList(instruction)));
            funcNameAux.append(realMethodName);
            treeInfo = this.processInter(objName, historyInst, nameParams.get(6), treeInfo, realMethodName, new ArrayList<Instruction>());
        }
        else if(funcName.equals(PHP_RUNTIME + "invoke.ObjectInvokeHelper.getProperty")) {
            historyInst.clear();
            if(nameParams.size() > 1) {
                String attName = nameParams.get(1);
                String fullFuncID = treeInfo.getFuncSummary().getId();
                String fullConstructFunc = this.getConstructFunc(fullFuncID);
                Var var = super.getFuncSummaries().get(fullConstructFunc).getVarThis(attName);
                treeInfo.putVar(attName, var);
                historyInst.put(attName, var.getInstructions());
                if(var.getInstructions().get(0).getTag() != Tag.ARRAY) {
                    treeInfo.putVar("this." + attName, new Var(attName, new Instruction(Tag.OBJECT, "@this-" + attName)));
                    historyInst.put("this." + attName, new ArrayList<Instruction>(Arrays.asList(new Instruction(Tag.OBJECT, "@this-" + attName))));
                }
                //Instruction instruction = new Instruction(Tag.OBJECT, "@this");
                //historyInst.put("0", new ArrayList<Instruction>(Arrays.asList(instruction)));
            }
        }
        else if(funcName.equals(PHP_RUNTIME + "invoke.ObjectInvokeHelper.assignProperty")) {
            historyInst.clear();
            Instruction instruction = new Instruction(Tag.OBJECT, "");
            historyInst.put("0", new ArrayList<Instruction>(Arrays.asList(instruction)));
            String assVarName = nameParams.get(1);
            String attName = nameParams.get(2);
            Var assVar = treeInfo.getVar(assVarName);
            if(treeInfo.getFuncSummary().getId().contains("__construct") && nameParams.size() > 2) {
                super.getFuncSummaries().get(treeInfo.getFuncSummary().getId()).putVarThis(attName, assVar);
            }
            else {
                String fullFuncID = treeInfo.getFuncSummary().getId();
                String fullConstructFunc = this.getConstructFunc(fullFuncID);
                super.getFuncSummaries().get(fullConstructFunc).putVarThis(attName, assVar);
                treeInfo.putAttVar("this." + attName, assVar);
            }
        }
        else if(funcName.equals(PHP_RUNTIME + "invoke.ObjectInvokeHelper.getRefProperty")) {
            historyInst.clear();
            if(nameParams.size() > 1) {
                if(treeInfo.getFuncSummary().getId().contains("__construct")) {
                    String attName = nameParams.get(1);
                    Instruction regProfInst = new Instruction(Tag.ARRAY, REFPROP + attName);
                    historyInst.put("-1", new ArrayList<Instruction>(Arrays.asList(regProfInst)));
                }
            }
        }
        return treeInfo;
    }

    public String getConstructFunc(String fullFuncID) {
        int lastIndexSlash = fullFuncID.lastIndexOf("\\");
        String funcIdConstruct = fullFuncID.substring(0, lastIndexSlash) + "\\__construct";
        String fullConstructFunc = "";
        for(String funcSumId : super.getFuncSummaries().keySet()) {
            if(funcSumId.startsWith(funcIdConstruct)) {
                fullConstructFunc = funcSumId;
                break;
            }
        }
        return fullConstructFunc;
    }

    /**
     *******    ProcessFunction Auxilary Methods   ******
     */
    public TreeInfo processInter(String realFuncName, HashMap<String, ArrayList<Instruction>> historyInst, String param, TreeInfo treeInfo, String methodName,
                             ArrayList<Instruction> objInsts) throws Exception{
        //make sure it exists
        String idCurrentFunc = treeInfo.getFuncSummary().getId();
        int p = idCurrentFunc.lastIndexOf("\\");
        //String funcName = idCurrentFunc.substring(p+1);
        String dirLocalFile = idCurrentFunc.substring(0,p);
        if(realFuncName.equals("")) {
            if(dirLocalFile.contains("\\")) {
                int pAux = dirLocalFile.lastIndexOf("\\");
                realFuncName = dirLocalFile.substring(pAux+1);
                dirLocalFile = dirLocalFile.substring(0, pAux);
            }
        }
        String funcNameId = (methodName.equals("")) ? dirLocalFile + "\\" + realFuncName : dirLocalFile + "\\" + realFuncName + "\\" + methodName;
        if(!idCurrentFunc.equals(funcNameId)) {
            String possLocalDirName = super.getBaseDir() + "\\" + dirLocalFile + "\\sootOutput\\";
            File possLocalDir = new File(possLocalDirName);
            String[] localFuncs = possLocalDir.getAbsoluteFile().list();
            boolean existsFunc = false;
            if(localFuncs != null) {
                if(Arrays.asList(localFuncs).contains(realFuncName)) {
                    existsFunc = true;
                }
            }
            if(!existsFunc) {
                for(String extraFile : this.extraFiles) {
                    String possDirNameExt;
                    if(extraFile.contains("..")) {
                        int lastIndexDoubleDots = extraFile.lastIndexOf("..");
                        String realExtraFile = extraFile.substring(lastIndexDoubleDots+3);
                        realExtraFile = realExtraFile.replace("/", "\\");
                        String commonDir = "";
                        if(dirLocalFile.contains("\\")) {
                            int lastIndDirLocal = dirLocalFile.lastIndexOf("\\");
                            commonDir = dirLocalFile.substring(0, lastIndDirLocal);
                        }
                        if(extraFile.contains("/")) {
                            String[] pExtraFile = extraFile.split("/");
                            for(String piece : pExtraFile) {
                                if(piece.equals("..")) {
                                    if(commonDir.contains("\\")) {
                                        int pAuxCommonDir = commonDir.lastIndexOf("\\");
                                        commonDir = commonDir.substring(0, pAuxCommonDir);
                                    }
                                    else {
                                        commonDir = "";
                                        break;
                                    }
                                }
                                else {
                                    break;
                                }
                            }
                            extraFile = (commonDir.equals("")) ? realExtraFile : commonDir + "\\" + realExtraFile;
                        }
                    }
                    possDirNameExt = super.getBaseDir() + "\\" + extraFile + "\\sootOutput\\";
                    File dirExt = new File(possDirNameExt);
                    String[] extFuncs = dirExt.getAbsoluteFile().list();
                    if(extFuncs != null) {
                        if(Arrays.asList(extFuncs).contains(realFuncName)) {
                            dirLocalFile = extraFile;
                            funcNameId = dirLocalFile + "\\" + realFuncName + "\\" + methodName;
                            existsFunc = true;
                            break;
                        }
                    }
                }
            }
            if(existsFunc) {
                ArrayList<Var> paramList = treeInfo.getVarParams(new ArrayList<String>(Arrays.asList(param)));
                Var var = new Var("", new Instruction(Tag.OBJECT));
                ArrayList<Var> paramsList = new ArrayList<Var>(Arrays.asList(var, paramList.get(0)));
                boolean procRealTime = true;
                if(!super.containsFuncSummary(funcNameId)) {
                    this.processAux(dirLocalFile, realFuncName, methodName, paramsList);
                    procRealTime = false;
                }
                String currentDir = idCurrentFunc.substring(0,p);
                HashMap<String, Var> attVars = treeInfo.getAttVars();
                if(attVars.isEmpty()) {
                    if(super.getFuncSummaries().containsKey(dirLocalFile + "\\" + realFuncName + "\\__construct")) {
                        attVars = super.getFuncSummaries().get(dirLocalFile + "\\" + realFuncName + "\\__construct").getAttVars();
                    }
                }
                if(methodName.equals("__construct")) { super.setAuxObjParam(funcNameId, paramsList); }
                ArrayList<Integer> paramPot = (currentDir.equals(dirLocalFile) && !methodName.equals("")) ? super.processFuncSummaryParams(funcNameId, paramsList, objInsts, attVars) :
                        super.processFuncSummaryParams(funcNameId, paramsList, objInsts, new HashMap<String, Var>());
                for(Integer indexPot : paramPot) {
                    super.replacePotVuln(funcNameId, treeInfo.getParamVars(), indexPot);
                }
                if(!procRealTime) {
                    super.processUserDefFunc(funcNameId, paramsList, objInsts);
                }
                historyInst.clear();
                Var retVar = (currentDir.equals(dirLocalFile) && !methodName.equals("")) ? super.returnVarFuncSummary(funcNameId, paramsList, objInsts, attVars)
                        : super.returnVarFuncSummary(funcNameId, paramsList, objInsts, new HashMap<String, Var>());
                ArrayList<Var> varProcessed = super.processSendParamList(new ArrayList<Var>(Arrays.asList(retVar)), treeInfo.getParamVars());
                retVar = varProcessed.get(0);
                ArrayList<Instruction> varInsts = retVar.getInstructions();
                historyInst.put("0temp", varInsts);
                if(!varInsts.isEmpty()) {
                    if(varInsts.get(0).getTag() == Tag.ARRAY) {
                        treeInfo.newVar("0temp", varInsts.get(0));
                        treeInfo.setValueVar((ArrayList<ArrayList<Object>>) retVar.getValue(), "0temp");
                    }
                }
            }
        }
        return treeInfo;
    }

    public String processIndex(String index) throws Exception {
        Tokenizer tokenizer = new Tokenizer();
        String stringIndex = "";
        if(NumberUtils.isCreatable(index)){
            char c;
            for (int i = 0; i < index.length(); i++) {
                c = index.charAt(i);
                if (!Character.isDigit(c)) {
                    break;
                }
                stringIndex += c;
            }
        }
        else if(tokenizer.isConstant(index)) {
            stringIndex = index;
        }
        return stringIndex;
    }

    public TreeInfo processRefOfIndex(String index, String varName, HashMap<String, ArrayList<Instruction>> historyInst, TreeInfo treeInfo) throws Exception {
        String stringIndex = processIndex(index);
        boolean isSource = false;
        String varSource = "";
        if(NumberUtils.isCreatable(stringIndex)) {
            stringIndex = NumberUtils.createLong(stringIndex).toString();
        }
        else {
            String auxIndex[] = stringIndex.split("\"");
            if(auxIndex.length > 1) {
                varSource = auxIndex[1];
                String altVarSource = "$" + varSource;
                for(String key : super.getInfoVulns().keySet()) {
                    if(super.getInfoVulns().get(key).getSources().contains(varSource)) {
                        isSource = true;
                    }
                    else if(super.getInfoVulns().get(key).getSources().contains(altVarSource)) {
                        isSource = true;
                        varSource = altVarSource;
                    }
                }
            }
        }
        if(isSource) {
            Instruction sourceInst = new Instruction(Tag.SOURCE, varSource);
            historyInst.clear();
            historyInst.put("-1", new ArrayList<Instruction>(Arrays.asList(sourceInst)));
        }
        else {
            ArrayList<Instruction> historyVar = treeInfo.getHistoryVar(varName);
            boolean processed = false;
            String refPropVar = "";
            String fullConstructFunc = "";
            Tokenizer tokenizer = new Tokenizer();
            if(historyVar != null) {
                if(!historyVar.isEmpty()) {
                    if(historyVar.get(0).getTag() == Tag.ARRAY && historyVar.get(0).getName().startsWith(REFPROP)) {
                        String[] pRefPropVar = historyVar.get(0).getName().split(REFPROP);
                        refPropVar = pRefPropVar[1];
                        String fullFuncID = treeInfo.getFuncSummary().getId();
                        fullConstructFunc = this.getConstructFunc(fullFuncID);
                        Var var = super.getFuncSummaries().get(fullConstructFunc).getVarThis(refPropVar);
                        historyVar = var.getInstructions();
                    }
                    if(historyVar.get(0).getTag() == Tag.ARRAY && historyVar.get(0).getName().equals(HASHMAP)) {
                        processed = true;
                        treeInfo = this.processRefOfIndexHashmap(historyVar, index, varName, refPropVar, fullConstructFunc,historyInst, treeInfo);
                    }
                    else if(historyVar.get(0).getTag() == Tag.ARRAY && historyVar.get(0).getName().contains("[") && historyVar.get(0).getName().contains("]")) {
                        processed = true;
                        treeInfo = this.processMultiDimRefOfIndex(varName, stringIndex, refPropVar, treeInfo, historyInst, historyVar);
                    }
                    else if(historyVar.get(0).getTag() == Tag.ARRAY && tokenizer.isConstant(stringIndex) && !NumberUtils.isCreatable(stringIndex)) {
                        processed = true;
                        treeInfo = this.transfArrayToHashmap(varName, stringIndex, refPropVar, historyInst, treeInfo);
                    }
                }
            }
            if(!processed) {
                treeInfo = this.processRefOfIndexAux(varName, stringIndex, refPropVar, historyInst, treeInfo);
            }
        }
        return treeInfo;
    }

     public TreeInfo processRefOfIndexHashmap(ArrayList<Instruction> historyVar, String index, String varName, String refPropVar, String fullConstructFunc,
                                              HashMap<String, ArrayList<Instruction>> historyInst, TreeInfo treeInfo) throws Exception{
         ArrayList<Instruction> keyInst = historyInst.get(index);
         if(keyInst == null) { keyInst = new ArrayList<Instruction>(Arrays.asList(new Instruction(Tag.CONSTANT, index))); }
         else { historyInst.remove(keyInst); }
         Tokenizer tokenizer = new Tokenizer();
         int indexHash = -1;
         int minus = 1;
         for (int i = 0; i < historyVar.size(); i++) {
             if (historyVar.get(i).getTag() == Tag.CONSTANT) {
                 if (tokenizer.isConstant(historyVar.get(i).getName())) {
                     if (treeInfo.compareHist(keyInst, new ArrayList<Instruction>(Arrays.asList(historyVar.get(i))))) {
                         indexHash = i - minus;
                         break;
                     }
                 }
                 else {
                     ArrayList<Instruction> keyInstVar = treeInfo.getHistoryVar(historyVar.get(i).getName());
                     if (keyInstVar != null) {
                         if (treeInfo.compareHist(keyInst, keyInstVar)) {
                             indexHash = i - minus;
                             break;
                         }
                     }
                 }
             }
             else if(historyVar.get(i).getTag() == Tag.CAST) {
                 minus++;
             }
         }
         historyInst.clear();
         String newVar;
         if(indexHash == -1) {
             int newIndex = historyVar.size() - minus;
             ArrayList<Object> valAux = new ArrayList<Object>();
             valAux.add(new ArrayList<Object>());
             if(refPropVar.equals("")) {
                 treeInfo.addInstructionVar(varName, new Instruction(Tag.CONSTANT, index));
                 treeInfo.addValueHist(valAux, varName);
                 newVar = varName + "[" + newIndex + "]";
             }
             else {
                 Var var = super.getFuncSummaries().get(fullConstructFunc).getVarThis(refPropVar);
                 var.addInstruction(new Instruction(Tag.CONSTANT, index));
                 var.addValue(valAux);
                 super.getFuncSummaries().get(treeInfo.getFuncSummary().getId()).putVarThis(refPropVar, var);
                 newVar = REFPROP + refPropVar + "[" + newIndex + "]";
             }
         }
         else { newVar = varName + "[" + indexHash + "]"; }
         ArrayList<Instruction> instructions = new ArrayList<Instruction>();
         instructions.add(new Instruction(Tag.ARRAY, newVar));
         historyInst.put("-1", instructions);
         return treeInfo;
     }

     public TreeInfo processMultiDimRefOfIndex(String varName, String stringIndex, String refPropVar, TreeInfo treeInfo, HashMap<String, ArrayList<Instruction>> historyInst,
                                               ArrayList<Instruction> historyVar) {
         ArrayList<Instruction> instructions = new ArrayList<Instruction>();
         String varInst = (!stringIndex.equals("")) ? varName + "[" + stringIndex + "]" : varName + "[-1]";
         instructions.add(new Instruction(Tag.ARRAY, varInst));
         historyInst.clear();
         historyInst.put(historyVar.get(0).getName(), instructions);
         if(NumberUtils.isCreatable(stringIndex)) {
             String[] pVar = historyVar.get(0).getName().split("\\[");
             String[] auxpVar = pVar[1].split("\\]");
             String firstVarName = pVar[0];
             int firstIndex = NumberUtils.isCreatable(auxpVar[0]) ? Integer.parseInt(auxpVar[0]) : -1;
             int secondIndex = Integer.parseInt(stringIndex);
             if(firstIndex != -1 && secondIndex != -1) {
                 ArrayList<Object> auxVal = treeInfo.getValueVarIndex(firstVarName, firstIndex);
                 if(auxVal.size() < secondIndex) {
                     int sizeVal = auxVal.size();
                     while(sizeVal < secondIndex) {
                         auxVal.add(new ArrayList<Object>());
                         sizeVal++;
                     }
                     treeInfo.updateValueIndexVar(firstVarName, firstIndex, auxVal);
                 }
             }
         }
        return treeInfo;
     }

     public TreeInfo transfArrayToHashmap(String varName, String stringIndex, String refPropVar, HashMap<String, ArrayList<Instruction>> historyInst,
                                          TreeInfo treeInfo) throws Exception {
        if(refPropVar.equals("")) {
            treeInfo.newVar(varName, new Instruction(Tag.ARRAY, HASHMAP));
            ArrayList<Object> valAux = new ArrayList<Object>();
            valAux.add(new ArrayList<Object>());
            treeInfo.addInstructionVar(varName, new Instruction(Tag.CONSTANT, stringIndex));
            treeInfo.addValueHist(valAux, varName);
            String varInst = varName + "[0]";
            ArrayList<Instruction> instructions = new ArrayList<Instruction>(Arrays.asList(new Instruction(Tag.ARRAY, varInst)));
            historyInst.clear();
            historyInst.put("-1", instructions);
        }
        else {
            Var var = new Var(refPropVar, new Instruction(Tag.ARRAY, HASHMAP));
            ArrayList<Object> valAux = new ArrayList<Object>();
            valAux.add(new ArrayList<Object>());
            var.addInstruction(new Instruction(Tag.CONSTANT, stringIndex));
            var.addValue(valAux);
            super.getFuncSummaries().get(treeInfo.getFuncSummary().getId()).putVarThis(refPropVar, var);
            String varInst = REFPROP + refPropVar + "[0]";
            ArrayList<Instruction> instructions = new ArrayList<Instruction>(Arrays.asList(new Instruction(Tag.ARRAY, varInst)));
            historyInst.clear();
            historyInst.put("-1", instructions);
        }
        return treeInfo;
     }

     public TreeInfo processRefOfIndexAux(String varName, String stringIndex, String refPropVar, HashMap<String, ArrayList<Instruction>> historyInst, TreeInfo treeInfo) {
         historyInst.clear();
         ArrayList<Instruction> instructions = new ArrayList<Instruction>();
         if(refPropVar.equals("")) {
             String varInst = (!stringIndex.equals("")) ? varName + "[" + stringIndex + "]" : varName + "[-1]";
             instructions.add(new Instruction(Tag.ARRAY, varInst));
             if(treeInfo.getValueVar(varName) != null) {
                 if (treeInfo.isIndexArray(varName)) {
                     //varInst com name normal
                     historyInst.put(varInst, instructions);
                 }
                 else { historyInst.put("-1", instructions);}
             }
             else {
                 historyInst.put("-1", instructions);
             }
             if(NumberUtils.isCreatable(stringIndex)) {
                 if(treeInfo.lastIndexArray(varName) < Integer.parseInt(stringIndex)) {
                     int lastIndex = treeInfo.lastIndexArray(varName);
                     while(lastIndex != Integer.parseInt(stringIndex)) {
                         ArrayList<Object> auxObj = new ArrayList<Object>();
                         treeInfo.addValueHist(auxObj, varName);
                         lastIndex++;
                     }
                 }
             }
         }
         else {
             String varInst = (!stringIndex.equals("")) ? REFPROP + refPropVar + "[" + stringIndex + "]" : REFPROP + refPropVar + varName + "[-1]";
             instructions.add(new Instruction(Tag.ARRAY, varInst));
             Var varRefProp = super.getFuncSummaries().get(treeInfo.getFuncSummary().getId()).getVarThis(refPropVar);
             if(varRefProp.getValue() != null) {
                 if (varRefProp.isArrayofArrays()) {
                     treeInfo.putVar(refPropVar, varRefProp);
                     historyInst.put(varInst, instructions);
                 }
                 else { historyInst.put("-1", instructions);}
             }
             else {
                 historyInst.put("-1", instructions);
             }
             if(NumberUtils.isCreatable(stringIndex)) {
                 if(varRefProp.lastIndexOfArray() < Integer.parseInt(stringIndex)) {
                     int lastIndex = varRefProp.lastIndexOfArray();
                     boolean putVar = false;
                     while(lastIndex != Integer.parseInt(stringIndex)) {
                         ArrayList<Object> auxObj = new ArrayList<Object>();
                         if(varRefProp.isArrayofArrays()) {
                             treeInfo.addValueHist(auxObj, refPropVar);
                         }
                         else {
                             putVar = true;
                             varRefProp.addValue(auxObj);
                         }
                         lastIndex++;
                     }
                     if(putVar) { super.getFuncSummaries().get(treeInfo.getFuncSummary().getId()).putVarThis(refPropVar, varRefProp); }
                 }
             }
         }
        return treeInfo;
     }

    public void processValueOfIndex(String index, String varName, HashMap<String, ArrayList<Instruction>> historyInst, TreeInfo treeInfo) throws Exception {
        String stringIndex = processIndex(index);
        if(NumberUtils.isCreatable(stringIndex)) {
            stringIndex = NumberUtils.createLong(stringIndex).toString();
        }
        ArrayList<Instruction> historyVar = treeInfo.getHistoryVar(varName);
        boolean isHash = false;
        if(historyVar != null) {
            if(!historyVar.isEmpty()) {
                if(historyVar.get(0).getTag() == Tag.ARRAY && historyVar.get(0).getName().equals(HASHMAP)) {
                    isHash = true;
                    ArrayList<Instruction> keyInst = historyInst.get(index);
                    if(keyInst == null) { keyInst = new ArrayList<Instruction>(Arrays.asList(new Instruction(Tag.CONSTANT, index))); }
                    else { historyInst.remove(keyInst); }
                    Tokenizer tokenizer = new Tokenizer();
                    int indexHash = -1;
                    for (int i = 0; i < historyVar.size(); i++) {
                        if (historyVar.get(i).getTag() == Tag.CONSTANT) {
                            if (tokenizer.isConstant(historyVar.get(i).getName())) {
                                if (index.equals(new ArrayList<Instruction>(Arrays.asList(historyVar.get(i))))) {
                                    indexHash = i - 1;
                                    break;
                                }
                            }
                            else {
                                ArrayList<Instruction> keyInstVar = treeInfo.getHistoryVar(historyVar.get(i).getName());
                                if (keyInstVar != null) {
                                    if (keyInst.equals(keyInstVar)) {
                                        indexHash = i - 1;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    historyInst.clear();
                    ArrayList<Instruction> instructions = new ArrayList<Instruction>();
                    varName = varName + "[" + indexHash + "]";
                    instructions.add(new Instruction(Tag.ARRAY));
                    historyInst.put(varName, instructions);
                }
            }
        }
        if(!isHash) {
            ArrayList<Instruction> instructions = new ArrayList<Instruction>();
            varName = (!stringIndex.equals("")) ? varName + "[" + stringIndex + "]" : varName + "[-1]";
            instructions.add(new Instruction(Tag.ARRAY));
            historyInst.put(varName, instructions);
        }
    }

    public void processValue(ArrayList<ArrayList<Object>> value, String varName, TreeInfo treeInfo) throws Exception {
        if (value != null) {
            //if there is a previous cast, it should still be considered as an array
            if ((treeInfo.getHistoryVar(varName).size() == 1 || treeInfo.getHistoryVar(varName).get(1).getTag() == Tag.CAST ||
                    treeInfo.getHistoryVar(varName).get(0).getName().equals(HASHMAP)) && (treeInfo.getHistoryVar(varName).get(0).getTag() == Tag.ARRAY)) {
                treeInfo.setValueVar(value, varName);
            }
            else {
                for(ArrayList<Object> indexValue : value) {
                    for(Object singleObj : indexValue) {
                        Instruction inst = (Instruction) singleObj;
                        treeInfo.addInstructionVar(varName, inst);
                    }
                }
                if (treeInfo.getHistoryVar(varName).get(0).getTag() == Tag.ARRAY) { treeInfo.getHistoryVar(varName).remove(0); }
            }
        }
    }

    public void processRealParamsVal(String varName, HashMap<String, ArrayList<Instruction>> historyInst, TreeInfo treeInfo,
                                     boolean isArray) throws Exception {
        Tokenizer tokenizer = new Tokenizer();
        if (tokenizer.isConstant(varName) && !isArray) {
            Instruction constInstruction = new Instruction(Tag.CONSTANT, varName);
            historyInst.put("1", new ArrayList<Instruction>(Arrays.asList(constInstruction)));
        }
        else {
            if(treeInfo.containsVar(varName)) {
                ArrayList<ArrayList<Object>> params = (ArrayList<ArrayList<Object>>) treeInfo.getValueVar(varName);
                ArrayList<Instruction> storyParams = new ArrayList<Instruction>();
                if(params != null) {
                    for (ArrayList<Object> singleParam : params) {
                        for (Object par : singleParam) {
                            if (par instanceof Instruction) {
                                Instruction inst = (Instruction) par;
                                storyParams.add(inst);
                            } else if (par instanceof ArrayList<?>) {
                                ArrayList<?> subPar = (ArrayList<?>) par;
                                for (Object subInstPar : subPar) {
                                    if (subInstPar instanceof Instruction) {
                                        Instruction inst = (Instruction) subInstPar;
                                        storyParams.add(inst);
                                    }
                                }
                            }
                        }
                    }
                    historyInst.put("params", storyParams);
                }
            }
        }
    }

    public TreeInfo processParamAssign(String realFuncName, int nodeID, TreeInfo treeInfo) {
        //This is a little bit hardcoded, but I think it works. Because whenever there is a function like exec
        //you are going to have something like this:
        //       $r14[1] = r4;
        //       $r14[2] = r9;
        //       $r15 = <$php_module_mf9ac249e2e9f49c78c4a10be9bcedaa8: jphp.runtime.invoke.cache.FunctionCallCache $CALL_FUNC_CACHE>;
        //       staticinvoke <jphp.runtime.invoke.InvokeHelper: jphp.runtime.Memory call(jphp.runtime.env.Environment,jphp.runtime.env.TraceInfo,java.lang.String,java.lang.String,jphp.runtime.Memory[],jphp.runtime.invoke.cache.FunctionCallCache,int)>(r0, $r16, "exec", "exec", $r14, $r15, 0);
        // And exec func is assigned to r4 var.
        int oldNodeID = nodeID - 3;
        if(oldNodeID >= 0 && oldNodeID < treeInfo.getInstructionStringSize()) {
            String oldInstruction = treeInfo.getInstructionString(oldNodeID);
            if(oldInstruction.contains("= ")) {
                String[] partsOldInstruction = oldInstruction.split("= ");
                String realVar = partsOldInstruction[1];
                ArrayList<Object> arrayInstructions = new ArrayList<Object>();
                ArrayList<Instruction> instFunc = treeInfo.isSource(realFuncName) ? new ArrayList<Instruction>(Arrays.asList(new Instruction(Tag.SOURCE, realFuncName))) :
                        new ArrayList<Instruction>(Arrays.asList(new Instruction(Tag.FUNCTION, realFuncName)));
                arrayInstructions.add(instFunc);
                treeInfo.newVarArray(realVar, arrayInstructions, new ArrayList<Instruction>(Arrays.asList(new Instruction(Tag.ARRAY))));
            }
        }
        return treeInfo;
    }

    public void transformFuncName(String funcName, StringBuilder funcNameAux) {
            if(funcName.equals(PHP_RUNTIME + "env.Environment.echo") || funcName.equals(PHP_RUNTIME + "env.Environment.die") ||
           funcName.equals("org.develnext.jphp.zend.ext.standard.FileFunctions.unlink") ||
           funcName.equals("org.develnext.jphp.zend.ext.standard.StringFunctions.strip_tags") ||
           funcName.equals("org.develnext.jphp.zend.ext.standard.StringFunctions.urlencode") ||
           funcName.equals("org.develnext.jphp.zend.ext.standard.StringFunctions.substr") ||
           funcName.equals("org.develnext.jphp.zend.ext.standard.StringFunctions.explode") ||
           funcName.equals("org.develnext.jphp.zend.ext.standard.StringFunctions.trim") ||
           funcName.equals(PHP_RUNTIME + "ext.core.LangFunctions.is_array") ||
           funcName.equals(PHP_RUNTIME + "ext.core.LangFunctions.is_bool") ||
           funcName.equals(PHP_RUNTIME + "ext.core.LangFunctions.is_callable") ||
           funcName.equals(PHP_RUNTIME + "ext.core.LangFunctions.is_countable") ||
           funcName.equals(PHP_RUNTIME + "ext.core.LangFunctions.is_double") ||
           funcName.equals(PHP_RUNTIME + "ext.core.LangFunctions.is_float") ||
           funcName.equals(PHP_RUNTIME + "ext.core.LangFunctions.is_int") ||
           funcName.equals(PHP_RUNTIME + "ext.core.LangFunctions.is_integer") ||
           funcName.equals(PHP_RUNTIME + "ext.core.LangFunctions.is_iterable") ||
           funcName.equals(PHP_RUNTIME + "ext.core.LangFunctions.is_long") ||
           funcName.equals(PHP_RUNTIME + "ext.core.LangFunctions.is_null") ||
           funcName.equals(PHP_RUNTIME + "ext.core.LangFunctions.is_numeric") ||
           funcName.equals(PHP_RUNTIME + "ext.core.LangFunctions.is_object") ||
           funcName.equals(PHP_RUNTIME + "ext.core.LangFunctions.is_real") ||
           funcName.equals(PHP_RUNTIME + "ext.core.LangFunctions.is_resource") ||
           funcName.equals(PHP_RUNTIME + "ext.core.LangFunctions.is_scalar") ||
           funcName.equals(PHP_RUNTIME + "ext.core.LangFunctions.is_string") ||
           funcName.equals(PHP_RUNTIME + "env.Environment.exit") ||
           funcName.equals(PHP_RUNTIME + "ext.core.LangFunctions.settype") ||
           funcName.equals(PHP_RUNTIME + "ext.core.LangFunctions.floatval") || funcName.equals(PHP_RUNTIME + "ext.core.StringFunctions.unserialize")) {
            String realFuncName = this.cropRealNameFunc(funcName);
            funcNameAux.append(realFuncName);
        }
    }



    @Override
    public ArrayList<Instruction> additionalProcessAss(ArrayList<Instruction> history, String varDep) {
        if (history == null) {
            if (varDep.equals(PHP_RUNTIME + "Memory.NULL")) {
                history = new ArrayList<Instruction>();
                Instruction instructionAux = new Instruction(Tag.CONSTANT);
                history.add(instructionAux);
            }
        }
        return history;
    }

    /**
     *******    Auxilary Methods   ******
     */
    private boolean isOperator(String funcName) {
        for(String funcOperator : this.funcOperators) {
            if(funcOperator.equals(funcName)) { return true; }
        }
        return false;
    }

    private boolean isConst(String funcName) {
        for(String funcConstant : this.funcsConsts) {
            if(funcConstant.equals(funcName)) { return true; }
        }
        return false;
    }

    private boolean isCast(String funcName) {
        for(String funcCast : this.casts) {
            if(funcCast.equals(funcName)) { return true; }
        }
        return false;
    }

    private boolean isAssOp(String funcName) {
        for(String funcAssOp : this.assOps) {
            if(funcAssOp.equals(funcName)) { return true; }
        }
        return false;
    }

    public String cropRealNameFunc(String funcName) {
        String realFuncName = "";
        if(funcName.contains(".")) {
            String[] partsFunc = funcName.split("\\.");
            realFuncName = partsFunc[partsFunc.length-1];
        }
        return realFuncName;
    }

}
