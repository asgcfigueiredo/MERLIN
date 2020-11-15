package jphp.runtime.memory.support.operation.array;

import jphp.runtime.Memory;
import jphp.runtime.common.HintType;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.lang.ForeachIterator;
import jphp.runtime.memory.ArrayMemory;
import jphp.runtime.memory.support.MemoryOperation;
import jphp.runtime.reflection.ParameterEntity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ArrayMemoryOperation extends MemoryOperation {
    protected final MemoryOperation operation;
    protected final Class<?> arrayElementClass;
    protected final Class<?> arrayClass;

    public ArrayMemoryOperation(Class<?> arrayClass) {
        this.operation = MemoryOperation.get(this.arrayElementClass = arrayClass.getComponentType(), null);
        this.arrayClass = arrayClass;
    }

    @Override
    public Class<?>[] getOperationClasses() {
        return new Class<?>[] { arrayClass };
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object convert(Environment env, TraceInfo trace, Memory arg) throws Throwable {
        ForeachIterator iterator = arg.getNewIterator(env);
        if (iterator == null) {
            return null;
        }

        List tmp = new ArrayList();
        while (iterator.next()) {
            tmp.add(operation.convert(env, trace, iterator.getValue()));
        }

        Object[] r = (Object[]) Array.newInstance(arrayElementClass, tmp.size());
        for (int i = 0; i < r.length; i++) {
            r[i] = tmp.get(i);
        }

        return r;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Memory unconvert(Environment env, TraceInfo trace, Object arg) throws Throwable {
        if (arg == null) {
            return Memory.NULL;
        }

        ArrayMemory r = new ArrayMemory();
        for (Object el : (Object[]) arg) {
            r.add(operation.unconvert(env, trace, el));
        }

        return r.toConstant();
    }

    @Override
    public void applyTypeHinting(ParameterEntity parameter) {
        parameter.setType(HintType.TRAVERSABLE);
    }
}
