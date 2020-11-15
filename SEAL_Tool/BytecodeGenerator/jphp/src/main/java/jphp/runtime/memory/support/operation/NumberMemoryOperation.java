package jphp.runtime.memory.support.operation;

import jphp.runtime.Memory;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.memory.DoubleMemory;
import jphp.runtime.memory.LongMemory;
import jphp.runtime.memory.support.MemoryOperation;

public class NumberMemoryOperation extends MemoryOperation<Number> {
    @Override
    public Class<?>[] getOperationClasses() {
        return new Class<?>[] { Number.class };
    }

    @Override
    public Number convert(Environment env, TraceInfo trace, Memory arg) throws Throwable {
        switch (arg.getRealType()) {
            case DOUBLE: return arg.toDouble();
            case INT: return arg.toInteger();
            default:
                return convert(env, trace, arg.toNumeric());
        }
    }

    @Override
    public Memory unconvert(Environment env, TraceInfo trace, Number arg) throws Throwable {
        if (arg instanceof Double || arg instanceof Float) {
            return DoubleMemory.valueOf(arg.doubleValue());
        } else {
            return LongMemory.valueOf(arg.longValue());
        }
    }
}
