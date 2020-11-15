package jphp.runtime.memory.support.operation;

import jphp.runtime.Memory;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.memory.LongMemory;
import jphp.runtime.memory.support.MemoryOperation;

public class ByteMemoryOperation extends MemoryOperation<Byte> {
    @Override
    public Class<?>[] getOperationClasses() {
        return new Class<?>[] { Byte.class, Byte.TYPE };
    }

    @Override
    public Byte convert(Environment env, TraceInfo trace, Memory arg) throws Throwable {
        return (byte)arg.toInteger();
    }

    @Override
    public Memory unconvert(Environment env, TraceInfo trace, Byte arg) throws Throwable {
        return LongMemory.valueOf(arg);
    }
}
