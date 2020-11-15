package jphp.runtime.memory.support.operation.array;

import jphp.runtime.Memory;
import jphp.runtime.common.HintType;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.memory.ArrayMemory;
import jphp.runtime.memory.support.MemoryOperation;
import jphp.runtime.reflection.ParameterEntity;

public class DoubleArrayMemoryOperation extends MemoryOperation<double[]> {
    @Override
    public Class<?>[] getOperationClasses() {
        return new Class<?>[] { double[].class };
    }

    @Override
    public double[] convert(Environment env, TraceInfo trace, Memory arg) throws Throwable {
        return arg.toValue(ArrayMemory.class).toDoubleArray();
    }

    @Override
    public Memory unconvert(Environment env, TraceInfo trace, double[] arg) throws Throwable {
        return ArrayMemory.ofDoubles(arg).toConstant();
    }

    @Override
    public void applyTypeHinting(ParameterEntity parameter) {
        parameter.setType(HintType.ARRAY);
    }
}
