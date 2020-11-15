package jphp.runtime.memory.support.operation;

import jphp.runtime.Memory;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.memory.*;
import jphp.runtime.memory.*;
import jphp.runtime.memory.helper.CharArrayMemory;
import jphp.runtime.memory.helper.UndefinedMemory;
import jphp.runtime.memory.support.MemoryOperation;

public class MemoryMemoryOperation extends MemoryOperation<Memory> {
    @Override
    public Class<?>[] getOperationClasses() {
        return new Class<?>[] {
                Memory.class,
                NullMemory.class,
                UndefinedMemory.class,
                StringMemory.class,
                LongMemory.class,
                DoubleMemory.class,
                TrueMemory.class,
                FalseMemory.class,
                KeyValueMemory.class,
                BinaryMemory.class,
                StringBuilderMemory.class,
                CharArrayMemory.class,
                CharMemory.class,
                ObjectMemory.class,
                ReferenceMemory.class
        };
    }

    @Override
    public Memory convert(Environment env, TraceInfo trace, Memory arg) throws Throwable {
        return arg == null ? Memory.NULL : arg;
    }

    @Override
    public Memory unconvert(Environment env, TraceInfo trace, Memory arg) throws Throwable {
        return arg == null ? Memory.NULL : arg;
    }
}
