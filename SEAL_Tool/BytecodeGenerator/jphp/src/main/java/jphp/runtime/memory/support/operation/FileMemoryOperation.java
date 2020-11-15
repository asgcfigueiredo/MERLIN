package jphp.runtime.memory.support.operation;

import jphp.runtime.ext.core.classes.stream.FileObject;
import jphp.runtime.Memory;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.memory.ObjectMemory;
import jphp.runtime.memory.support.MemoryOperation;

import java.io.File;

public class FileMemoryOperation extends MemoryOperation<File> {
    @Override
    public Class<?>[] getOperationClasses() {
        return new Class<?>[] { File.class };
    }

    @Override
    public File convert(Environment env, TraceInfo trace, Memory arg) throws Throwable {
        return arg.isNull() ? null : FileObject.valueOf(arg);
    }

    @Override
    public Memory unconvert(Environment env, TraceInfo trace, File arg) throws Throwable {
        return arg == null ? Memory.NULL : ObjectMemory.valueOf(new FileObject(env, arg));
    }
}
