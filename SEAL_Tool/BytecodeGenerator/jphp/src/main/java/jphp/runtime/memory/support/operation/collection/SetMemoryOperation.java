package jphp.runtime.memory.support.operation.collection;

import jphp.runtime.Memory;
import jphp.runtime.common.HintType;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.lang.ForeachIterator;
import jphp.runtime.memory.ArrayMemory;
import jphp.runtime.memory.support.MemoryOperation;
import jphp.runtime.memory.support.operation.GenericMemoryOperation;
import jphp.runtime.reflection.ParameterEntity;

import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Set;

public class SetMemoryOperation extends GenericMemoryOperation<Set> {
    public SetMemoryOperation(Type... genericTypes) {
        super(genericTypes);

        if (genericTypes == null) {
            operations = new MemoryOperation[] { MemoryOperation.get(Memory.class, null) };
        }
    }

    @Override
    public Class<?>[] getOperationClasses() {
        return new Class<?>[] { Set.class, LinkedHashSet.class };
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set convert(Environment env, TraceInfo trace, Memory arg) throws Throwable {
        Set result = createTreeSet();

        ForeachIterator newIterator = arg.getNewIterator(env);

        if (newIterator == null) {
            return result;
        }

        for (Memory el : newIterator) {
            result.add(operations[0].convert(env, trace, el));
        }

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Memory unconvert(Environment env, TraceInfo trace, Set arg) throws Throwable {
        if (arg == null) {
            return Memory.NULL;
        }

        ArrayMemory result = new ArrayMemory();
        for (Object el : arg) {
            result.add(operations[0].unconvert(env, trace, el));
        }

        return result.toConstant();
    }

    @Override
    public void applyTypeHinting(ParameterEntity parameter) {
        parameter.setType(HintType.TRAVERSABLE);
    }

    protected Set createTreeSet() {
        return new LinkedHashSet();
    }
}
