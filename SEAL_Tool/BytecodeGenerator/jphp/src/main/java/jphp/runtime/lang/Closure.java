package jphp.runtime.lang;

import jphp.runtime.annotation.Reflection;
import jphp.runtime.common.HintType;
import jphp.runtime.exceptions.support.ErrorType;
import jphp.runtime.invoke.Invoker;
import jphp.runtime.invoke.ObjectInvokeHelper;
import jphp.runtime.lang.support.IStaticVariables;
import jphp.runtime.memory.ArrayMemory;
import jphp.runtime.memory.ObjectMemory;
import jphp.runtime.memory.ReferenceMemory;
import jphp.runtime.reflection.ClassEntity;
import jphp.runtime.reflection.ParameterEntity;
import jphp.runtime.Memory;
import jphp.runtime.env.Environment;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Reflection.Name("Closure")
public abstract class Closure extends BaseObject implements IStaticVariables, Cloneable {
    protected Memory[] uses;
    private Map<String, ReferenceMemory> statics;
    protected Memory self = Memory.NULL;
    protected String scope = null;

    /*public Closure(Environment env, ClassEntity closure, Memory self, Memory[] uses) {
        this(env, closure, self, null, uses);
    }*/

    public Closure(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }

    public Closure(Environment env, ClassEntity closure, Memory self, String scope, Memory[] uses) {
        super(closure);
        this.self = self;
        this.scope = scope;

        if (env != null && (this.scope == null || this.scope.isEmpty())) {
            this.scope = env.getLateStatic();
        }

        if (this.scope != null && this.scope.isEmpty()) {
            this.scope = null;
        }

        this.uses = uses;
    }

    @Reflection.Signature
    abstract public Memory __invoke(Environment env, Memory... args) throws Throwable;

    public Memory[] getUses() {
        return uses == null ? Memory.CONST_EMPTY_ARRAY : uses;
    }

    @Reflection.Signature({@Reflection.Arg("prop"), @Reflection.Arg("value")})
    public Memory __set(Environment env, Memory... args){
        env.error(ErrorType.E_ERROR, "Closure object cannot have properties");
        return Memory.NULL;
    }

    @Reflection.Signature({@Reflection.Arg("prop")})
    public Memory __get(Environment env, Memory... args){
        return __set(env, args);
    }

    @Reflection.Signature({@Reflection.Arg("prop")})
    public Memory __unset(Environment env, Memory... args){
        return __set(env, args);
    }

    @Reflection.Signature({@Reflection.Arg("prop")})
    public Memory __isset(Environment env, Memory... args){
        return __set(env, args);
    }

    @Override
    public Memory getStatic(String name){
        if (statics == null)
            return null;

        return statics.get(name);
    }

    public String getScope() {
        return scope;
    }

    public Memory getSelf() {
        return self;
    }

    public Memory getOrCreateStatic(String name, Memory initValue){
        if (statics == null)
            statics = new HashMap<>();

        ReferenceMemory result = statics.get(name);
        if (result == null) {
            result = new ReferenceMemory(initValue);
            statics.put(name, result);
        }

        return result;
    }

    @Reflection.Signature({
            @Reflection.Arg("newThis"),
            @Reflection.Arg(value = "parameters", type = HintType.VARARG, optional = @Reflection.Optional("null"))
    })
    public Memory call(Environment env, Memory... args) throws Throwable {
        ParameterEntity.validateTypeHinting(env, 1, args, HintType.OBJECT, true);

        Closure newClosure = (Closure) this.clone();
        newClosure.self = args[0];
        newClosure.scope = newClosure.self.toValue(ObjectMemory.class).getReflection().getName();

        return ObjectInvokeHelper.invokeMethod(
                newClosure, newClosure.getReflection().methodMagicInvoke, env, env.trace(),
                Arrays.copyOfRange(args, 1, args.length)
        );
    }

    @Reflection.Signature({@Reflection.Arg("newThis"), @Reflection.Arg(value = "newScope", optional = @Reflection.Optional("static"))})
    public Memory bindTo(Environment env, Memory... args) throws CloneNotSupportedException {
        ParameterEntity.validateTypeHinting(env, 1, args, HintType.OBJECT, true);

        Closure newClosure = (Closure) this.clone();
        newClosure.self = args[0];
        newClosure.scope = args[1].toString();
        return new ObjectMemory(newClosure);
    }

    @Reflection.Signature
    public Memory __debugInfo(Environment env, Memory... args) {
        ArrayMemory r = new ArrayMemory();

        if (uses != null && uses.length > 0) {
            r.put("uses", ArrayMemory.of(uses));
        }

        if (self.isNotNull()) {
            r.put("this", self.toImmutable());
        }

        return r;
    }

    @Reflection.Signature({
            @Reflection.Arg(value = "closure", typeClass = "Closure"),
            @Reflection.Arg(value = "newThis"),
            @Reflection.Arg(value = "newScope", optional = @Reflection.Optional("static"))
    })
    public static Memory bind(Environment env, Memory... args) throws CloneNotSupportedException {
        ParameterEntity.validateTypeHinting(env, 2, args, HintType.OBJECT, true);
        Closure closure = args[0].toObject(Closure.class);

        Closure newClosure = (Closure) closure.clone();
        newClosure.self = args[0];
        newClosure.scope = args[1].toString();
        return new ObjectMemory(newClosure);
    }

    @Reflection.Signature(@Reflection.Arg(value = "callable", type = HintType.CALLABLE))
    public static Memory fromCallable(Environment env, Memory... args) {
        Invoker invoker = Invoker.create(env, args[0]);

        return ObjectMemory.valueOf(new ClosureInvoker(env, invoker));
    }

    @Reflection.Name("php\\lang\\ClosureInvoker")
    public static class ClosureInvoker extends Closure {
        private Invoker invoker;

        public ClosureInvoker(Environment env, ClassEntity clazz) {
            super(env, clazz);
        }

        public ClosureInvoker(Environment env, Invoker invoker) {
            super(env, env.fetchClass(ClosureInvoker.class), Memory.NULL, "", Memory.CONST_EMPTY_ARRAY);
            this.invoker = invoker;
        }

        @Reflection.Signature
        private void __construct() {
        }

        @Override
        @Reflection.Signature
        public Memory __invoke(Environment env, Memory... args) throws Throwable {
            return invoker.forEnvironment(env).call(args);
        }

        @Override
        public Memory getOrCreateStatic(String name) {
            return Memory.NULL;
        }
    }
}
