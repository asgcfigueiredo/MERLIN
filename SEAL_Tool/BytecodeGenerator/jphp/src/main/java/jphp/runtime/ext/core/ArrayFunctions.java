package jphp.runtime.ext.core;

import jphp.runtime.ext.support.compile.FunctionsContainer;
import jphp.runtime.lang.spl.Countable;
import jphp.runtime.Memory;
import jphp.runtime.annotation.Runtime;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.lang.ForeachIterator;
import jphp.runtime.memory.ArrayMemory;
import jphp.runtime.memory.LongMemory;
import jphp.runtime.memory.ObjectMemory;
import jphp.runtime.memory.TrueMemory;

import java.util.HashSet;
import java.util.Set;

public class ArrayFunctions extends FunctionsContainer {

    public static final String COUNT_WARN_MSG = "count(): Parameter must be an array or an object that implements Countable";

    private static int recursive_count(Environment env, TraceInfo trace, ArrayMemory array, Set<Integer> used){
        ForeachIterator iterator = array.foreachIterator(false, false);
        int size = array.size();
        while (iterator.next()){
            Memory el = iterator.getValue();
            if (el.isArray()){
                if (used == null)
                    used = new HashSet<>();

                int pointer = el.getPointer();

                if (!used.add(pointer)){
                    env.warning(trace, "recursion detected");
                } else {
                    size += recursive_count(env, trace, array, used);
                }
                used.remove(pointer);
            }
        }
        return size;
    }

    public static Memory is_countable(Environment env, TraceInfo traceInfo, Memory var) {
        switch (var.type) {
            case ARRAY:
                return Memory.TRUE;
            case OBJECT:
                return TrueMemory.valueOf(var.instanceOf(Countable.class));
        }

        return Memory.FALSE;
    }

    public static Memory count(Environment env, TraceInfo trace, Memory var, int mode){
        switch (var.type){
            case ARRAY:
                if (mode == 1){
                    return LongMemory.valueOf(recursive_count(env, trace, var.toValue(ArrayMemory.class), null));
                } else
                    return LongMemory.valueOf(var.toValue(ArrayMemory.class).size());
            case NULL:
                env.warning(trace, COUNT_WARN_MSG);
                return Memory.CONST_INT_0;
            case OBJECT:
                ObjectMemory objectMemory = var.toValue(ObjectMemory.class);
                if (objectMemory.value instanceof Countable){
                    try {
                        return env.invokeMethod(objectMemory, "count");
                    } catch (Throwable throwable) {
                        env.forwardThrow(throwable);
                    }
                } else {
                    env.warning(trace, COUNT_WARN_MSG);
                    return Memory.CONST_INT_1;
                }
            default:
                env.warning(trace, COUNT_WARN_MSG);
                return Memory.CONST_INT_1;
        }
    }

    @Runtime.Immutable
    public static Memory count(Environment env, TraceInfo trace, Memory var){
        return count(env, trace, var, 0);
    }

    @Runtime.Immutable
    public static Memory sizeof(Environment env, TraceInfo trace, Memory var){
        return count(env, trace, var, 0);
    }

    @Runtime.Immutable
    public static Memory sizeof(Environment env, TraceInfo trace, Memory var, int mode){
        return count(env, trace, var, mode);
    }
}
