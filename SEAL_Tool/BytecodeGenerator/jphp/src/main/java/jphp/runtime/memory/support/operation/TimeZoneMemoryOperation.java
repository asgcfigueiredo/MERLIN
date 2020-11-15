package jphp.runtime.memory.support.operation;

import jphp.runtime.ext.core.classes.time.WrapTimeZone;
import jphp.runtime.Memory;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.memory.ObjectMemory;
import jphp.runtime.memory.support.MemoryOperation;
import jphp.runtime.reflection.ParameterEntity;

import java.util.TimeZone;

public class TimeZoneMemoryOperation extends MemoryOperation<TimeZone> {
    @Override
    public Class<?>[] getOperationClasses() {
        return new Class<?>[] { TimeZone.class };
    }

    @Override
    public TimeZone convert(Environment env, TraceInfo trace, Memory arg) throws Throwable {
        if (arg.isNull()) {
            return null;
        }

        return arg.toObject(WrapTimeZone.class).getTimeZone();
    }

    @Override
    public Memory unconvert(Environment env, TraceInfo trace, TimeZone arg) throws Throwable {
        if (arg == null) {
            return Memory.NULL;
        }

        return ObjectMemory.valueOf(new WrapTimeZone(env, arg));
    }

    @Override
    public void applyTypeHinting(ParameterEntity parameter) {
        parameter.setTypeNativeClass(WrapTimeZone.class);
    }
}
