package jphp.runtime.invoke;

import jphp.runtime.reflection.FunctionEntity;
import jphp.runtime.reflection.ParameterEntity;
import jphp.runtime.Memory;
import jphp.runtime.common.Messages;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;

public class FunctionInvoker extends Invoker {
    private final FunctionEntity entity;
    protected final Environment env;

    public FunctionInvoker(Environment env, TraceInfo trace, FunctionEntity function) {
        super(env, trace);
        entity = function;
        this.env = env;
    }

    @Override
    public ParameterEntity[] getParameters() {
        return entity.getParameters();
    }

    @Override
    public String getName() {
        return entity.getName();
    }

    @Override
    public int getArgumentCount() {
        return entity.getParameters() == null ? 0 : entity.getParameters().length;
    }

    public FunctionEntity getFunction() {
        return entity;
    }

    @Override
    public void pushCall(TraceInfo trace, Memory[] args) {
        env.pushCall(trace, null, args, entity.getName(), null, null);
    }

    @Override
    protected Memory invoke(Memory... args) throws Throwable {
        return InvokeHelper.call(env, trace, entity, args);
    }

    @Override
    public int canAccess(Environment env) {
        return 0;
    }

    public static FunctionInvoker valueOf(Environment env, TraceInfo trace, String name){
        FunctionEntity functionEntity = env.fetchFunction(name);
        if (functionEntity == null){
            if (trace == null)
                return null;
            env.error(trace, Messages.ERR_CALL_TO_UNDEFINED_FUNCTION.fetch(name));
        }

        return new FunctionInvoker(env, trace, functionEntity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FunctionInvoker)) return false;

        FunctionInvoker that = (FunctionInvoker) o;

        return entity.equals(that.entity);
    }

    @Override
    public int hashCode() {
        return entity.hashCode();
    }
}
