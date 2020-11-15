package jphp.runtime.memory.helper;

import jphp.runtime.exceptions.support.ErrorType;
import jphp.runtime.Memory;
import jphp.runtime.common.Messages;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.memory.ReferenceMemory;
import jphp.runtime.reflection.ClassEntity;
import jphp.runtime.reflection.ConstantEntity;

public class ClassConstantMemory extends ReferenceMemory {
    private final String name;
    private final String lowerName;

    private final String className;
    private final String classLowerName;

    public ClassConstantMemory(String className, String name){
        super();
        this.className = className;
        this.classLowerName = className.toLowerCase();
        this.name = name;
        this.lowerName = name.toLowerCase();
    }

    @Override
    public Memory toImmutable(Environment env, TraceInfo trace) {
        ClassEntity classEntity = env.fetchClass(className, classLowerName, true);
        if (classEntity == null) {
            env.error(trace, ErrorType.E_ERROR, Messages.ERR_CLASS_NOT_FOUND, className);
            return NULL;
        }

        ConstantEntity entity = classEntity.findConstant(name);
        if (entity == null){
            env.error(trace, ErrorType.E_ERROR, Messages.ERR_UNDEFINED_CLASS_CONSTANT, className + "::" + name);
            return NULL;
        }

        return entity.getValue();
    }

    public String getName() {
        return name;
    }

    public String getClassName() {
        return className;
    }

    @Override
    public Memory toValue() {
        return this;
    }
}
