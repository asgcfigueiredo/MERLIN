package jphp.runtime.memory.support.operation;

import jphp.runtime.Memory;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.memory.LongMemory;
import jphp.runtime.memory.support.MemoryOperation;

public class LongMemoryOperation extends MemoryOperation<Long> {
    @Override
    public Class<?>[] getOperationClasses() {
        return new Class<?>[] { Long.class, Long.TYPE };
    }

    @Override
    public Long convert(Environment env, TraceInfo trace, Memory arg) throws Throwable {
        return arg.toLong();
    }

    @Override
    public Memory unconvert(Environment env, TraceInfo trace, Long arg) throws Throwable {
        return LongMemory.valueOf(arg);
    }
}
