package jphp.runtime.ext.core.reflection;

import jphp.runtime.env.Environment;
import jphp.runtime.lang.BaseException;
import jphp.runtime.reflection.ClassEntity;

import static jphp.runtime.annotation.Reflection.*;

@Name("ReflectionException")
public class ReflectionException extends BaseException {
    public ReflectionException(Environment env) {
        super(env);
    }

    public ReflectionException(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }
}
