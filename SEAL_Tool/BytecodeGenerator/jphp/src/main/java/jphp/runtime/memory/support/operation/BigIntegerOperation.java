package jphp.runtime.memory.support.operation;

import jphp.runtime.Memory;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.memory.StringMemory;
import jphp.runtime.memory.support.MemoryOperation;

import java.math.BigInteger;

public class BigIntegerOperation extends MemoryOperation<BigInteger> {
    @Override
    public Class<?>[] getOperationClasses() {
        return new Class<?>[] { BigInteger.class };
    }

    @Override
    public BigInteger convert(Environment env, TraceInfo trace, Memory arg) throws Throwable {
        return new BigInteger(arg.toString());
    }

    @Override
    public Memory unconvert(Environment env, TraceInfo trace, BigInteger arg) throws Throwable {
        return StringMemory.valueOf(arg.toString());
    }
}
