package jphp.runtime.memory.support.operation;

import jphp.runtime.Memory;
import jphp.runtime.common.HintType;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.exceptions.CriticalException;
import jphp.runtime.lang.ForeachIterator;
import jphp.runtime.memory.support.MemoryOperation;
import jphp.runtime.reflection.ParameterEntity;
import jphp.runtime.reflection.support.TypeChecker;

public class ForeachIteratorMemoryOperation extends MemoryOperation<ForeachIterator> {
    @Override
    public Class<?>[] getOperationClasses() {
        return new Class<?>[] { ForeachIterator.class };
    }

    @Override
    public ForeachIterator convert(Environment env, TraceInfo trace, Memory arg) throws Throwable {
        return arg.isNull() ? null : arg.getNewIterator(env);
    }

    @Override
    public Memory unconvert(Environment env, TraceInfo trace, ForeachIterator arg) throws Throwable {
        throw new CriticalException("Unsupported operation");
    }

    @Override
    public void applyTypeHinting(ParameterEntity parameter) {
        parameter.setTypeChecker(TypeChecker.of(HintType.TRAVERSABLE));
    }
}
