package jphp.runtime.memory.support.operation;

import jphp.runtime.ext.core.classes.util.WrapScanner;
import jphp.runtime.Memory;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.memory.ObjectMemory;
import jphp.runtime.memory.support.MemoryOperation;
import jphp.runtime.reflection.ParameterEntity;

import java.util.Scanner;

public class ScannerMemoryOperation extends MemoryOperation<Scanner> {
    @Override
    public Class<?>[] getOperationClasses() {
        return new Class<?>[] { Scanner.class };
    }

    @Override
    public Scanner convert(Environment env, TraceInfo trace, Memory arg) throws Throwable {
        if (arg.isNull()) {
            return null;
        }

        return arg.toObject(WrapScanner.class).getScanner();
    }

    @Override
    public Memory unconvert(Environment env, TraceInfo trace, Scanner arg) throws Throwable {
        if (arg == null) {
            return Memory.NULL;
        }

        return ObjectMemory.valueOf(new WrapScanner(env, arg));
    }

    @Override
    public void applyTypeHinting(ParameterEntity parameter) {
        parameter.setTypeNativeClass(WrapScanner.class);
    }
}
