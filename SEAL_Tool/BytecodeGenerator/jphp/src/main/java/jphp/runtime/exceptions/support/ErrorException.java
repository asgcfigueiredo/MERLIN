package jphp.runtime.exceptions.support;

import jphp.runtime.env.Context;
import jphp.runtime.env.TraceInfo;

abstract public class ErrorException extends PhpException {

    public ErrorException(String message, TraceInfo traceInfo) {
        super(message, traceInfo);
    }

    public ErrorException(String message, Context context) {
        super(message, context);
    }

    abstract public ErrorType getType();
}
