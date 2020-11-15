package jphp.runtime.common;

import jphp.runtime.reflection.ModuleEntity;
import jphp.runtime.env.CompileScope;
import jphp.runtime.env.Context;
import jphp.runtime.env.Environment;

abstract public class AbstractCompiler {

    protected final Environment environment;
    protected final CompileScope scope;
    protected final Context context;

    public AbstractCompiler(Environment environment, Context context){
        this.context = context;
        this.scope = environment.getScope();
        this.environment = environment;
    }

    public Context getContext() {
        return context;
    }

    public CompileScope getScope() {
        return scope;
    }

    abstract public ModuleEntity compile(boolean autoRegister);

    public ModuleEntity compile(){
        return compile(true);
    }

    public Environment getEnvironment() {
        return environment;
    }
}
