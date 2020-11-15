package jphp.runtime.memory.helper;

import jphp.runtime.Memory;
import jphp.runtime.memory.ArrayMemory;
import jphp.runtime.memory.LongMemory;
import jphp.runtime.memory.ReferenceMemory;
import jphp.runtime.memory.StringMemory;

public class ArrayKeyMemory extends ReferenceMemory {
    private ArrayMemory array;

    public ArrayKeyMemory(ArrayMemory array, Memory key) {
        super(key);
        this.array = array;
    }

    @Override
    public Memory assign(Memory memory) {
        array.renameKey(getValue(), memory);
        return super.assign(memory);
    }

    @Override
    public Memory assign(long memory) {
        array.renameKey(getValue(), LongMemory.valueOf(memory));
        return super.assign(memory);
    }

    @Override
    public Memory assign(String memory) {
        array.renameKey(getValue(), new StringMemory(memory));
        return super.assign(memory);
    }

    @Override
    public Memory assign(boolean memory) {
        return assign(memory ? 1 : 0);
    }

    @Override
    public Memory assign(double memory) {
        return assign((long)memory);
    }
}
