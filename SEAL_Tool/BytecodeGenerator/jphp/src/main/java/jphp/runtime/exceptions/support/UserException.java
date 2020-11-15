package jphp.runtime.exceptions.support;

import jphp.runtime.env.Context;
import jphp.runtime.env.TraceInfo;

abstract public class UserException extends PhpException {
    public UserException(String message, TraceInfo traceInfo) {
        super(message, traceInfo);
    }

    public UserException(String message, Context context) {
        super(message, context);
    }
}
