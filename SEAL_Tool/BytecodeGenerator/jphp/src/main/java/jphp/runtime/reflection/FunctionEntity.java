package jphp.runtime.reflection;

import jphp.runtime.reflection.helper.ClosureEntity;
import jphp.runtime.reflection.support.AbstractFunctionEntity;
import jphp.runtime.Memory;
import jphp.runtime.env.Context;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.invoke.InvokeHelper;
import jphp.runtime.lang.Closure;
import jphp.runtime.memory.ObjectMemory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class FunctionEntity extends AbstractFunctionEntity {
    protected boolean isInternal = false;
    protected ModuleEntity module;

    private Class<?> nativeClazz;
    private Method nativeMethod;

    private boolean isStatic = false;

    private Closure cachedClosure;

    public FunctionEntity(Context context) {
        super(context);
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean aStatic) {
        isStatic = aStatic;
    }

    public boolean isInternal() {
        return isInternal;
    }

    public Class<?> getNativeClazz() {
        return nativeClazz;
    }

    public void setNativeClazz(Class<?> nativeClazz) {
        this.nativeClazz = nativeClazz;
    }

    public Method getNativeMethod() {
        return nativeMethod;
    }

    public void setNativeMethod(Method nativeMethod) {
        this.nativeMethod = nativeMethod;
        nativeMethod.setAccessible(true);
    }

    public boolean isDeprecated(){
        return false; // TODO
    }

    public ModuleEntity getModule() {
        return module;
    }

    public void setModule(ModuleEntity module) {
        this.module = module;
    }

    public boolean hasParameters() {
        return parameters != null && parameters.length > 0;
    }

    public Memory invoke(Environment env, TraceInfo trace, Memory[] arguments) throws Throwable {
        try {
            return InvokeHelper.checkReturnType(
                    env, trace,
                    (Memory)nativeMethod.invoke(null, env, arguments),
                    this
            );
        } catch (InvocationTargetException e){
            return env.__throwException(e);
        } finally {
            unsetArguments(arguments);
        }
    }

    @Override
    public Memory getImmutableResultTyped(Environment env, TraceInfo trace) {
        Memory result = getImmutableResult();

        if (result != null && !resultTypeChecked) {
            result = InvokeHelper.checkReturnType(env, trace, result, this);

            if (result != null) {
                this.result = result;
                this.resultTypeChecked = true;
            }
        }

        return result;
    }

    @Override
    public Closure getClosure(Environment env) {
        if (cachedClosure != null)
            return cachedClosure;

        final FunctionEntity bind = this;
        final ClosureEntity closureEntity1 = new ClosureEntity(this.getContext());
        closureEntity1.setParent(env.scope.fetchUserClass(Closure.class));
        closureEntity1.parameters = this.parameters;
        closureEntity1.setReturnReference(this.isReturnReference());

        MethodEntity m = new MethodEntity(this);
        m.setUsesStackTrace(true);
        m.setClazz(closureEntity1);
        m.setName("__invoke");
        closureEntity1.addMethod(m, null);
        closureEntity1.doneDeclare();

        Closure tmp = new Closure(env, closureEntity1, new ObjectMemory(env.getLateObject()), null, Memory.CONST_EMPTY_ARRAY){
            @Override
            public Memory __invoke(Environment e, Memory... args) {
                try {
                    return bind.invoke(e, e.peekCall(0).trace, args);
                } catch (RuntimeException e1){
                    throw e1;
                } catch (Throwable throwable) {
                    throw new RuntimeException(throwable);
                }
            }

            @Override
            public Memory getOrCreateStatic(String name) {
                return Memory.NULL;
            }

            @Override
            public ClassEntity getReflection() {
                return closureEntity1;
            }
        };

        try {
            m.setNativeMethod(tmp.getClass().getDeclaredMethod("__invoke", Environment.class, Memory[].class));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return cachedClosure = tmp;
    }
}
