package jphp.runtime.memory.support.operation;

import jphp.runtime.Memory;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.memory.StringMemory;
import jphp.runtime.memory.support.MemoryOperation;

public class CharSequenceMemoryOperation extends MemoryOperation<CharSequence> {
    @Override
    public Class<?>[] getOperationClasses() {
        return new Class<?>[] { CharSequence.class };
    }

    @Override
    public CharSequence convert(Environment env, TraceInfo trace, Memory arg) throws Throwable {
        return arg.toString();
    }

    @Override
    public Memory unconvert(Environment env, TraceInfo trace, CharSequence arg) throws Throwable {
        return StringMemory.valueOf(arg.toString());
    }
}
