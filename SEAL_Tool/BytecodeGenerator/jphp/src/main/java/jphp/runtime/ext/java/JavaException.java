package jphp.runtime.ext.java;

import jphp.runtime.Memory;
import jphp.runtime.env.Environment;
import jphp.runtime.lang.BaseException;
import jphp.runtime.memory.StringBuilderMemory;
import jphp.runtime.memory.StringMemory;
import jphp.runtime.reflection.ClassEntity;
import jphp.runtime.util.JVMStackTracer;

import static jphp.runtime.annotation.Reflection.*;

@Name("php\\lang\\JavaException")
public class JavaException extends BaseException {
    protected Throwable throwable;

    public JavaException(Environment env, Throwable throwable) {
        super(env);
        setThrowable(throwable);
    }

    public JavaException(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }

    public Throwable getThrowable() {
        return throwable;
    }

    @Override
    public String getMessage() {
        return throwable != null ? throwable.getMessage() : super.getMessage();
    }

    @Override
    public String getLocalizedMessage() {
        return throwable != null ? throwable.getLocalizedMessage() : super.getLocalizedMessage();
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
        clazz.setProperty(this, "message", new StringMemory(throwable.toString()));
    }

    @Signature
    public Memory isIllegalArgumentException(Environment env, Memory... args){
        return throwable instanceof IllegalArgumentException ? Memory.TRUE :Memory.FALSE;
    }

    @Signature
    public Memory isRuntimeException(Environment env, Memory... args){
        return throwable instanceof RuntimeException ? Memory.TRUE :Memory.FALSE;
    }

    @Signature
    public Memory isNullPointerException(Environment env, Memory... args){
        return throwable instanceof NullPointerException ? Memory.TRUE :Memory.FALSE;
    }

    @Signature
    public Memory isNumberFormatException(Environment env, Memory... args){
        return throwable instanceof NumberFormatException ? Memory.TRUE :Memory.FALSE;
    }

    @Signature
    public Memory getJavaException(Environment env, Memory... args) {
        if (throwable == null)
            return Memory.NULL;

        return StringMemory.valueOf(throwable.getClass().getName());
    }

    @Signature
    public Memory printJVMStackTrace(Environment env, Memory... args) {
        if (throwable == null)
            return Memory.NULL;

        JVMStackTracer tracer = new JVMStackTracer(
                env.scope.getClassLoader(), throwable.getStackTrace()
        );
        for(JVMStackTracer.Item el : tracer){
            env.echo(el.toString() + "\n");
        }
        return Memory.NULL;
    }

    @Signature
    public Memory getJVMStackTrace(Environment env, Memory... args) {
        if (throwable == null)
            return Memory.NULL;

        StringBuilderMemory sb = new StringBuilderMemory();

        JVMStackTracer tracer = new JVMStackTracer(
                env.scope.getClassLoader(), throwable.getStackTrace()
        );

        for(JVMStackTracer.Item el : tracer){
            sb.append(el.toString());
            sb.append("\n");
        }

        return sb;
    }
}
