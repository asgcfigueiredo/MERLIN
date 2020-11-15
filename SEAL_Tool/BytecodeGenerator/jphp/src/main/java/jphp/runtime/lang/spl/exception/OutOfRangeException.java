package jphp.runtime.lang.spl.exception;

import jphp.runtime.env.Environment;
import jphp.runtime.reflection.ClassEntity;

import static jphp.runtime.annotation.Reflection.Name;

@Name("OutOfRangeException")
public class OutOfRangeException extends RuntimeException {
    public OutOfRangeException(Environment env) {
        super(env);
    }

    public OutOfRangeException(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }
}
