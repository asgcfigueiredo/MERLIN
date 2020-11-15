package jphp.runtime.env.handler;

import jphp.runtime.env.message.SystemMessage;
import jphp.runtime.exceptions.support.ErrorException;

abstract public class ErrorReportHandler {
    abstract public boolean onError(SystemMessage error);
    abstract public boolean onFatal(ErrorException error);
}
