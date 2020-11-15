package jphp.runtime.reflection.helper;

import jphp.runtime.Memory;
import jphp.runtime.env.Context;
import jphp.runtime.env.Environment;
import jphp.runtime.exceptions.CriticalException;
import jphp.runtime.lang.Closure;
import jphp.runtime.memory.ObjectMemory;
import jphp.runtime.reflection.ClassEntity;
import jphp.runtime.reflection.ParameterEntity;

import java.lang.reflect.InvocationTargetException;

public class ClosureEntity extends ClassEntity {
    protected boolean returnReference;
    public ParameterEntity[] parameters;
    public ParameterEntity[] uses;

    protected ObjectMemory singleton;
    protected GeneratorEntity generatorEntity;

    public ClosureEntity(Context context) {
        super(context);
        setName(Closure.class.getSimpleName());
        setType(Type.CLOSURE);
    }

    public boolean isReturnReference() {
        return returnReference;
    }

    @Override
    public boolean isHiddenInCallStack() {
        return true;
    }

    public void setReturnReference(boolean returnReference) {
        this.returnReference = returnReference;
    }

    public void setParameters(ParameterEntity[] parameters) {
        this.parameters = parameters;
    }

    public void setUses(ParameterEntity[] uses) {
        this.uses = uses;
    }

    /**
     * Use getSingleton(string)
     */
    @Deprecated
    public ObjectMemory getSingleton() {
        return getSingleton(null);
    }

    public ObjectMemory getSingleton(String selfContextClass) {
        if (singleton != null) {
            return singleton;
        }

        synchronized (this) {
            if (singleton == null) {
                try {
                    singleton = new ObjectMemory((Closure) this.nativeConstructor.newInstance(null, this, Memory.NULL, selfContextClass, null));
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new CriticalException(e);
                } catch (InvocationTargetException e) {
                    throw new CriticalException(e.getTargetException());
                }
            }
        }

        return singleton;
    }

    @Override
    public String getName() {
        return parent.getName();
    }

    @Override
    public String getLowerName() {
        return parent == null ? super.getLowerName() : parent.getLowerName();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ClosureEntity;
    }

    @Override
    public void setNativeClazz(Class<?> nativeClazz) {
        this.nativeClazz = nativeClazz;
        if (!nativeClazz.isInterface()){
            try {
                this.nativeConstructor = nativeClazz.getConstructor(Environment.class, ClassEntity.class, Memory.class, String.class, Memory[].class);
                this.nativeConstructor.setAccessible(true);

            } catch (NoSuchMethodException e) {
                throw new CriticalException(e);
            }
        }
    }

    public GeneratorEntity getGeneratorEntity() {
        return generatorEntity;
    }

    public void setGeneratorEntity(GeneratorEntity generatorEntity) {
        this.generatorEntity = generatorEntity;
    }
}
