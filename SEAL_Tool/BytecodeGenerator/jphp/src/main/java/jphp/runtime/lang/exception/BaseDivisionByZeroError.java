package jphp.runtime.lang.exception;

import jphp.runtime.exceptions.support.ErrorType;
import jphp.runtime.annotation.Reflection.Name;
import jphp.runtime.env.Environment;
import jphp.runtime.reflection.ClassEntity;

@Name("DivisionByZeroError")
public class BaseDivisionByZeroError extends BaseArithmeticError {
    public BaseDivisionByZeroError(Environment env, ErrorType errorType) {
        super(env, errorType);
    }

    public BaseDivisionByZeroError(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }
}
