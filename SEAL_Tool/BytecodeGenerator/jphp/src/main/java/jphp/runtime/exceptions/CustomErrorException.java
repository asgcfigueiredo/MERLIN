package jphp.runtime.exceptions;

import jphp.runtime.exceptions.support.ErrorException;
import jphp.runtime.exceptions.support.ErrorType;
import jphp.runtime.env.TraceInfo;

public class CustomErrorException extends ErrorException {
    protected final ErrorType type;
    public CustomErrorException(ErrorType type, String message, TraceInfo traceInfo) {
        super(message, traceInfo);
        this.type = type;
    }

    @Override
    public ErrorType getType() {
        return type;
    }
}
