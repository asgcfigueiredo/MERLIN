package jphp.runtime.memory.support.operation;

import jphp.runtime.ext.core.classes.stream.MiscStream;
import jphp.runtime.ext.core.classes.stream.Stream;
import jphp.runtime.Memory;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.memory.ObjectMemory;
import jphp.runtime.memory.support.MemoryOperation;

import java.io.OutputStream;

public class OutputStreamMemoryOperation extends MemoryOperation<OutputStream> {
    @Override
    public Class<?>[] getOperationClasses() {
        return new Class<?>[] { OutputStream.class };
    }

    @Override
    public OutputStream convert(Environment env, TraceInfo trace, Memory arg) throws Throwable {
        return Stream.getOutputStream(env, arg);
    }

    @Override
    public Memory unconvert(Environment env, TraceInfo trace, OutputStream arg) throws Throwable {
        return ObjectMemory.valueOf(new MiscStream(env, arg));
    }

    @Override
    public void releaseConverted(Environment env, TraceInfo info, OutputStream arg) {
        Stream.closeStream(env, arg);
    }
}
