package jphp.runtime.memory.support.operation;

import jphp.runtime.Memory;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.memory.StringMemory;
import jphp.runtime.memory.support.MemoryOperation;

public class StringMemoryOperation extends MemoryOperation<String> {
    @Override
    public Class<?>[] getOperationClasses() {
        return new Class<?>[] { String.class };
    }

    @Override
    public String convert(Environment env, TraceInfo trace, Memory arg) throws Throwable {
        return arg.toString();
    }

    @Override
    public Memory unconvert(Environment env, TraceInfo trace, String arg) throws Throwable {
        return StringMemory.valueOf(arg);
    }
}
