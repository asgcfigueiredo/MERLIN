package jphp.runtime.memory.support.operation;

import jphp.runtime.Memory;
import jphp.runtime.common.HintType;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.exceptions.CriticalException;
import jphp.runtime.invoke.Invoker;
import jphp.runtime.memory.support.MemoryOperation;
import jphp.runtime.reflection.ParameterEntity;

public class InvokerMemoryOperation extends MemoryOperation<Invoker> {
    @Override
    public Class<?>[] getOperationClasses() {
        return new Class<?>[] { Invoker.class };
    }

    @Override
    public Invoker convert(Environment env, TraceInfo trace, Memory arg) throws Throwable {
        return arg.toInvoker(env);
    }

    @Override
    public Memory unconvert(Environment env, TraceInfo trace, Invoker arg) throws Throwable {
        throw new CriticalException("Unsupported operation");
    }

    @Override
    public void applyTypeHinting(ParameterEntity parameter) {
        parameter.setType(HintType.CALLABLE);
    }
}
