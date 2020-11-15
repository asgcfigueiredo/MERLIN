package jphp.runtime.lang.spl.exception;

import jphp.runtime.env.Environment;
import jphp.runtime.reflection.ClassEntity;

import static jphp.runtime.annotation.Reflection.Name;

@Name("OverflowException")
public class OverflowException extends RuntimeException {
    public OverflowException(Environment env) {
        super(env);
    }

    public OverflowException(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }
}
