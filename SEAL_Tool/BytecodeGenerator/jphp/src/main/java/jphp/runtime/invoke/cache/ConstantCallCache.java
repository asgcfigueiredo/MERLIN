package jphp.runtime.invoke.cache;

import jphp.runtime.reflection.ConstantEntity;

public class ConstantCallCache extends CallCache<ConstantEntity> {
    @Override
    public Item[] newArrayData(int length) {
        return new Item[length];
    }

    @Override
    public Item[][] newArrayArrayData(int length) {
        return new Item[length][];
    }
}
