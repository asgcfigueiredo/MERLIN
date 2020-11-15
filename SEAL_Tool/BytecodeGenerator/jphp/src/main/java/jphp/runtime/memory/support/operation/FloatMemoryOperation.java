package jphp.runtime.memory.support.operation;

import jphp.runtime.Memory;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.memory.DoubleMemory;
import jphp.runtime.memory.support.MemoryOperation;

public class FloatMemoryOperation extends MemoryOperation<Float> {
    @Override
    public Class<?>[] getOperationClasses() {
        return new Class<?>[] { Float.class, Float.TYPE };
    }

    @Override
    public Float convert(Environment env, TraceInfo trace, Memory arg) throws Throwable {
        return arg.toFloat();
    }

    @Override
    public Memory unconvert(Environment env, TraceInfo trace, Float arg) throws Throwable {
        return DoubleMemory.valueOf(arg);
    }
}
