package jphp.runtime.lang.spl.exception;

import jphp.runtime.env.Environment;
import jphp.runtime.reflection.ClassEntity;

import static jphp.runtime.annotation.Reflection.Name;

@Name("InvalidArgumentException")
public class InvalidArgumentException extends LogicException {
    public InvalidArgumentException(Environment env) {
        super(env);
    }

    public InvalidArgumentException(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }
}
