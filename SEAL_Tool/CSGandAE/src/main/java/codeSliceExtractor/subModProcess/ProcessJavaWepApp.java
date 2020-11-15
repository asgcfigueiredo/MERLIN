package codeSliceExtractor.subModProcess;

import codeSliceExtractor.VulnerabilityInfo;
import codeSliceExtractor.processJimple.codeData.Instruction;
import codeSliceExtractor.processJimple.codeData.Var;
import codeSliceExtractor.processJimple.processTrees.TreeInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ProcessJavaWepApp extends ProcessWebApp {
    private HashMap<String, Var> attributes = new HashMap<String, Var>();
    private String fileForward;

    public ProcessJavaWepApp() {
        super();
    }

    public static ProcessJavaWepApp getInstance() {
        if (single_instance == null) {
            single_instance = new ProcessJavaWepApp();
        }
        return (ProcessJavaWepApp) single_instance;
    }

    @Override
    public void init(ArrayList<String> files,HashMap<String, VulnerabilityInfo> sensitiveSinks, String baseDir) {
        super.init(files, sensitiveSinks, baseDir);
        super.setNoProcessFuncs(new ArrayList<String>(Arrays.asList("<clinit>()", "getDependants()", "getPackageImports()", "getClassImports()",
                "_jsp_getExpressionFactory()", "_jspInit()", "_jspDestroy()")));
    }

    @Override
    public TreeInfo additionalProcessFunc(String funcName, ArrayList<String> nameParams, int nodeID, HashMap<String,
                    ArrayList<Instruction>> historyInst, ArrayList<ArrayList<Object>> value, TreeInfo treeInfo, StringBuilder funcNameAux) throws Exception {
        if(funcName.equals("javax.servlet.http.HttpServletRequest.setAttribute")) {
            if(nameParams.size() == 2) {
                String varName = nameParams.get(0);
                Var var = treeInfo.getVar(nameParams.get(1));
                this.attributes.put(varName, var);
            }
        }
        else if(funcName.equals("javax.servlet.http.HttpServletRequest.getRequestDispatcher")) {
            this.fileForward = nameParams.get(0);
        }
        else if(funcName.equals("javax.servlet.RequestDispatcher.forward")) {
            String auxFunc = this.fileForward.replace("\"", "");
            String[] auxFunc2 = auxFunc.split("\\.");
            String realFuncName = auxFunc2[0] + "._jspService";
            funcNameAux.append(realFuncName);
            treeInfo.setParamsFunc(nodeID, "javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse");
        }
        else if(funcName.equals("javax.servlet.http.HttpServletRequest.getAttribute")) {
            historyInst.clear();
            Var varParam = this.attributes.get(nameParams.get(0));
            if(varParam != null) {
                ArrayList<Instruction> instructions = new ArrayList<Instruction>();
                for(Instruction inst : varParam.getInstructions()) {
                    instructions.add(inst);
                }
                historyInst.put("0", instructions);
            }
        }
        return treeInfo;
    }


}
