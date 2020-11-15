package jphp.runtime.exceptions;

import jphp.runtime.exceptions.support.ErrorException;
import jphp.runtime.exceptions.support.ErrorType;
import jphp.runtime.env.Context;
import jphp.runtime.env.TraceInfo;

public class CompileException extends ErrorException {

    public CompileException(String message, TraceInfo traceInfo) {
        super(message, traceInfo);
    }

    public CompileException(String message, Context context) {
        super(message, context);
    }

    @Override
    public ErrorType getType() {
        return ErrorType.E_COMPILE_ERROR;
    }
}
