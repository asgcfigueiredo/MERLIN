package jphp.runtime.memory.helper;

import jphp.runtime.Memory;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.memory.ReferenceMemory;

public class ConstantMemory extends ReferenceMemory {
    private final String name;
    private final String lowerName;

    public ConstantMemory(String name){
        super();
        this.name = name;
        this.lowerName = name.toLowerCase();
    }

    @Override
    public Memory toImmutable(Environment env, TraceInfo trace) {
        return env.__getConstant(name, lowerName, trace);
    }

    public String getName() {
        return name;
    }

    @Override
    public Memory toValue() {
        return this;
    }
}
