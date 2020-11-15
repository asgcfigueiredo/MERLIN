package jphp.runtime.memory.helper;

import jphp.runtime.Memory;
import jphp.runtime.memory.NullMemory;

public class UndefinedMemory extends NullMemory {

    public static final UndefinedMemory INSTANCE = new UndefinedMemory();

    @Override
    public Memory toImmutable() {
        return UNDEFINED;
    }
}
