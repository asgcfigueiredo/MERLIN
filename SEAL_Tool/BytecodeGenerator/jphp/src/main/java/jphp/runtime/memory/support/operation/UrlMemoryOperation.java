package jphp.runtime.memory.support.operation;

import jphp.runtime.Memory;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.memory.StringMemory;
import jphp.runtime.memory.support.MemoryOperation;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlMemoryOperation extends MemoryOperation<URL> {
    @Override
    public Class<?>[] getOperationClasses() {
        return new Class<?>[] { URL.class };
    }

    @Override
    public URL convert(Environment env, TraceInfo trace, Memory arg) throws Throwable {
        try {
            return new URL(arg.toString());
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public Memory unconvert(Environment env, TraceInfo trace, URL arg) throws Throwable {
        return StringMemory.valueOf(arg.toString());
    }
}
