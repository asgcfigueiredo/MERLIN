package jphp.runtime.memory.support.operation;

import jphp.runtime.ext.core.classes.stream.MiscStream;
import jphp.runtime.ext.core.classes.stream.Stream;
import jphp.runtime.Memory;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.memory.ObjectMemory;
import jphp.runtime.memory.support.MemoryOperation;

import java.io.InputStream;

public class InputStreamMemoryOperation extends MemoryOperation<InputStream> {
    @Override
    public Class<?>[] getOperationClasses() {
        return new Class<?>[] { InputStream.class };
    }

    @Override
    public InputStream convert(Environment env, TraceInfo trace, Memory arg) throws Throwable {
        return Stream.getInputStream(env, arg);
    }

    @Override
    public Memory unconvert(Environment env, TraceInfo trace, InputStream arg) throws Throwable {
        return ObjectMemory.valueOf(new MiscStream(env, arg));
    }

    @Override
    public void releaseConverted(Environment env, TraceInfo info, InputStream arg) {
        Stream.closeStream(env, arg);
    }
}
