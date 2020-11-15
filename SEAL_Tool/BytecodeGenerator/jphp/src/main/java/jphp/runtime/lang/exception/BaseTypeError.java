package jphp.runtime.lang.exception;

import jphp.runtime.exceptions.support.ErrorType;
import jphp.runtime.annotation.Reflection.Name;
import jphp.runtime.env.Environment;
import jphp.runtime.reflection.ClassEntity;

@Name("TypeError")
public class BaseTypeError extends BaseError {
    public BaseTypeError(Environment env, ErrorType errorType) {
        super(env, errorType);
    }

    public BaseTypeError(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }
}
