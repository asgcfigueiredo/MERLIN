package jphp.runtime.invoke;

import jphp.runtime.ext.core.classes.WrapInvoker;
import jphp.runtime.memory.ObjectMemory;
import jphp.runtime.reflection.ParameterEntity;
import jphp.runtime.Memory;
import jphp.runtime.common.Callback;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;

public class RunnableInvoker extends Invoker {
    private final Callback<Memory, Memory[]> runnable;

    public RunnableInvoker(Environment env, Callback<Memory, Memory[]> runnable) {
        super(env, env.trace());
        this.runnable = runnable;
    }

    @Override
    public ParameterEntity[] getParameters() {
        return new ParameterEntity[0];
    }

    @Override
    public String getName() {
        return "Closure";
    }

    @Override
    public int getArgumentCount() {
        return 0;
    }

    @Override
    protected void pushCall(TraceInfo trace, Memory[] args) {
    }

    @Override
    protected Memory invoke(Memory... args) throws Throwable {
        return runnable.call(args);
    }

    @Override
    public int canAccess(Environment env) {
        return 0;
    }

    public static Memory create(Environment env, Callback<Memory, Memory[]> runnable) {
        RunnableInvoker invoker = new RunnableInvoker(env, runnable);
        WrapInvoker wrapInvoker = new WrapInvoker(env, invoker);

        return ObjectMemory.valueOf(wrapInvoker);
    }
}
