package jphp.runtime.invoke;

import jphp.runtime.memory.ObjectMemory;
import jphp.runtime.reflection.ClassEntity;
import jphp.runtime.reflection.MethodEntity;
import jphp.runtime.reflection.ParameterEntity;
import jphp.runtime.Memory;
import jphp.runtime.common.Messages;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.lang.IObject;

public class DynamicMethodInvoker extends Invoker {
    protected final IObject object;
    protected final MethodEntity method;

    public DynamicMethodInvoker(Environment env, TraceInfo trace, IObject object, MethodEntity method) {
        super(env, trace);
        this.object = object;
        this.method = method;
    }

    @Override
    public ParameterEntity[] getParameters() {
        return method.getParameters();
    }

    @Override
    public String getName() {
        return object.getReflection().getName() + "::" + method.getName();
    }

    public IObject getObject() {
        return object;
    }

    public MethodEntity getMethod() {
        return method;
    }

    @Override
    public int getArgumentCount() {
        return method.getParameters() == null ? 0 : method.getParameters().length;
    }

    @Override
    public void pushCall(TraceInfo trace, Memory[] args) {
        env.pushCallEx(trace, object, args, method.getName(), method.getClazz(), object.getReflection());
    }

    @Override
    protected Memory invoke(Memory... args) throws Throwable {
        return ObjectInvokeHelper.invokeMethod(
                object, method, env, trace == null ? TraceInfo.UNKNOWN : trace, args, false
        );
    }

    @Override
    public int canAccess(Environment env) {
        return method.canAccess(env);
    }

    public static DynamicMethodInvoker valueOf(Environment env, TraceInfo trace, IObject object, String methodName){
        String methodNameL = methodName.toLowerCase();
        int pos;
        MethodEntity methodEntity;
        if ((pos = methodName.indexOf("::")) > -1){
            String className = methodNameL.substring(0, pos);
            methodNameL = methodNameL.substring(pos + 2);

            ClassEntity classEntity = object.getReflection();
            ClassEntity clazz = env.fetchClass(methodName.substring(0, pos), className, false);
            if (!classEntity.isInstanceOf(clazz)){
                return null;
            }
            methodEntity = clazz.findMethod(methodNameL);
        } else
            methodEntity = object.getReflection().findMethod(methodNameL);

        if (methodEntity == null){
            if (object.getReflection().methodMagicCall != null) {
                return new MagicDynamicMethodInvoker(
                        env, trace, object, object.getReflection().methodMagicCall, methodName
                );
            }
            if (trace == null) {
                return null;
            }
            env.error(trace, Messages.ERR_CALL_TO_UNDEFINED_METHOD.fetch(object.getReflection().getName() + "::" + methodName));
        }

        return new DynamicMethodInvoker(env, trace, object, methodEntity);
    }

    public static DynamicMethodInvoker valueOf(Environment env, TraceInfo trace, Memory object, String methodName){
        return valueOf(env, trace, ((ObjectMemory)object).value, methodName);
    }

    public static DynamicMethodInvoker valueOf(Environment env, TraceInfo trace, IObject object){
        MethodEntity methodEntity = object.getReflection().methodMagicInvoke;
        if (methodEntity == null){
            if (trace == null)
                return null;
            env.error(trace, Messages.ERR_CALL_TO_UNDEFINED_METHOD.fetch(object.getReflection().getName() + "::__invoke"));
        }

        return new DynamicMethodInvoker(env, null, object, methodEntity);
    }

    public static DynamicMethodInvoker valueOf(Environment env, TraceInfo trace, Memory object){
        return valueOf(env, trace, ((ObjectMemory)object).value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DynamicMethodInvoker)) return false;

        DynamicMethodInvoker that = (DynamicMethodInvoker) o;

        return method.equals(that.method) && object.getPointer() == that.object.getPointer();

    }

    @Override
    public int hashCode() {
        int result = object.hashCode();
        result = 31 * result + method.hashCode();
        return result;
    }
}
