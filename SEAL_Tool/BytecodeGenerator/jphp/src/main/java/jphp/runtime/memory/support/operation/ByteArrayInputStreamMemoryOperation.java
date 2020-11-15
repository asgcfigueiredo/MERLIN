package jphp.runtime.memory.support.operation;

import jphp.runtime.Memory;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.exceptions.CriticalException;
import jphp.runtime.memory.BinaryMemory;
import jphp.runtime.memory.support.MemoryOperation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ByteArrayInputStreamMemoryOperation extends MemoryOperation<ByteArrayInputStream> {
    @Override
    public Class<?>[] getOperationClasses() {
        return new Class<?>[] { ByteArrayInputStream.class };
    }

    @Override
    public ByteArrayInputStream convert(Environment env, TraceInfo trace, Memory arg) throws Throwable {
        return new ByteArrayInputStream(arg.getBinaryBytes(env.getDefaultCharset()));
    }

    @Override
    public Memory unconvert(Environment env, TraceInfo trace, ByteArrayInputStream arg) throws Throwable {
        byte[] buffer = new byte[4096];
        try {
            ByteBuffer result = ByteBuffer.allocate(20);

            int len;
            while ((len = arg.read(buffer)) > 0) {
                // nop
                result.put(buffer, 0, len);
            }

            return new BinaryMemory(result.array());
        } catch (IOException e) {
            throw new CriticalException(e);
        }
    }
}
