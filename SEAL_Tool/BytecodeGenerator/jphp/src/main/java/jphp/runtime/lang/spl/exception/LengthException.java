package jphp.runtime.lang.spl.exception;

import jphp.runtime.env.Environment;
import jphp.runtime.reflection.ClassEntity;

import static jphp.runtime.annotation.Reflection.Name;

@Name("LengthException")
public class LengthException extends LogicException {
    public LengthException(Environment env) {
        super(env);
    }

    public LengthException(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }
}
