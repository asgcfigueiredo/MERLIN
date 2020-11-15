package jphp.runtime.memory.helper;

import jphp.runtime.Memory;
import jphp.runtime.memory.ReferenceMemory;

public class ShortcutMemory extends ReferenceMemory {

    public ShortcutMemory(ReferenceMemory value) {
        super(value);
    }

    @Override
    public Memory toImmutable() {
        return getValue();
    }

    @Override
    public boolean isShortcut() {
        return true;
    }
}
