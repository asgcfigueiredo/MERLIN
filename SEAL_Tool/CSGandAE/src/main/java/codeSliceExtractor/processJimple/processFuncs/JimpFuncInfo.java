package codeSliceExtractor.processJimple.processFuncs;

/**
 * Information regarding each instruction in jimple containing a function
 */
public class JimpFuncInfo {
    private String funcName = "";
    private String varFunc = "";
    private String paramsString = "";

    public JimpFuncInfo() {}

    public void setFuncName(String funcName) { this.funcName = funcName; }

    public void setVarFunc(String varFunc) { this.varFunc = varFunc; }

    public void setParamsString(String paramsString) { this.paramsString = paramsString; }

    public String getFuncName() { return this.funcName; }

    public String getVarFunc() { return this.varFunc; }

    public String getParamsString() { return this.paramsString; }
}
