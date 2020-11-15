package jphp.runtime.memory.support.operation;

import jphp.runtime.Memory;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.memory.TrueMemory;
import jphp.runtime.memory.support.MemoryOperation;

public class BooleanMemoryOperation extends MemoryOperation<Boolean> {
    @Override
    public Class<?>[] getOperationClasses() {
        return new Class<?>[] { Boolean.class, Boolean.TYPE };
    }

    @Override
    public Boolean convert(Environment env, TraceInfo trace, Memory arg) throws Throwable {
        return arg.toBoolean();
    }

    @Override
    public Memory unconvert(Environment env, TraceInfo trace, Boolean arg) throws Throwable {
        return arg == null ? Memory.NULL : TrueMemory.valueOf(arg);
    }
}
