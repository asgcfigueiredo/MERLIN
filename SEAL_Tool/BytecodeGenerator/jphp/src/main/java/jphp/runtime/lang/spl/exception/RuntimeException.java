package jphp.runtime.lang.spl.exception;

import jphp.runtime.env.Environment;
import jphp.runtime.lang.BaseException;
import jphp.runtime.reflection.ClassEntity;

import static jphp.runtime.annotation.Reflection.Name;

@Name("RuntimeException")
public class RuntimeException extends BaseException {
    public RuntimeException(Environment env) {
        super(env);
    }

    public RuntimeException(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }
}
