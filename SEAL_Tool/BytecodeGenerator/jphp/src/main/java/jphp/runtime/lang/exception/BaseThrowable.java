package jphp.runtime.lang.exception;

import jphp.runtime.Memory;
import jphp.runtime.annotation.Reflection.Name;
import jphp.runtime.annotation.Reflection.Signature;
import jphp.runtime.env.Environment;
import jphp.runtime.lang.IObject;

@Name("Throwable")
public interface BaseThrowable extends IObject {
    @Signature
    Memory getMessage(Environment env, Memory... args);

    @Signature
    Memory getCode(Environment env, Memory... args);

    @Signature
    Memory getFile(Environment env, Memory... args);

    @Signature
    Memory getLine(Environment env, Memory... args);

    @Signature
    Memory getTrace(Environment env, Memory... args);

    @Signature
    Memory getTraceAsString(Environment env, Memory... args);

    @Signature
    Memory __toString(Environment env, Memory... args);
}
