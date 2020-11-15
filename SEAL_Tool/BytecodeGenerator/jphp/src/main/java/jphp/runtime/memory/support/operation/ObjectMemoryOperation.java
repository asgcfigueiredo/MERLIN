package jphp.runtime.memory.support.operation;

import jphp.runtime.Memory;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.memory.support.MemoryOperation;

public class ObjectMemoryOperation extends MemoryOperation<Object> {
    @Override
    public Class<?>[] getOperationClasses() {
        return new Class<?>[] { Object.class };
    }

    @Override
    public Object convert(Environment env, TraceInfo trace, Memory arg) throws Throwable {
        return Memory.unwrap(env, arg);
    }

    @Override
    public Memory unconvert(Environment env, TraceInfo trace, Object arg) throws Throwable {
        return Memory.wrap(env, arg);
    }
}
