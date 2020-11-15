package jphp.runtime.memory.support.operation;

import jphp.runtime.ext.core.classes.util.WrapLocale;
import jphp.runtime.Memory;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.memory.ObjectMemory;
import jphp.runtime.memory.support.MemoryOperation;
import jphp.runtime.reflection.ParameterEntity;
import jphp.runtime.reflection.support.ReflectionUtils;

import java.util.Locale;

public class LocaleMemoryOperation extends MemoryOperation<Locale> {
    @Override
    public Class<?>[] getOperationClasses() {
        return new Class<?>[] { Locale.class };
    }

    @Override
    public Locale convert(Environment env, TraceInfo trace, Memory arg) throws Throwable {
        if (arg.isNull()) {
            return null;
        }

        return arg.toObject(WrapLocale.class).getLocale();
    }

    @Override
    public Memory unconvert(Environment env, TraceInfo trace, Locale arg) throws Throwable {
        if (arg == null) {
            return Memory.NULL;
        }

        return ObjectMemory.valueOf(new WrapLocale(env, arg));
    }

    @Override
    public void applyTypeHinting(ParameterEntity parameter) {
        parameter.setTypeClass(ReflectionUtils.getClassName(WrapLocale.class));
    }
}
