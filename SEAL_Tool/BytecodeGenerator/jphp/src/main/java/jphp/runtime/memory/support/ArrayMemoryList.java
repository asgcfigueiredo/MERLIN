package jphp.runtime.memory.support;

import jphp.runtime.Memory;

import java.util.*;


public class ArrayMemoryList<E extends Memory> extends GapList<E> {
    public ArrayMemoryList() {
    }

    public ArrayMemoryList(int capacity) {
        super(capacity);
    }

    public ArrayMemoryList(Collection<? extends E> coll) {
        super(coll);
    }
}
