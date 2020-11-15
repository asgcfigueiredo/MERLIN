package jphp.runtime.memory.support.operation;

import jphp.runtime.Memory;
import jphp.runtime.common.HintType;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.memory.ArrayMemory;
import jphp.runtime.memory.support.MemoryOperation;
import jphp.runtime.reflection.ParameterEntity;

public class ArrayMemoryMemoryOperation extends MemoryOperation<ArrayMemory> {
    @Override
    public Class<?>[] getOperationClasses() {
        return new Class<?>[] { ArrayMemory.class };
    }

    @Override
    public ArrayMemory convert(Environment env, TraceInfo trace, Memory arg) throws Throwable {
        if (arg.isNull()) {
            return null;
        }

        return arg.toValue(ArrayMemory.class);
    }

    @Override
    public Memory unconvert(Environment env, TraceInfo trace, ArrayMemory arg) throws Throwable {
        return arg == null ? Memory.NULL : arg;
    }

    @Override
    public void applyTypeHinting(ParameterEntity parameter) {
        parameter.setType(HintType.ARRAY);
    }
}
