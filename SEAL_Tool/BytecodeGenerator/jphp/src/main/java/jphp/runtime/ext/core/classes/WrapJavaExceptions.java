package jphp.runtime.ext.core.classes;

import jphp.runtime.env.Environment;
import jphp.runtime.ext.java.JavaException;
import jphp.runtime.lang.BaseException;
import jphp.runtime.reflection.ClassEntity;

import static jphp.runtime.annotation.Reflection.Name;

final public class WrapJavaExceptions {

    @Name("php\\lang\\IllegalArgumentException")
    public static class IllegalArgumentException extends JavaException {
        public IllegalArgumentException(Environment env, Throwable throwable) {
            super(env, throwable);
        }

        public IllegalArgumentException(Environment env, ClassEntity clazz) {
            super(env, clazz);
        }
    }

    @Name("php\\lang\\IllegalStateException")
    public static class IllegalStateException extends JavaException {
        public IllegalStateException(Environment env, Throwable throwable) {
            super(env, throwable);
        }

        public IllegalStateException(Environment env, ClassEntity clazz) {
            super(env, clazz);
        }
    }

    @Name("php\\lang\\InterruptedException")
    public static class InterruptedException extends JavaException {
        public InterruptedException(Environment env, Throwable throwable) {
            super(env, throwable);
        }

        public InterruptedException(Environment env, ClassEntity clazz) {
            super(env, clazz);
        }
    }

    @Name("php\\lang\\NumberFormatException")
    public static class NumberFormatException extends JavaException {
        public NumberFormatException(Environment env, Throwable throwable) {
            super(env, throwable);
        }

        public NumberFormatException(Environment env, ClassEntity clazz) {
            super(env, clazz);
        }
    }

    @Name("php\\concurrent\\TimeoutException")
    public static class TimeoutException extends JavaException {
        public TimeoutException(Environment env, Throwable throwable) {
            super(env, throwable);
        }

        public TimeoutException(Environment env, ClassEntity clazz) {
            super(env, clazz);
        }
    }

    @Name("php\\lang\\NotImplementedException")
    public static class NotImplementedException extends BaseException {
        public NotImplementedException(Environment env) {
            super(env);
        }

        public NotImplementedException(Environment env, ClassEntity clazz) {
            super(env, clazz);
        }
    }
}
