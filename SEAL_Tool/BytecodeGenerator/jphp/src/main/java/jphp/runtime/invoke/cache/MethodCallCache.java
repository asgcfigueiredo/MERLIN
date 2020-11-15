package jphp.runtime.invoke.cache;

import jphp.runtime.reflection.MethodEntity;

public class MethodCallCache extends CallCache<MethodEntity> {
    @Override
    public Item[] newArrayData(int length) {
        return new Item[length];
    }

    @Override
    public Item[][] newArrayArrayData(int length) {
        return new Item[length][];
    }
}
