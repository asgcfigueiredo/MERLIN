package jphp.runtime.invoke;

import jphp.runtime.memory.ArrayMemory;
import jphp.runtime.memory.StringMemory;
import jphp.runtime.reflection.MethodEntity;
import jphp.runtime.Memory;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;

class MagicStaticMethodInvoker extends StaticMethodInvoker {
    protected Memory methodName;

    public MagicStaticMethodInvoker(Environment env, TraceInfo trace, String calledClass, MethodEntity method,
                                    String methodName) {
        super(env, trace, calledClass, method);
        this.methodName = new StringMemory(methodName);
    }

    @Override
    public void pushCall(TraceInfo trace, Memory[] args) {
        env.pushCallEx(trace, null, args, methodName.toString(), method.getClazz(), calledClass);
        env.pushCallEx(
                trace, null,
                new Memory[]{methodName, new ArrayMemory(true, args)},
                method.getName(), method.getClazz(), calledClass
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
