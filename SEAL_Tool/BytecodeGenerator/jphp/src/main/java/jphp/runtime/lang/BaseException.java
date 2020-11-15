package jphp.runtime.lang;

import jphp.runtime.annotation.Reflection;
import jphp.runtime.common.HintType;
import jphp.runtime.common.Modifier;
import jphp.runtime.lang.exception.BaseBaseException;
import jphp.runtime.lang.exception.BaseThrowable;
import jphp.runtime.reflection.ClassEntity;
import jphp.runtime.Memory;
import jphp.runtime.env.Environment;

@Reflection.BaseType
@Reflection.Name("Exception")
@Reflection.Signature(root = true, value =
{
        @Reflection.Arg(value = "message", modifier = Modifier.PROTECTED, type = HintType.STRING),
        @Reflection.Arg(value = "code", modifier = Modifier.PROTECTED, type = HintType.INT),
        @Reflection.Arg(value = "previous", modifier = Modifier.PROTECTED, type = HintType.OBJECT),
        @Reflection.Arg(value = "trace", modifier = Modifier.PROTECTED, type = HintType.ARRAY),
        @Reflection.Arg(value = "file", modifier = Modifier.PROTECTED, type = HintType.STRING),
        @Reflection.Arg(value = "line", modifier = Modifier.PROTECTED, type = HintType.INT),
        @Reflection.Arg(value = "position", modifier = Modifier.PROTECTED, type = HintType.INT)
})
public class BaseException extends BaseBaseException implements BaseThrowable {
    public BaseException(Environment env) {
        super(env);
    }

    public BaseException(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }

    @Reflection.Signature(value = {
            @Reflection.Arg(value = "message", optional = @Reflection.Optional(value = "")),
            @Reflection.Arg(value = "code", optional = @Reflection.Optional(value = "0")),
            @Reflection.Arg(value = "previous", nativeType = BaseThrowable.class, optional = @Reflection.Optional(value = "NULL"))
    })
    public Memory __construct(Environment env, Memory... args) {
        clazz.refOfProperty(props, "message").assign(args[0].toString());
        if (args.length > 1) {
            clazz.refOfProperty(props, "code").assign(args[1].toLong());
        }

        if (args.length > 2) {
            clazz.refOfProperty(props, "previous").assign(args[2]);
        }

        return Memory.NULL;
    }

    @Override
    @Reflection.Signature
    final public Memory getMessage(Environment env, Memory... args) {
        return super.getMessage(env, args);
    }

    @Override
    @Reflection.Signature
    final public Memory getCode(Environment env, Memory... args) {
        return super.getCode(env, args);
    }

    @Override
    @Reflection.Signature
    final public Memory getLine(Environment env, Memory... args) {
        return super.getLine(env, args);
    }

    @Override
    @Reflection.Signature
    final public Memory getPosition(Environment env, Memory... args) {
        return super.getPosition(env, args);
    }

    @Override
    @Reflection.Signature
    final public Memory getFile(Environment env, Memory... args) {
        return super.getFile(env, args);
    }

    @Override
    @Reflection.Signature
    final public Memory getTrace(Environment env, Memory... args) {
        return super.getTrace(env, args);
    }

    @Override
    @Reflection.Signature
    final public Memory getPrevious(Environment env, Memory... args) {
        return super.getPrevious(env, args);
    }

    @Override
    @Reflection.Signature
    public Memory __toString(Environment env, Memory... args) {
        return super.__toString(env, args);
    }

    @Override
    @Reflection.Signature
    final public Memory getTraceAsString(Environment env, Memory... args) {
        return super.getTraceAsString(env, args);
    }

    @Reflection.Signature
    private Memory __clone(Environment env, Memory... args) {
        return Memory.NULL;
    }
}
