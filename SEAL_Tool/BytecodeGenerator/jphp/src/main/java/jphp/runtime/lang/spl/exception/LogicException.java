package jphp.runtime.lang.spl.exception;

import jphp.runtime.env.Environment;
import jphp.runtime.lang.BaseException;
import jphp.runtime.reflection.ClassEntity;

import static jphp.runtime.annotation.Reflection.Name;

@Name("LogicException")
public class LogicException extends BaseException {
    public LogicException(Environment env) {
        super(env);
    }

    public LogicException(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }
}
