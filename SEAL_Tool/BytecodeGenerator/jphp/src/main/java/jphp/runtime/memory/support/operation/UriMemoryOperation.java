package jphp.runtime.memory.support.operation;

import jphp.runtime.Memory;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.memory.StringMemory;
import jphp.runtime.memory.support.MemoryOperation;

import java.net.URI;

public class UriMemoryOperation extends MemoryOperation<URI> {
    @Override
    public Class<?>[] getOperationClasses() {
        return new Class<?>[] { URI.class };
    }

    @Override
    public URI convert(Environment env, TraceInfo trace, Memory arg) throws Throwable {
        return URI.create(arg.toString());
    }

    @Override
    public Memory unconvert(Environment env, TraceInfo trace, URI arg) throws Throwable {
        return StringMemory.valueOf(arg.toString());
    }
}
