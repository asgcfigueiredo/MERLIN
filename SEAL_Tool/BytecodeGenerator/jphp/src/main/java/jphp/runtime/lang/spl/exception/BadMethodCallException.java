package jphp.runtime.lang.spl.exception;

import jphp.runtime.env.Environment;
import jphp.runtime.reflection.ClassEntity;

import static jphp.runtime.annotation.Reflection.Name;

@Name("BadMethodCallException")
public class BadMethodCallException extends BadFunctionCallException {
    public BadMethodCallException(Environment env) {
        super(env);
    }

    public BadMethodCallException(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }
}
