package jphp.runtime.lang.spl.iterator;

import jphp.runtime.Memory;
import jphp.runtime.env.Environment;
import jphp.runtime.lang.BaseObject;
import jphp.runtime.lang.ForeachIterator;
import jphp.runtime.memory.ObjectMemory;
import jphp.runtime.reflection.ClassEntity;

import static jphp.runtime.annotation.Reflection.Name;
import static jphp.runtime.annotation.Reflection.Signature;

@Name("EmptyIterator")
public class EmptyIterator extends BaseObject implements Iterator {
    public EmptyIterator(Environment env) {
        super(env);
    }

    public EmptyIterator(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }

    @Override
    @Signature
    public Memory current(Environment env, Memory... args) {
        return Memory.NULL;
    }

    @Override
    @Signature
    public Memory key(Environment env, Memory... args) {
        return Memory.NULL;
    }

    @Override
    @Signature
    public Memory next(Environment env, Memory... args) {
        return Memory.NULL;
    }

    @Override
    @Signature
    public Memory rewind(Environment env, Memory... args) {
        return Memory.NULL;
    }

    @Override
    @Signature
    public Memory valid(Environment env, Memory... args) {
        return Memory.FALSE;
    }

    @Override
    public ForeachIterator getNewIterator(Environment env, boolean getReferences, boolean getKeyReferences) {
        return ObjectMemory.valueOf(this).getNewIterator(env, getReferences, getKeyReferences);
    }

    @Override
    public ForeachIterator getNewIterator(Environment env) {
        return ObjectMemory.valueOf(this).getNewIterator(env);
    }
}
