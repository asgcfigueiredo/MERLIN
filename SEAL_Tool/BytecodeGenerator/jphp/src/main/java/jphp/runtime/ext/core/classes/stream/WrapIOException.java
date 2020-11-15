package jphp.runtime.ext.core.classes.stream;

import jphp.runtime.env.Environment;
import jphp.runtime.ext.java.JavaException;
import jphp.runtime.reflection.ClassEntity;

import static jphp.runtime.annotation.Reflection.Name;

@Name("php\\io\\IOException")
public class WrapIOException extends JavaException {
    public WrapIOException(Environment env, Throwable throwable) {
        super(env, throwable);
    }

    public WrapIOException(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }
}
