package org.develnext.jphp.core.compiler.jvm;

import jdk.internal.org.objectweb.asm.ClassReader;
import jphp.runtime.reflection.*;
import org.develnext.jphp.core.tokenizer.token.expr.value.YieldExprToken;
import jphp.runtime.Memory;
import jphp.runtime.env.Context;
import jphp.runtime.env.Environment;
import jphp.runtime.reflection.helper.ClosureEntity;
import jphp.runtime.common.Messages;
import jphp.runtime.common.Modifier;
import jphp.runtime.common.AbstractCompiler;
import org.develnext.jphp.core.compiler.jvm.statement.*;
import jphp.runtime.exceptions.CompileException;
import jphp.runtime.exceptions.support.ErrorType;
import org.develnext.jphp.core.syntax.SyntaxAnalyzer;
import org.develnext.jphp.core.tokenizer.TokenMeta;
import org.develnext.jphp.core.tokenizer.TokenType;
import org.develnext.jphp.core.tokenizer.Tokenizer;
import org.develnext.jphp.core.tokenizer.token.Token;
import org.develnext.jphp.core.tokenizer.token.expr.value.ClosureStmtToken;
import org.develnext.jphp.core.tokenizer.token.expr.value.NameToken;
import org.develnext.jphp.core.tokenizer.token.stmt.*;
import jphp.runtime.reflection.helper.GeneratorEntity;

import java.io.IOException;
import java.io.*;
import java.util.*;

public class JvmCompiler extends AbstractCompiler {
    protected final ModuleEntity module;
    protected NamespaceStmtToken namespace;
    protected List<DeclareStmtToken> declareStmtTokens = new ArrayList<DeclareStmtToken>();
    private List<ClassStmtCompiler> classes = new ArrayList<ClassStmtCompiler>();
    private Map<String, ConstantEntity> constants = new LinkedHashMap<String, ConstantEntity>();
    private Map<String, FunctionEntity> functions = new LinkedHashMap<String, FunctionEntity>();

    protected final SyntaxAnalyzer analyzer;
    protected final List<Token> tokens;

    protected YieldExprToken lastYield;

    public JvmCompiler(Environment environment, Context context) throws IOException {
        this(environment, context, new SyntaxAnalyzer(environment, new Tokenizer(context)));
    }

    public JvmCompiler(Environment environment, Context context, SyntaxAnalyzer analyzer) {
        super(environment, context);
        this.classes = new ArrayList<ClassStmtCompiler>();
        this.module = new ModuleEntity(context);
        this.module.setId( scope.nextModuleIndex() );

        this.tokens = analyzer.getTree();
        this.analyzer = analyzer;
    }

    public SyntaxAnalyzer getAnalyzer() {
        return analyzer;
    }

    public ConstantEntity findConstant(String fullName){
        return constants.get(fullName.toLowerCase());
    }

    public FunctionEntity findFunction(String name){
        return functions.get(name.toLowerCase());
    }

    public NamespaceStmtToken getNamespace() {
        return namespace;
    }

    public void setNamespace(NamespaceStmtToken namespace) {
        this.namespace = namespace;
    }

    public YieldExprToken getLastYield() {
        return lastYield;
    }

    public void setLastYield(YieldExprToken lastYield) {
        this.lastYield = lastYield;
    }

    public ClassEntity compileClass(ClassStmtToken clazz, boolean external) {
        ClassStmtCompiler cmp = new ClassStmtCompiler(this, clazz);
        cmp.setExternal(external);
        return cmp.compile();
    }

    public ClassEntity compileClass(ClassStmtToken clazz){
        return compileClass(clazz, false);
    }

    public FunctionEntity compileFunction(FunctionStmtToken function){
        FunctionStmtCompiler cmp = new FunctionStmtCompiler(this, function);
        return cmp.compile();
    }

    public List<ConstantEntity> compileConstant(ConstStmtToken constant){
        List<ConstantEntity> result = new ArrayList<ConstantEntity>();
        for(ConstStmtToken.Item el : constant.items){
            ConstantEntity constantEntity = new ConstantEntity(getContext());
            constantEntity.setName(el.getFulledName());
            constantEntity.setModule(module);
            constantEntity.setTrace(el.name.toTraceInfo(context));

            ExpressionStmtCompiler expressionStmtCompiler = new ExpressionStmtCompiler(this);
            Memory memory = expressionStmtCompiler.writeExpression(el.value, true, true, false);
            if (memory == null){
                throw new CompileException(
                        Messages.ERR_EXPECTED_CONST_VALUE.fetch(el.getFulledName()),
                        constant.toTraceInfo(context)
                );
            }

            constantEntity.setValue(memory);
            result.add(constantEntity);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public MethodEntity compileMethod(ClassStmtCompiler clazzCompiler, MethodStmtToken method, boolean external,
                                      GeneratorEntity generatorEntity) {
        MethodStmtCompiler compiler = new MethodStmtCompiler(clazzCompiler, method);
        compiler.setExternal(external);
        compiler.setGeneratorEntity(generatorEntity);
        clazzCompiler.node.methods.add(compiler.node);
        return compiler.compile();
    }

    public MethodEntity compileMethod(ClassStmtCompiler clazzCompiler, MethodStmtToken method){
        return compileMethod(clazzCompiler, method, false, null);
    }

    public void compileExpression(MethodStmtCompiler method, ExprStmtToken expression) {
        new ExpressionStmtCompiler(method, expression).compile();
    }

    public List<ExprStmtToken> process(List<Token> tokens, NamespaceStmtToken namespace){
        List<ExprStmtToken> externalCode = new ArrayList<ExprStmtToken>();

        // write constants
        for(ConstStmtToken constant : analyzer.getConstants()){
            List<ConstantEntity> items = compileConstant(constant);

            for(ConstantEntity el : items){
                if (!constants.containsKey(el.getLowerName())){
                    module.addConstant(el);

                    if (scope.findUserConstant(el.getName()) != null){
                        environment.error(el.getTrace(), ErrorType.E_ERROR,
                            Messages.ERR_CANNOT_REDECLARE_CONSTANT,
                            el.getName()
                        );
                    }

                    constants.put(el.getLowerName(), el);
                } else {
                    environment.error(el.getTrace(), ErrorType.E_ERROR,
                        Messages.ERR_CANNOT_REDECLARE_CONSTANT,
                        el.getName()
                    );
                }
            }
        }

        // write closures
        for(ClosureStmtToken closure : analyzer.getClosures()){
            ClosureEntity closureEntity = new ClosureStmtCompiler(this, closure).compile();
            module.addClosure(closureEntity);
        }

        for (FunctionStmtToken function : analyzer.getFunctions()){
            if (!function.isStatic()) {
                FunctionEntity entity = compileFunction(function);
                entity.setStatic(function.isStatic());
                module.addFunction(entity);
            }
        }

        for(Token token : tokens) {
            if (token instanceof NamespaceStmtToken) {
                setNamespace((NamespaceStmtToken) token);
            } if (token instanceof DeclareStmtToken) {
                declareStmtTokens.add((DeclareStmtToken) token);
            } if (token instanceof ClassStmtToken){
                ClassStmtCompiler cmp = new ClassStmtCompiler(this, (ClassStmtToken)token);
                cmp.setExternal(true);
                ClassEntity entity = cmp.compile();
                entity.setStatic(true);
                module.addClass(entity);

                if (cmp.isInitDynamicExists()){
                    externalCode.add(new ExprStmtToken(getEnvironment(), getContext(),
                        new ClassInitEnvironment((ClassStmtToken)token, entity)
                    ));
                }
            } else if (token instanceof FunctionStmtToken){
                FunctionEntity entity = compileFunction((FunctionStmtToken)token);
                entity.setStatic(true);
                module.addFunction(entity);
                functions.put(entity.getLowerName(), entity);
            } else if (token instanceof ExprStmtToken){
                externalCode.add((ExprStmtToken)token);
            } else
                externalCode.add(new ExprStmtToken(getEnvironment(), getContext(), token));
        }

        return externalCode;
    }

    @Override
    public ModuleEntity compile(boolean autoRegister) {
        this.classes = new ArrayList<ClassStmtCompiler>();
        module.setInternalName("$php_module_m" + UUID.randomUUID().toString().replace("-", ""));
        String fileDirectory = "";
        Properties props = new Properties();
        try {
            props.load(JvmCompiler.class.getClassLoader().getResourceAsStream("project.properties"));
            fileDirectory = props.getProperty("fileDirectory");
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<ExprStmtToken> externalCode = process(tokens, NamespaceStmtToken.getDefault());

        for (DeclareStmtToken declare : declareStmtTokens) {
            String name = declare.getName().getName();

            ExpressionStmtCompiler expressionStmtCompiler = new ExpressionStmtCompiler(this);

            Memory value = expressionStmtCompiler.tryCalculateExpression(declare.getValue());

            if ("strict_types".equals(name.toLowerCase())) {
                module.setStrictTypes(value.toBoolean());
            } else {
                environment.error(declare.getValue().getMeta().toTraceInfo(context), ErrorType.E_ERROR,
                        Messages.ERR_INVALID_DECLARE_CONSTANT,
                        name
                );
            }
        }

        NamespaceStmtToken namespace = NamespaceStmtToken.getDefault();
        ClassStmtToken token = new ClassStmtToken(TokenMeta.of( module.getName() ));
        token.setFinal(true);
        token.setName(new NameToken(TokenMeta.of( module.getInternalName() )));
        token.setNamespace(namespace);

        MethodStmtToken methodToken = new MethodStmtToken( token.getMeta() );
        methodToken.setFinal(true);
        methodToken.setClazz(token);
        methodToken.setModifier(Modifier.PUBLIC);
        methodToken.setStatic(true);
        methodToken.setDynamicLocal(true);
        methodToken.setName((NameToken) Token.of("__include"));
        methodToken.setArguments(new ArrayList<ArgumentStmtToken>());
        methodToken.setLocal(analyzer.getScope().getVariables());
        methodToken.setLabels(analyzer.getScope().getLabels());

        methodToken.setBody(BodyStmtToken.of(externalCode));

        token.setMethods(Arrays.asList(methodToken));
        

        ClassStmtCompiler classStmtCompiler = new ClassStmtCompiler(this, token);
        classStmtCompiler.setExternal(true);
        classStmtCompiler.setSystem(true);
        System.out.println("Function name: " + classStmtCompiler.getFunctionName());

        module.setData(classStmtCompiler.compile().getData());


        for(String functionName: functions.keySet()) {
            System.out.println("Function processed: " + functionName);
            FunctionEntity functionEntity = functions.get(functionName);
            String functionFilename = fileDirectory + "\\output\\" + functionName + ".class";
            try{
                FileOutputStream  myObj1 = new FileOutputStream (functionFilename);
                myObj1.write(functionEntity.getData());
                myObj1.close();
            }
            catch (FileNotFoundException  ex)  {   ex.printStackTrace(); }
            catch (IOException ex)  {  ex.printStackTrace();}
        }

        for(ClassEntity c : module.getClasses()) {
            System.out.println("Class processed: " + c.getName());
            String className = fileDirectory + "\\output\\";
            if(c.getName().contains("\\")) {
                int auxindCName = c.getName().lastIndexOf("\\");
                String auxCName = c.getName().substring(auxindCName + 1);
                className = className + auxCName + ".class";
            }
            else {
                className = className + c.getName() + ".class";
            }
            try{
                ClassReader classReader = new ClassReader(c.getData());
                FileOutputStream  myObj1 = new FileOutputStream (className);
                myObj1.write(c.getData());
                myObj1.close();
            }
            catch (FileNotFoundException  ex)  {   ex.printStackTrace(); }
            catch (IOException ex)  {  ex.printStackTrace();}
        }
    
        //FunctionStmtToken tokenF = new FunctionStmtToken(TokenMeta.of(n));
        //FunctionStmtCompiler functionStmtCompiler = new FunctionStmtCompiler(this, tokenF);
        //functionStmtCompiler.compile().getData();
        // ..

        if (autoRegister)
            scope.addUserModule(module);

        return module;
    }

    public ModuleEntity getModule() {
        return module;
    }

    public List<ClassStmtCompiler> getClasses() {
        return classes;
    }

    public String getSourceFile() {
        return context.getFileName();
    }

    public class ClassInitEnvironment extends StmtToken {
        protected final ClassStmtToken token;
        protected final ClassEntity entity;

        public ClassInitEnvironment(ClassStmtToken token, ClassEntity clazz) {
            super(token.getMeta(), TokenType.T_J_CUSTOM);
            this.token = token;
            this.entity = clazz;
        }

        public ClassStmtToken getToken() {
            return token;
        }

        public ClassEntity getEntity() {
            return entity;
        }
    }
}
