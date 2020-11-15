package jphp.runtime.ext.core.reflection;

import jphp.runtime.Memory;
import jphp.runtime.common.HintType;
import jphp.runtime.env.Environment;
import jphp.runtime.reflection.ClassEntity;

import static jphp.runtime.annotation.Reflection.*;

@Name("ReflectionObject")
public class ReflectionObject extends ReflectionClass {

    public ReflectionObject(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }

    @Override
    @Signature(@Arg(value = "object", type = HintType.OBJECT))
    public Memory __construct(Environment env, Memory... args) {
        return super.__construct(env, args);
    }
}
