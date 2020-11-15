package jphp.runtime.memory.support.operation;

import jphp.runtime.Memory;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.memory.support.MemoryOperation;

public class VoidMemoryOperation extends MemoryOperation {
    @Override
    public Class<?>[] getOperationClasses() {
        return new Class<?>[] { void.class, Void.class };
    }

    @Override
    public Object convert(Environment env, TraceInfo trace, Memory arg) throws Throwable {
        return null;
    }

    @Override
    public Memory unconvert(Environment env, TraceInfo trace, Object arg) throws Throwable {
        return Memory.NULL;
    }
}
