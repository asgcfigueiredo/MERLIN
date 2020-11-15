package jphp.runtime.lang.spl.exception;

import jphp.runtime.env.Environment;
import jphp.runtime.reflection.ClassEntity;

import static jphp.runtime.annotation.Reflection.Name;

@Name("UnexpectedValueException")
public class UnexpectedValueException extends RuntimeException {
    public UnexpectedValueException(Environment env) {
        super(env);
    }

    public UnexpectedValueException(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }
}
