package jphp.runtime.lang.spl.exception;

import jphp.runtime.env.Environment;
import jphp.runtime.reflection.ClassEntity;

import static jphp.runtime.annotation.Reflection.Name;

@Name("BadFunctionCallException")
public class BadFunctionCallException extends LogicException {
    public BadFunctionCallException(Environment env) {
        super(env);
    }

    public BadFunctionCallException(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }
}
