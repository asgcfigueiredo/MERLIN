package jphp.runtime.memory.support.operation;

import jphp.runtime.ext.core.classes.util.WrapRegex;
import jphp.runtime.Memory;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.memory.ObjectMemory;
import jphp.runtime.memory.support.MemoryOperation;

import java.util.regex.Pattern;

public class PatternMemoryOperation extends MemoryOperation<Pattern> {
    @Override
    public Class<?>[] getOperationClasses() {
        return new Class<?>[] { Pattern.class};
    }

    @Override
    public Pattern convert(Environment env, TraceInfo trace, Memory arg) throws Throwable {
        if (arg.instanceOf(WrapRegex.class)) {
            return arg.toObject(WrapRegex.class).getMatcher().pattern();
        } else {
            return Pattern.compile(arg.toString());
        }
    }

    @Override
    public Memory unconvert(Environment env, TraceInfo trace, Pattern arg) throws Throwable {
        return ObjectMemory.valueOf(new WrapRegex(env, arg.matcher(""), ""));
    }
}
