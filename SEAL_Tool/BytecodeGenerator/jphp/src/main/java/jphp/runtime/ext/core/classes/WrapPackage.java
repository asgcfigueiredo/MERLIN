package jphp.runtime.ext.core.classes;

import jphp.runtime.Memory;
import jphp.runtime.annotation.Reflection.Arg;
import jphp.runtime.annotation.Reflection.Name;
import jphp.runtime.annotation.Reflection.Signature;
import jphp.runtime.common.HintType;
import jphp.runtime.env.Environment;
import jphp.runtime.env.Package;
import jphp.runtime.lang.BaseObject;
import jphp.runtime.memory.ArrayMemory;
import jphp.runtime.reflection.ClassEntity;

@Name("php\\lang\\Package")
public class WrapPackage extends BaseObject {
    private Package pkg;

    public WrapPackage(Environment env, Package pkg) {
        super(env);
        this.pkg = pkg;
    }

    public WrapPackage(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }

    public Package getPackage() {
        return pkg;
    }

    @Signature
    public Memory __construct(Environment env, Memory... args) {
        pkg = new Package();
        return Memory.NULL;
    }

    @Signature(@Arg("name"))
    public Memory hasClass(Environment env, Memory... args) {
        return pkg.hasClass(args[0].toString()) ? Memory.TRUE : Memory.FALSE;
    }

    @Signature(@Arg("name"))
    public Memory hasFunction(Environment env, Memory... args) {
        return pkg.hasFunction(args[0].toString()) ? Memory.TRUE : Memory.FALSE;
    }

    @Signature(@Arg("name"))
    public Memory hasConstant(Environment env, Memory... args) {
        return pkg.hasConstant(args[0].toString()) ? Memory.TRUE : Memory.FALSE;
    }

    @Signature(@Arg("name"))
    public Memory addClass(Environment env, Memory... args) {
        return pkg.addClass(args[0].toString()) ? Memory.TRUE : Memory.FALSE;
    }

    @Signature(@Arg(value = "names", type = HintType.ARRAY))
    public Memory addClasses(Environment env, Memory... args) {
        for (Memory one : args[0].toValue(ArrayMemory.class)) {
            pkg.addClass(one.toString());
        }

        return Memory.NULL;
    }

    @Signature(@Arg("name"))
    public Memory addFunction(Environment env, Memory... args) {
        return pkg.addFunction(args[0].toString()) ? Memory.TRUE : Memory.FALSE;
    }

    @Signature(@Arg(value = "names", type = HintType.ARRAY))
    public Memory addFunctions(Environment env, Memory... args) {
        for (Memory one : args[0].toValue(ArrayMemory.class)) {
            pkg.addFunction(one.toString());
        }

        return Memory.NULL;
    }

    @Signature(@Arg("name"))
    public Memory addConstant(Environment env, Memory... args) {
        return pkg.addConstant(args[0].toString()) ? Memory.TRUE : Memory.FALSE;
    }


    @Signature(@Arg(value = "names", type = HintType.ARRAY))
    public Memory addConstants(Environment env, Memory... args) {
        for (Memory one : args[0].toValue(ArrayMemory.class)) {
            pkg.addConstant(one.toString());
        }

        return Memory.NULL;
    }
}
