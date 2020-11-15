package jphp.runtime.lang.spl.iterator;

import jphp.runtime.Memory;
import jphp.runtime.common.HintType;
import jphp.runtime.env.Environment;
import jphp.runtime.lang.BaseObject;
import jphp.runtime.lang.ForeachIterator;
import jphp.runtime.lang.spl.Traversable;
import jphp.runtime.memory.ObjectMemory;
import jphp.runtime.reflection.ClassEntity;

import static jphp.runtime.annotation.Reflection.*;

@Name("IteratorIterator")
public class IteratorIterator extends BaseObject implements Iterator, OuterIterator, Traversable {
    protected Memory iterator;
    protected ForeachIterator foreachIterator;
    protected boolean valid = true;

    public IteratorIterator(Environment env) {
        super(env);
    }

    public IteratorIterator(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }

    @Signature(@Arg(value = "iterator", type = HintType.TRAVERSABLE))
    public Memory __construct(Environment env, Memory... args) {
        iterator = args[0].toImmutable();
        foreachIterator = iterator.getNewIterator(env);
        return iterator;
    }

    @Override
    @Signature
    public Memory getInnerIterator(Environment env, Memory... args) {
        return iterator;
    }

    @Override
    @Signature
    public Memory current(Environment env, Memory... args) {
        return foreachIterator.getValue();
    }

    @Override
    @Signature
    public Memory key(Environment env, Memory... args) {
        return foreachIterator.getMemoryKey();
    }

    @Override
    @Signature
    public Memory next(Environment env, Memory... args) {
        valid = foreachIterator.next();
        return Memory.NULL;
    }

    @Override
    @Signature
    public Memory rewind(Environment env, Memory... args) {
        foreachIterator.reset();
        valid = foreachIterator.next();
        return Memory.NULL;
    }

    @Override
    @Signature
    public Memory valid(Environment env, Memory... args) {
        return valid ? Memory.TRUE : Memory.FALSE;
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
