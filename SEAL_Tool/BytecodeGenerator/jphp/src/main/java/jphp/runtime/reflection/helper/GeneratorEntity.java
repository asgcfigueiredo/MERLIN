package jphp.runtime.reflection.helper;


import jphp.runtime.reflection.support.AbstractFunctionEntity;
import jphp.runtime.Memory;
import jphp.runtime.env.Context;
import jphp.runtime.env.Environment;
import jphp.runtime.exceptions.CriticalException;
import jphp.runtime.lang.IObject;
import jphp.runtime.reflection.ClassEntity;
import jphp.runtime.reflection.ParameterEntity;

import java.lang.reflect.InvocationTargetException;

public class GeneratorEntity extends ClassEntity {
    protected boolean returnReference;
    public ParameterEntity[] parameters;
    protected AbstractFunctionEntity owner;

    public GeneratorEntity(Context context) {
        super(context);
        this.lowerName = getLowerName();
        this.shortName = getShortName();
        this.name      = getName();
        this.namespaceName = getNamespaceName();
        setType(Type.GENERATOR);
    }

    @Override
    public String getName() {
        return "Generator";
    }

    @Override
    public String getLowerName() {
        return "generator";
    }

    @Override
    public String getShortName() {
        return "Generator";
    }

    @Override
    public String getNamespaceName() {
        return "";
    }

    public boolean isReturnReference() {
        return returnReference;
    }

    public void setReturnReference(boolean returnReference) {
        this.returnReference = returnReference;
    }

    public void setParameters(ParameterEntity[] parameters) {
        this.parameters = parameters;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof GeneratorEntity;
    }

    @Override
    public IObject newObjectWithoutConstruct(Environment env) {
        IObject object;
        try {
            object = (IObject) nativeConstructor.newInstance(env, this, env.getLateObject(), Memory.CONST_EMPTY_ARRAY);
        } catch (InvocationTargetException e){
            env.__throwException(e);
            return null;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new CriticalException(e);
        }
        return object;
    }

    @Override
    public void setNativeClazz(Class<?> nativeClazz) {
        this.nativeClazz = nativeClazz;

        if (!nativeClazz.isInterface()){
            try {
                this.nativeConstructor = nativeClazz.getConstructor(
                        Environment.class, ClassEntity.class, Memory.class, Memory[].class
                );
                this.nativeConstructor.setAccessible(true);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
