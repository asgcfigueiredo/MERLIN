package jphp.runtime.invoke;

import jphp.runtime.memory.ArrayMemory;
import jphp.runtime.memory.StringMemory;
import jphp.runtime.reflection.MethodEntity;
import jphp.runtime.Memory;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.lang.IObject;

class MagicDynamicMethodInvoker extends DynamicMethodInvoker {
    protected final Memory methodName;

    public MagicDynamicMethodInvoker(Environment env, TraceInfo trace,
                                     IObject object, MethodEntity method, String methodName) {
        super(env, trace, object, method);
        this.methodName = new StringMemory(methodName);
    }

    @Override
    public void pushCall(TraceInfo trace, Memory[] args) {
        env.pushCallEx(trace, object, args, methodName.toString(), method.getClazz(), object.getReflection());
        env.pushCallEx(
                trace, object,
                new Memory[]{methodName, new ArrayMemory(true, args)},
                method.getName(), method.getClazz(), object.getReflection()
        );
    }

    @Override
    public void popCall() {
        env.popCall();
        env.popCall();
    }

    @Override
    protected Memory invoke(Memory... args) throws Throwable {
        return super.invoke(methodName, new ArrayMemory(false, args));
    }
}
