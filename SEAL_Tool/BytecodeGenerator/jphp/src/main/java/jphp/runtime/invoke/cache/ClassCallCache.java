package jphp.runtime.invoke.cache;

import jphp.runtime.reflection.ClassEntity;

public class ClassCallCache extends CallCache<ClassEntity> {
    @Override
    public Item[] newArrayData(int length) {
        return new Item[length];
    }

    @Override
    public Item[][] newArrayArrayData(int length) {
        return new Item[length][];
    }
}
