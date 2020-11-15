package jphp.runtime.memory.support.operation;

import jphp.runtime.Memory;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.memory.StringMemory;
import jphp.runtime.memory.support.MemoryOperation;

import java.math.BigDecimal;

public class BigDecimalOperation extends MemoryOperation<BigDecimal> {
    @Override
    public Class<?>[] getOperationClasses() {
        return new Class<?>[] { BigDecimal.class };
    }

    @Override
    public BigDecimal convert(Environment env, TraceInfo trace, Memory arg) throws Throwable {
        return new BigDecimal(arg.toString());
    }

    @Override
    public Memory unconvert(Environment env, TraceInfo trace, BigDecimal arg) throws Throwable {
        return StringMemory.valueOf(arg.toString());
    }
}
