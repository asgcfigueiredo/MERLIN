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
import java.util.concurrent.CopyOnWriteArrayList;

public class ListMemoryOperation extends GenericMemoryOperation<Collection> {
    public ListMemoryOperation(Type... genericTypes) {
        super(genericTypes);

        if (genericTypes == null) {
            operations = new MemoryOperation[] { MemoryOperation.get(Memory.class, null) };
        }
    }

    @Override
    public Class<?>[] getOperationClasses() {
        return new Class<?>[] {
                List.class,
                ArrayList.class,
                LinkedList.class,
                Collection.class,
                Stack.class,
                Vector.class,
                CopyOnWriteArrayList.class
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public List convert(Environment env, TraceInfo trace, Memory arg) throws Throwable {
        List result = new ArrayList();

        for (Memory el : arg.getNewIterator(env)) {
            if (operations.length >= 1) {
                result.add(operations[0].convert(env, trace, el));
            } else {
                result.add(Memory.unwrap(env, el, true));
            }
        }

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Memory unconvert(Environment env, TraceInfo trace, Collection arg) throws Throwable {
        if (arg == null) {
            return Memory.NULL;
        }

        ArrayMemory result = new ArrayMemory();
        for (Object el : arg) {
            if (operations.length >= 1) {
                result.add(operations[0].unconvert(env, trace, el));
            } else {
                result.add(Memory.wrap(env, el));
            }
        }

        return result.toConstant();
    }

    @Override
    public void applyTypeHinting(ParameterEntity parameter) {
        parameter.setType(HintType.TRAVERSABLE);
    }
}
