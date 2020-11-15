package jphp.runtime.memory.support.operation.collection;

import jphp.runtime.Memory;
import jphp.runtime.common.HintType;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.memory.ArrayMemory;
import jphp.runtime.memory.support.MemoryOperation;
import jphp.runtime.memory.support.operation.GenericMemoryOperation;
import jphp.runtime.reflection.ParameterEntity;

import java.lang.reflect.Type;
import java.util.*;

public class EnumerationMemoryOperation extends GenericMemoryOperation<Enumeration> {
    public EnumerationMemoryOperation(Type... genericTypes) {
        super(genericTypes);

        if (genericTypes == null) {
            operations = new MemoryOperation[] { MemoryOperation.get(Memory.class, null) };
        }
    }

    @Override
    public Class<?>[] getOperationClasses() {
        return new Class<?>[] {
                Enumeration.class
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public Enumeration convert(Environment env, TraceInfo trace, Memory arg) throws Throwable {
        Vector result = new Vector();

        for (Memory el : arg.getNewIterator(env)) {
            result.add(operations[0].convert(env, trace, el));
        }

        return result.elements();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Memory unconvert(Environment env, TraceInfo trace, Enumeration arg) throws Throwable {
        if (arg == null) {
            return Memory.NULL;
        }

        ArrayMemory result = new ArrayMemory();
        while (arg.hasMoreElements()) {
            result.add(operations[0].unconvert(env, trace, arg.nextElement()));
        }

        return result.toConstant();
    }

    @Override
    public void applyTypeHinting(ParameterEntity parameter) {
        parameter.setType(HintType.TRAVERSABLE);
    }
}
