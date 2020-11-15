package jphp.runtime.ext.core.classes;

import jphp.runtime.Memory;
import jphp.runtime.annotation.Runtime;
import jphp.runtime.common.HintType;
import jphp.runtime.env.Environment;
import jphp.runtime.invoke.DynamicMethodInvoker;
import jphp.runtime.invoke.FunctionInvoker;
import jphp.runtime.invoke.Invoker;
import jphp.runtime.invoke.StaticMethodInvoker;
import jphp.runtime.lang.BaseObject;
import jphp.runtime.lang.Closure;
import jphp.runtime.memory.ArrayMemory;
import jphp.runtime.memory.LongMemory;
import jphp.runtime.memory.ObjectMemory;
import jphp.runtime.memory.StringBuilderMemory;
import jphp.runtime.reflection.ClassEntity;
import jphp.runtime.reflection.ParameterEntity;

import static jphp.runtime.annotation.Reflection.*;

@Name("php\\lang\\Invoker")
public class WrapInvoker extends BaseObject {
    protected Invoker invoker;

    public WrapInvoker(Environment env, Invoker invoker) {
        super(env);
        this.invoker = invoker;
    }

    public WrapInvoker(Environment env, Memory arg) {
        super(env);
        this.invoker = Invoker.create(env, arg);
    }

    public WrapInvoker(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }

    public Invoker getInvoker() {
        return invoker;
    }

    @Signature({
            @Arg(value = "callback", type = HintType.CALLABLE)
    })
    public Memory __construct(Environment env, Memory... args) {
        this.invoker = Invoker.valueOf(env, null, args[0]);
        return Memory.NULL;
    }

    @Signature
    private Memory __clone(Environment env, Memory... args) { return Memory.NULL; }

    @Signature
    public Memory canAccess(Environment env, Memory... args) {
        return invoker.canAccess(env) == 0 ? Memory.TRUE : Memory.FALSE;
    }

    @Signature
    public Memory getArgumentCount(Environment env, Memory... args) {
        return LongMemory.valueOf(invoker.getArgumentCount());
    }

    @Signature
    public Memory getDescription(Environment env, Memory... args) {
        StringBuilderMemory sb = new StringBuilderMemory();
        sb.append(invoker.getName());
        sb.append("(");

        int i = 0;
        if (invoker.getParameters() == null) {
            sb.append("<internal>");
        } else
        for(ParameterEntity param : invoker.getParameters()) {
            if (i != 0)
                sb.append(", ");
            sb.append(param.getSignatureString());
            i++;
        }

        sb.append(")");
        return sb;
    }

    @Signature
    public Memory isClosure(Environment env, Memory... args) {
        return invoker instanceof DynamicMethodInvoker
                && ((DynamicMethodInvoker) invoker).getObject() instanceof Closure ? Memory.TRUE : Memory.FALSE;
    }

    @Signature
    public Memory isNamedFunction(Environment env, Memory... args) {
        return invoker instanceof FunctionInvoker ? Memory.TRUE : Memory.FALSE;
    }

    @Signature
    public Memory isStaticCall(Environment env, Memory... args) {
        return invoker instanceof StaticMethodInvoker ? Memory.TRUE : Memory.FALSE;
    }

    @Signature
    public Memory isDynamicCall(Environment env, Memory... args) {
        return invoker instanceof DynamicMethodInvoker
                && !(((DynamicMethodInvoker) invoker).getObject() instanceof Closure) ? Memory.TRUE : Memory.FALSE;
    }

    @Signature
    public Memory call(Environment env, Memory... args) {
        invoker.setTrace(env.trace());
        env.popCall();
        try {
            return invoker.callNoThrow(args);
        } finally {
            env.pushCall(this, "call");
        }
    }

    @Signature(@Arg(value = "args", type = HintType.ARRAY))
    public Memory callArray(Environment env, Memory... args) {
        return call(env, args[0].toValue(ArrayMemory.class).values());
    }

    @Signature
    public Memory __invoke(Environment env, Memory... args) {
        return call(env, args);
    }

    @Runtime.FastMethod
    @Signature({
            @Arg("callback")
    })
    public static Memory of(Environment env, Memory... args) {
        Invoker invoker = Invoker.valueOf(env, null, args[0]);
        if (invoker == null)
            return Memory.NULL;

        return new ObjectMemory(new WrapInvoker(env, invoker));
    }
}
