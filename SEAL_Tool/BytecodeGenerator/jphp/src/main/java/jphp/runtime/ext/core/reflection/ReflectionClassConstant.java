package jphp.runtime.ext.core.reflection;

import jphp.runtime.Memory;
import jphp.runtime.annotation.Reflection.Arg;
import jphp.runtime.annotation.Reflection.Name;
import jphp.runtime.annotation.Reflection.Signature;
import jphp.runtime.common.HintType;
import jphp.runtime.env.Environment;
import jphp.runtime.lang.BaseObject;
import jphp.runtime.memory.StringMemory;
import jphp.runtime.reflection.ClassEntity;
import jphp.runtime.reflection.ConstantEntity;

@Name("ReflectionClassConstant")
@Signature({
        @Arg(value = "name", type = HintType.STRING, readOnly = true),
        @Arg(value = "class", type = HintType.STRING, readOnly = true)
})
public class ReflectionClassConstant extends BaseObject {
    public final static int IS_PUBLIC = 256 ;
    public final static int IS_PROTECTED = 512 ;
    public final static int IS_PRIVATE = 1024 ;

    private ConstantEntity entity;

    public ReflectionClassConstant(Environment env, ConstantEntity constantEntity) {
        super(env);

        this.entity = constantEntity;

        setProp("name", constantEntity.getName());
        setProp("class", constantEntity.getClazz().getName());
    }

    public ReflectionClassConstant(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }

    @Signature({
            @Arg("class"),
            @Arg("name"),
    })
    public Memory __construct(Environment env, Memory... args) {
        String className = args[0].toString();
        String constName = args[1].toString();

        setProp("class", className);
        setProp("name", constName);

        ClassEntity classEntity = env.fetchClass(className);
        entity = classEntity.findConstant(constName);

        if (entity == null) {
            env.exception(ReflectionException.class, "Class Constant %s::%s does not exist", className, constName);
        }

        return Memory.UNDEFINED;
    }

    @Signature
    public String getDocComment() {
        return entity.getDocComment() == null ? null : entity.getDocComment().toString();
    }

    @Signature
    public int getModifiers() {
        int mod = 0;

        if (entity.isPrivate())
            mod |= IS_PRIVATE;
        else if (entity.isProtected())
            mod |= IS_PROTECTED;
        else if (entity.isPublic())
            mod |= IS_PUBLIC;

        return mod;
    }

    @Signature
    public boolean isPrivate() {
        return entity.isPrivate();
    }

    @Signature
    public boolean isProtected() {
        return entity.isProtected();
    }

    @Signature
    public boolean isPublic() {
        return entity.isPublic();
    }

    @Signature
    public Memory getValue(Environment env) {
        return entity.getValue(env).toImmutable();
    }

    @Signature
    public ReflectionClass getDeclaringClass(Environment env) {
        ReflectionClass reflectionClass = new ReflectionClass(env);
        reflectionClass.setEntity(entity.getClazz());
        return reflectionClass;
    }

    @Signature
    public Memory __toString(Environment env, Memory... args) {
        return StringMemory.valueOf(
                "Constant [ " + entity.getModifier().name().toLowerCase() + " " + entity.getName() + " ] { " + entity.getValue(env).toString() + " }"
        );
    }
}
