package jphp.runtime.memory.support.operation;

import jphp.runtime.Memory;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.memory.LongMemory;
import jphp.runtime.memory.support.MemoryOperation;

public class IntegerMemoryOperation extends MemoryOperation<Integer> {
    @Override
    public Class<?>[] getOperationClasses() {
        return new Class<?>[] { Integer.class, Integer.TYPE };
    }

    @Override
    public Integer convert(Environment env, TraceInfo trace, Memory arg) throws Throwable {
        return arg.toInteger();
    }

    @Override
    public Memory unconvert(Environment env, TraceInfo trace, Integer arg) throws Throwable {
        return arg == null ? Memory.NULL : LongMemory.valueOf(arg);
    }
}
