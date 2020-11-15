package jphp.runtime.lang.exception;

import jphp.runtime.exceptions.support.ErrorType;
import jphp.runtime.annotation.Reflection.Name;
import jphp.runtime.env.Environment;
import jphp.runtime.reflection.ClassEntity;

@Name("ArithmeticError")
public class BaseArithmeticError extends BaseError {
    public BaseArithmeticError(Environment env, ErrorType errorType) {
        super(env, errorType);
    }

    public BaseArithmeticError(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }
}
