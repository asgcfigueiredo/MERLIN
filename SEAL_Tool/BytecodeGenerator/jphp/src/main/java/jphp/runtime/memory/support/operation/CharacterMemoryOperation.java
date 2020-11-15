package jphp.runtime.memory.support.operation;

import jphp.runtime.Memory;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.memory.StringMemory;
import jphp.runtime.memory.support.MemoryOperation;

public class CharacterMemoryOperation extends MemoryOperation<Character> {
    @Override
    public Class<?>[] getOperationClasses() {
        return new Class<?>[] { Character.class, Character.TYPE };
    }

    @Override
    public Character convert(Environment env, TraceInfo trace, Memory arg) throws Throwable {
        return arg.toChar();
    }

    @Override
    public Memory unconvert(Environment env, TraceInfo trace, Character arg) throws Throwable {
        return StringMemory.valueOf(arg);
    }
}
