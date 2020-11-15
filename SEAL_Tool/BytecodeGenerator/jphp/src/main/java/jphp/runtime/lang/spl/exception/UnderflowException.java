package jphp.runtime.lang.spl.exception;

import jphp.runtime.env.Environment;
import jphp.runtime.reflection.ClassEntity;

import static jphp.runtime.annotation.Reflection.Name;

@Name("UnderflowException")
public class UnderflowException extends RuntimeException {
    public UnderflowException(Environment env) {
        super(env);
    }

    public UnderflowException(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }
}
