package jphp.runtime.reflection;

import jphp.runtime.memory.support.MemoryOperation;
import jphp.runtime.Memory;
import jphp.runtime.env.Context;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.exceptions.CriticalException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class CompilePropertyEntity extends PropertyEntity {
    protected final Field field;
    protected MemoryOperation operation;

    public CompilePropertyEntity(Context context, Field field) {
        super(context);
        this.field = field;
        this.field.setAccessible(true);

        setModifier(jphp.runtime.common.Modifier.PUBLIC);

        if (Modifier.isProtected(field.getModifiers())) {
            setModifier(jphp.runtime.common.Modifier.PROTECTED);
        } else if (Modifier.isPrivate(field.getModifiers())) {
            throw new CriticalException("Unsupported bind private fields: " + field.toGenericString());
        }

        this.operation = MemoryOperation.get(field.getType(), field.getGenericType());

        if (this.operation == null) {
            throw new CriticalException("Unsupported type for field " + field.toGenericString());
        }

        setStatic(Modifier.isStatic(field.getModifiers()));
    }

    @Override
    public Memory assignValue(Environment env, TraceInfo trace, Object object, String name, Memory value) {
        try {
            field.set(object, operation.convertNoThrow(env, trace, value));
        } catch (IllegalAccessException e) {
            throw new CriticalException(e);
        }
        return value;
    }

    @Override
    public Memory getStaticValue(Environment env, TraceInfo trace) {
        try {
            return operation.unconvertNoThow(env, trace, field.get(null));
        } catch (IllegalAccessException e) {
            throw new CriticalException(e);
        }
    }

    @Override
    public Memory getValue(Environment env, TraceInfo trace, Object object) {
        try {
            return operation.unconvertNoThow(env, trace, field.get(object));
        } catch (IllegalAccessException e) {
            throw new CriticalException(e);
        }
    }
}
