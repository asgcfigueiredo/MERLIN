package jphp.runtime.exceptions;

import jphp.runtime.exceptions.support.ErrorException;
import jphp.runtime.exceptions.support.ErrorType;
import jphp.runtime.env.Context;
import jphp.runtime.env.TraceInfo;

public class FatalException extends ErrorException {

    public FatalException(String message, TraceInfo traceInfo) {
        super(message, traceInfo);
    }

    public FatalException(String message, Context context) {
        super(message, context);
    }

    @Override
    public ErrorType getType() {
        return ErrorType.E_ERROR;
    }
}
