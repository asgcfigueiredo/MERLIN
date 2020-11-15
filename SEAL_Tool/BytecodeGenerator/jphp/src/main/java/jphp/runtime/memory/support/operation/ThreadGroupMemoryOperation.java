package jphp.runtime.memory.support.operation;

import jphp.runtime.Memory;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.ext.core.classes.WrapThreadGroup;
import jphp.runtime.memory.ObjectMemory;
import jphp.runtime.memory.support.MemoryOperation;
import jphp.runtime.reflection.ParameterEntity;

public class ThreadGroupMemoryOperation extends MemoryOperation<ThreadGroup> {
    @Override
    public Class<?>[] getOperationClasses() {
        return new Class<?>[] { ThreadGroup.class };
    }

    @Override
    public ThreadGroup convert(Environment env, TraceInfo trace, Memory arg) throws Throwable {
        if (arg.isNull()) {
            return null;
        }

        return arg.toObject(WrapThreadGroup.class).getGroup();
    }

    @Override
    public Memory unconvert(Environment env, TraceInfo trace, ThreadGroup arg) throws Throwable {
        if (arg == null) {
            return Memory.NULL;
        }

        return ObjectMemory.valueOf(new WrapThreadGroup(env, arg));
    }

    @Override
    public void applyTypeHinting(ParameterEntity parameter) {
        parameter.setTypeNativeClass(WrapThreadGroup.class);
    }
}
