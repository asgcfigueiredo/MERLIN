package jphp.runtime.env;

import jphp.runtime.exceptions.JPHPException;
import jphp.runtime.Memory;

public class DieException extends RuntimeException implements JPHPException {
    protected int exitCode = 0;

    public DieException(Memory value){
        super(value.toString());
        if (value.isNumber())
            exitCode = value.toInteger();
    }

    public int getExitCode() {
        return exitCode;
    }
}
