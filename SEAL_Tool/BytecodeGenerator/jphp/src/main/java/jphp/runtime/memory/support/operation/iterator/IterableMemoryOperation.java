package jphp.runtime.memory.support.operation.iterator;

import jphp.runtime.Memory;
import jphp.runtime.common.HintType;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.exceptions.CriticalException;
import jphp.runtime.lang.ForeachIterator;
import jphp.runtime.memory.support.MemoryOperation;
import jphp.runtime.memory.support.operation.GenericMemoryOperation;
import jphp.runtime.reflection.ParameterEntity;

import java.lang.reflect.Type;
import java.util.Iterator;

public class IterableMemoryOperation extends GenericMemoryOperation<Iterable> {

    public IterableMemoryOperation(Type... genericTypes) {
        super(genericTypes);
        if (genericTypes == null) {
            operations = new MemoryOperation[] { MemoryOperation.get(Memory.class, null) };
        }
    }

    @Override
    public Class<?>[] getOperationClasses() {
        return new Class<?>[] { Iterable.class };
    }

    @Override
    public Iterable convert(final Environment env, final TraceInfo trace, Memory arg) throws Throwable {
        final ForeachIterator iterator = arg.getNewIterator(env);

        return new Iterable() {
            @Override
            public Iterator iterator() {
                return new Iterator() {
                    protected Boolean hasNext;

                    @Override
                    public boolean hasNext() {
                        if (hasNext == null) {
                            hasNext = iterator.next();
                        }
                        return hasNext;
                    }

                    @Override
                    public Object next() {
                        if (hasNext == null) {
                            next();
                        } else {
                            hasNext = null;
                        }

                        return operations[0].convertNoThrow(env, trace, iterator.getValue());
                    }

                    @Override
                    public void remove() {
                        throw new IllegalStateException("Unsupported remove() method");
                    }
                };
            }
        };
    }

    @Override
    public Memory unconvert(Environment env, TraceInfo trace, Iterable arg) throws Throwable {
        throw new CriticalException("Unsupported operation");
    }

    @Override
    public void applyTypeHinting(ParameterEntity parameter) {
        parameter.setType(HintType.TRAVERSABLE);
    }
}
