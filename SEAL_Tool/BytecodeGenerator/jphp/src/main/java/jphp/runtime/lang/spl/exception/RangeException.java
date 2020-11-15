package jphp.runtime.lang.spl.exception;

import jphp.runtime.env.Environment;
import jphp.runtime.reflection.ClassEntity;

import static jphp.runtime.annotation.Reflection.Name;

@Name("RangeException")
public class RangeException extends RuntimeException {
    public RangeException(Environment env) {
        super(env);
    }

    public RangeException(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }
}
