package jphp.runtime.lang.spl.exception;

import jphp.runtime.env.Environment;
import jphp.runtime.reflection.ClassEntity;

import static jphp.runtime.annotation.Reflection.Name;

@Name("OutOfBoundsException")
public class OutOfBoundsException extends RuntimeException {
    public OutOfBoundsException(Environment env) {
        super(env);
    }

    public OutOfBoundsException(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }
}
