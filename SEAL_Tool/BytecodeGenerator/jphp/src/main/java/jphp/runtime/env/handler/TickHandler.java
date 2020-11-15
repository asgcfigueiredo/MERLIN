package jphp.runtime.env.handler;

import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.memory.ArrayMemory;

@FunctionalInterface
public interface TickHandler {
    void onTick(Environment env, TraceInfo trace, ArrayMemory locals);
}
