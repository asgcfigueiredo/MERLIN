package jphp.runtime.memory.support.operation;

import jphp.runtime.Memory;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.memory.LongMemory;
import jphp.runtime.memory.support.MemoryOperation;

public class ShortMemoryOperation extends MemoryOperation<Short> {
    @Override
    public Class<?>[] getOperationClasses() {
        return new Class<?>[] { Short.class, Short.TYPE };
    }

    @Override
    public Short convert(Environment env, TraceInfo trace, Memory arg) throws Throwable {
        return (short)arg.toInteger();
    }

    @Override
    public Memory unconvert(Environment env, TraceInfo trace, Short arg) throws Throwable {
        return LongMemory.valueOf(arg);
    }
}
