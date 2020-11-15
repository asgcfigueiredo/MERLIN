package jphp.runtime.memory.support.operation;

import jphp.runtime.Memory;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.memory.BinaryMemory;
import jphp.runtime.memory.support.MemoryOperation;


public class BinaryMemoryOperation extends MemoryOperation<byte[]> {
    @Override
    public Class<?>[] getOperationClasses() {
        return new Class<?>[] { byte[].class };
    }

    @Override
    public byte[] convert(Environment environment, TraceInfo traceInfo, Memory memory) throws Throwable {
        return memory.getBinaryBytes(environment.getDefaultCharset());
    }

    @Override
    public Memory unconvert(Environment environment, TraceInfo traceInfo, byte[] bytes) throws Throwable {
        return bytes == null ? Memory.NULL : new BinaryMemory(bytes);
    }
}
