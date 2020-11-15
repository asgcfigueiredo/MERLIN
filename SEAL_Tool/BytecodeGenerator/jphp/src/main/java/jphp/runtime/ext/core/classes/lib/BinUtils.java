package jphp.runtime.ext.core.classes.lib;

import jphp.runtime.Memory;
import jphp.runtime.common.HintType;
import jphp.runtime.env.Environment;
import jphp.runtime.lang.BaseObject;
import jphp.runtime.lang.ForeachIterator;
import jphp.runtime.memory.BinaryMemory;
import jphp.runtime.reflection.ClassEntity;
import jphp.runtime.reflection.ParameterEntity;

import java.io.ByteArrayOutputStream;

import static jphp.runtime.annotation.Reflection.*;
import static jphp.runtime.annotation.Runtime.FastMethod;

@Name("php\\lib\\Bin")
public class BinUtils extends BaseObject {
    public BinUtils(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }

    @Signature
    private Memory __construct(Environment env, Memory... args) { return Memory.NULL; }

    @FastMethod
    @Signature(@Arg("value"))
    public static Memory of(Environment env, Memory... args) {
        if (ParameterEntity.checkTypeHinting(env, args[0], HintType.TRAVERSABLE)) {
            ForeachIterator iterator = args[0].getNewIterator(env, false, false);
            ByteArrayOutputStream tmp = new ByteArrayOutputStream();
            while (iterator.next()) {
                tmp.write(iterator.getValue().toInteger());
            }
            return new BinaryMemory(tmp.toByteArray());
        }

        return new BinaryMemory(args[0].getBinaryBytes(env.getDefaultCharset()));
    }
}
