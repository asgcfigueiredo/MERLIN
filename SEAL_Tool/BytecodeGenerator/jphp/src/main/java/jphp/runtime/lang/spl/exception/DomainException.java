package jphp.runtime.lang.spl.exception;

import jphp.runtime.env.Environment;
import jphp.runtime.reflection.ClassEntity;

import static jphp.runtime.annotation.Reflection.Name;

@Name("DomainException")
public class DomainException extends LogicException {
    public DomainException(Environment env) {
        super(env);
    }

    public DomainException(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }
}
