package jphp.runtime.memory.support.operation.array;

import jphp.runtime.Memory;
import jphp.runtime.common.HintType;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.memory.ArrayMemory;
import jphp.runtime.memory.support.MemoryOperation;
import jphp.runtime.reflection.ParameterEntity;

public class CharArrayMemoryOperation extends MemoryOperation<char[]> {
    @Override
    public Class<?>[] getOperationClasses() {
        return new Class<?>[] { char[].class };
    }

    @Override
    public char[] convert(Environment env, TraceInfo trace, Memory arg) throws Throwable {
        char[] result = new char[((ArrayMemory)arg).size()];

        int i = 0;
        for (Memory el : (ArrayMemory)arg) {
            result[i++] = el.toChar();
        }

        return result;
    }

    @Override
    public Memory unconvert(Environment env, TraceInfo trace, char[] arg) throws Throwable {
        return ArrayMemory.ofChars(arg).toConstant();
    }

    @Override
    public void applyTypeHinting(ParameterEntity parameter) {
        parameter.setType(HintType.ARRAY);
    }
}
