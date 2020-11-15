package jphp.runtime.memory.support.operation;

import jphp.runtime.Memory;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.memory.DoubleMemory;
import jphp.runtime.memory.support.MemoryOperation;

public class DoubleMemoryOperation extends MemoryOperation<Double> {
    @Override
    public Class<?>[] getOperationClasses() {
        return new Class<?>[] { Double.class, Double.TYPE };
    }

    @Override
    public Double convert(Environment env, TraceInfo trace, Memory arg) throws Throwable {
        return arg.toDouble();
    }

    @Override
    public Memory unconvert(Environment env, TraceInfo trace, Double arg) throws Throwable {
        return arg == null ? Memory.NULL : DoubleMemory.valueOf(arg);
    }
}
