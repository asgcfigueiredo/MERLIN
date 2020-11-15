package jphp.runtime.lang;

import jphp.runtime.annotation.Reflection;
import jphp.runtime.exceptions.CriticalException;
import jphp.runtime.memory.*;
import jphp.runtime.memory.support.MemoryOperation;
import jphp.runtime.reflection.ClassEntity;
import jphp.runtime.Memory;
import jphp.runtime.env.Environment;
import jphp.runtime.memory.*;

@Reflection.Ignore
public interface IObject {
    ClassEntity getReflection();
    ArrayMemory getProperties();
    Environment getEnvironment();
    int getPointer();

    boolean isMock();
    void setAsMock();

    boolean isFinalized();
    void doFinalize();

    default boolean hasProp(String name) {
        return getProperties().containsKey(name);
    }

    default Memory getProp(String name) {
        return getProperties().valueOfIndex(name);
    }

    default void setProp(String name, Memory value) {
        getProperties().putAsKeyString(name, value.fast_toImmutable());
    }

    default void setProp(String name, String value) {
        getProperties().putAsKeyString(name, StringMemory.valueOf(value));
    }

    default void setProp(String name, long value) {
        getProperties().putAsKeyString(name, LongMemory.valueOf(value));
    }

    default void setProp(String name, double value) {
        getProperties().putAsKeyString(name, DoubleMemory.valueOf(value));
    }

    default void setProp(String name, boolean value) {
        getProperties().putAsKeyString(name, TrueMemory.valueOf(value));
    }

    default void removeProp(String name) {
        getProperties().removeByScalar(name);
    }

    default Memory callMethod(Environment env, String name, Memory... args) {
        try {
            return env.invokeMethod(this, name, args);
        } catch (Throwable throwable) {
            env.forwardThrow(throwable);
            return Memory.NULL;
        }
    }

    @SuppressWarnings("unchecked")
    default Memory callMethodAny(Environment env, String name, Object... args) {
        if (args != null && args.length > 0) {
            Memory[] passed = new Memory[args.length];

            for (int i = 0; i < passed.length; i++) {
                if (args[i] == null) {
                    passed[i] = Memory.NULL;
                    continue;
                }

                MemoryOperation operation = MemoryOperation.get(
                        args[i].getClass(), args[i].getClass().getGenericSuperclass()
                );

                if (operation == null) {
                    throw new CriticalException("Unsupported bind type - " + args[i].getClass().toString());
                }

                passed[i] = operation.unconvertNoThow(env, env.trace(), args[i]);
            }

            return callMethod(env, name, passed);
        } else {
            return callMethod(env, name);
        }
    }
}
