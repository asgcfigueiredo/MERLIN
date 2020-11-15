package jphp.runtime.lang.spl.iterator;

import jphp.runtime.Memory;
import jphp.runtime.env.Environment;
import jphp.runtime.reflection.ClassEntity;

import static jphp.runtime.annotation.Reflection.Name;

@Name("InfiniteIterator")
public class InfiniteIterator extends IteratorIterator {
    public InfiniteIterator(Environment env) {
        super(env);
    }

    public InfiniteIterator(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }

    @Override
    public Memory next(Environment env, Memory... args) {
        Memory r = super.next(env, args);
        if (!valid) {
            rewind(env);
        }

        return r;
    }
}
