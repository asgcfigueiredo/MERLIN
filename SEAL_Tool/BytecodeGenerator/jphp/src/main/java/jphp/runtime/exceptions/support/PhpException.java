package jphp.runtime.exceptions.support;

import jphp.runtime.env.Context;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.exceptions.JPHPException;

abstract public class PhpException extends RuntimeException implements JPHPException {

    protected TraceInfo traceInfo;

    public PhpException(String message, TraceInfo traceInfo) {
        super(message);
        this.traceInfo = traceInfo;
    }

    public PhpException(String message, Context context){
        super(message);
        this.traceInfo = new TraceInfo(context);
    }

    public TraceInfo getTraceInfo() {
        return traceInfo == null ? TraceInfo.UNKNOWN : traceInfo;
    }

    public void setTraceInfo(TraceInfo traceInfo) {
        this.traceInfo = traceInfo;
    }
}
