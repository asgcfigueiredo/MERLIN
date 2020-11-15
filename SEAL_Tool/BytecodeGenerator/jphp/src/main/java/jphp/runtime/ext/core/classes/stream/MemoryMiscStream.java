package jphp.runtime.ext.core.classes.stream;

import jphp.runtime.Memory;
import jphp.runtime.env.Environment;
import jphp.runtime.memory.StringMemory;
import jphp.runtime.reflection.ClassEntity;

import java.io.IOException;

import static jphp.runtime.annotation.Reflection.*;

@Name("php\\io\\MemoryStream")
public class MemoryMiscStream extends MiscStream {
    public MemoryMiscStream(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }

    @Override
    @Signature({@Arg(value = "content", optional = @Optional("null")), @Arg(value = "mode", optional = @Optional("r"))})
    public Memory __construct(Environment env, Memory... args) throws IOException {
        super.__construct(env, new StringMemory("memory"), args[1]);

        if (args[0].isNotNull()) {
            write(env, args[0], Memory.NULL);
            seek(env, Memory.CONST_INT_0);
        }

        return Memory.NULL;
    }
}
