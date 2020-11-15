package jphp.runtime.lang.spl.iterator;

import jphp.runtime.Memory;
import jphp.runtime.env.Environment;
import jphp.runtime.reflection.ClassEntity;

import static jphp.runtime.annotation.Reflection.Name;
import static jphp.runtime.annotation.Reflection.Signature;

@Name("FilterIterator")
abstract public class FilterIterator extends IteratorIterator {
    public FilterIterator(Environment env) {
        super(env);
    }

    public FilterIterator(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }

    @Signature
    abstract public Memory accept(Environment env, Memory... args);

    private void _next(Environment env) {
        while (true) {
            valid = foreachIterator.next();
            if (!valid) {
                break;
            } else {
                valid = env.invokeMethodNoThrow(this, "accept").toBoolean();
                if (valid) {
                    break;
                }
            }
        }
    }

    @Override
    public Memory rewind(Environment env, Memory... args) {
        foreachIterator.reset();
        _next(env);
        return Memory.NULL;
    }

    @Override
    public Memory next(Environment env, Memory... args) {
        _next(env);
        return Memory.NULL;
    }
}
