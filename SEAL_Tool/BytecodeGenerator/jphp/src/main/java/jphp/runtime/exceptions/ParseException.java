package jphp.runtime.exceptions;

import jphp.runtime.exceptions.support.ErrorException;
import jphp.runtime.exceptions.support.ErrorType;
import jphp.runtime.env.TraceInfo;

public class ParseException extends ErrorException {
    public ParseException(String message, TraceInfo traceInfo) {
        super(message, traceInfo);
    }

    @Override
    public ErrorType getType() {
        return ErrorType.E_PARSE;
    }
}
