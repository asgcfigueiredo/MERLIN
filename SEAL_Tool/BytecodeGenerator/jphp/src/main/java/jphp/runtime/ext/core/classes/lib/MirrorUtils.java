package jphp.runtime.ext.core.classes.lib;

import jphp.runtime.Memory;
import jphp.runtime.annotation.Reflection.Optional;
import jphp.runtime.annotation.Reflection.Signature;
import jphp.runtime.annotation.Runtime.FastMethod;
import jphp.runtime.common.HintType;
import jphp.runtime.common.Messages;
import jphp.runtime.env.Environment;
import jphp.runtime.ext.core.classes.WrapModule;
import jphp.runtime.lang.BaseObject;
import jphp.runtime.memory.ArrayMemory;
import jphp.runtime.memory.ObjectMemory;
import jphp.runtime.memory.StringMemory;
import jphp.runtime.reflection.ClassEntity;
import jphp.runtime.reflection.FunctionEntity;

import static jphp.runtime.annotation.Reflection.Arg;
import static jphp.runtime.annotation.Reflection.Name;

@Name("php\\lib\\Reflect")
public class MirrorUtils extends BaseObject {
    public MirrorUtils(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }

    @Signature
    private void __construct() {}

    @FastMethod
    @Signature({
            @Arg("object"),
            @Arg(value = "toLower", optional = @Optional("false"))
    })
    public static Memory typeOf(Environment env, Memory... args) {
        if (args[0].isObject()) {
            ClassEntity entity = args[0].toValue(ObjectMemory.class).getReflection();
            return StringMemory.valueOf(args[1].toBoolean() ? entity.getLowerName() : entity.getName());
        } else {
            return Memory.FALSE;
        }
    }

    @FastMethod
    @Signature({
            @Arg("typeName")
    })
    public static Memory typeModule(Environment env, Memory... args) {
        ClassEntity classEntity = env.fetchClass(args[0].toString(), true);

        if (classEntity != null && classEntity.getModule() != null) {
            return ObjectMemory.valueOf(new WrapModule(env, classEntity.getModule()));
        }

        return Memory.NULL;
    }

    @FastMethod
    @Signature({
            @Arg("funcName")
    })
    public static Memory functionModule(Environment env, Memory... args) {
        FunctionEntity entity = env.fetchFunction(args[0].toString());

        if (entity != null && entity.getModule() != null) {
            return ObjectMemory.valueOf(new WrapModule(env, entity.getModule()));
        }

        return Memory.NULL;
    }

    @Signature({
            @Arg("className"),
            @Arg(value = "args", type = HintType.ARRAY, optional = @Optional("null")),
            @Arg(value = "withConstruct", optional = @Optional("true"))
    })
    public static Memory newInstance(Environment env, Memory... args) throws Throwable {
        ClassEntity entity = env.fetchClass(args[0].toString(), true);

        if (entity == null) {
            env.exception(env.trace(), Messages.ERR_CLASS_NOT_FOUND.fetch(args[0]));
            return Memory.NULL;
        } else {
            return new ObjectMemory(entity.newObject(
                    env,
                    env.trace(),
                    args[2].toBoolean(),
                    args[1].isNull() ? null : args[1].toValue(ArrayMemory.class).values(true)
            ));
        }
    }
}
