package jphp.runtime.memory.support.operation;

import jphp.runtime.Memory;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.ext.core.classes.WrapThread;
import jphp.runtime.memory.ObjectMemory;
import jphp.runtime.memory.support.MemoryOperation;
import jphp.runtime.reflection.ParameterEntity;

public class ThreadMemoryOperation extends MemoryOperation<Thread> {
    @Override
    public Class<?>[] getOperationClasses() {
        return new Class<?>[] { Thread.class };
    }

    @Override
    public Thread convert(Environment env, TraceInfo trace, Memory arg) throws Throwable {
        if (arg.isNull()) {
            return null;
        }

        return arg.toObject(WrapThread.class).getThread();
    }

    @Override
    public Memory unconvert(Environment env, TraceInfo trace, Thread arg) throws Throwable {
        if (arg == null) {
            return Memory.NULL;
        }

        return ObjectMemory.valueOf(new WrapThread(env, arg));
    }

    @Override
    public void applyTypeHinting(ParameterEntity parameter) {
        parameter.setTypeNativeClass(WrapThread.class);
    }
}
