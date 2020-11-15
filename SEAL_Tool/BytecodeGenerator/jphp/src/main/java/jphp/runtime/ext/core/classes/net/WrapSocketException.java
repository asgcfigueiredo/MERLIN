package jphp.runtime.ext.core.classes.net;

import jphp.runtime.env.Environment;
import jphp.runtime.ext.java.JavaException;
import jphp.runtime.reflection.ClassEntity;

import static jphp.runtime.annotation.Reflection.Name;

@Name("php\\net\\SocketException")
public class WrapSocketException extends JavaException {
    public WrapSocketException(Environment env, Throwable throwable) {
        super(env, throwable);
    }

    public WrapSocketException(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }
}
