package jphp.runtime.memory.support.operation;

import jphp.runtime.Memory;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.memory.StringMemory;
import jphp.runtime.memory.support.MemoryOperation;
import jphp.runtime.reflection.ParameterEntity;

public class ClassMemoryOperation extends MemoryOperation<Class> {
    @Override
    public Class<?>[] getOperationClasses() {
        return new Class<?>[] { Class.class };
    }

    @Override
    public Class convert(Environment env, TraceInfo trace, Memory arg) throws Throwable {
        if (arg.isNull()) {
            return null;
        }

        try {
            return Class.forName(arg.toString());
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public Memory unconvert(Environment env, TraceInfo trace, Class arg) throws Throwable {
        if (arg == null) {
            return Memory.NULL;
        }

        return StringMemory.valueOf(arg.toString());
    }

    @Override
    public void applyTypeHinting(ParameterEntity parameter) {
        parameter.setTypeHintingChecker(new ParameterEntity.TypeHintingChecker() {
            @Override
            public boolean call(Environment env, Memory value) {
                try {
                    Class.forName(value.toString());
                    return true;
                } catch (ClassNotFoundException e) {
                    return false;
                }
            }

            @Override
            public String getNeeded(Environment env, Memory value) {
                return "a string contains the name of a Java Class, but '" + value + "' is not a java class";
            }
        });
    }
}
