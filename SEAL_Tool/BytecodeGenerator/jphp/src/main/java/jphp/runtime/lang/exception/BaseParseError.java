package jphp.runtime.lang.exception;

import jphp.runtime.exceptions.support.ErrorType;
import jphp.runtime.annotation.Reflection.Name;
import jphp.runtime.env.Environment;
import jphp.runtime.reflection.ClassEntity;

@Name("ParseError")
public class BaseParseError extends BaseError {
    public BaseParseError(Environment env) {
        super(env);
    }

    public BaseParseError(Environment env, ErrorType errorType) {
        super(env, errorType);
    }

    public BaseParseError(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }
}
