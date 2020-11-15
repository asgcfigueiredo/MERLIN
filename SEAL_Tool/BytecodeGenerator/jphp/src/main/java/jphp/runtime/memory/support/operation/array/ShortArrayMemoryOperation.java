package jphp.runtime.memory.support.operation.array;

import jphp.runtime.Memory;
import jphp.runtime.common.HintType;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.memory.ArrayMemory;
import jphp.runtime.memory.support.MemoryOperation;
import jphp.runtime.reflection.ParameterEntity;

public class ShortArrayMemoryOperation extends MemoryOperation<short[]> {
    @Override
    public Class<?>[] getOperationClasses() {
        return new Class<?>[] { short[].class };
    }

    @Override
    public short[] convert(Environment env, TraceInfo trace, Memory arg) throws Throwable {
        short[] result = new short[((ArrayMemory)arg).size()];

        int i = 0;
        for (Memory el : (ArrayMemory)arg) {
            result[i++] = (short) el.toInteger();
        }

        return result;
    }

    @Override
    public Memory unconvert(Environment env, TraceInfo trace, short[] arg) throws Throwable {
        return ArrayMemory.ofShorts(arg).toConstant();
    }

    @Override
    public void applyTypeHinting(ParameterEntity parameter) {
        parameter.setType(HintType.ARRAY);
    }
}
