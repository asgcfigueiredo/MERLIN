package jphp.runtime.ext.core.classes;

import java.util.Collection;

import jphp.runtime.env.Context;
import jphp.runtime.env.Environment;
import jphp.runtime.ext.core.classes.stream.Stream;
import jphp.runtime.ext.core.reflection.ReflectionClass;
import jphp.runtime.ext.core.reflection.ReflectionFunction;
import jphp.runtime.Memory;
import jphp.runtime.common.AbstractCompiler;
import jphp.runtime.common.HintType;
import jphp.runtime.env.*;
import jphp.runtime.lang.BaseObject;
import jphp.runtime.loader.dump.ModuleDumper;
import jphp.runtime.memory.ArrayMemory;
import jphp.runtime.reflection.ClassEntity;
import jphp.runtime.reflection.ConstantEntity;
import jphp.runtime.reflection.FunctionEntity;
import jphp.runtime.reflection.ModuleEntity;
import jphp.runtime.reflection.helper.ClosureEntity;
import jphp.runtime.reflection.helper.GeneratorEntity;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static jphp.runtime.annotation.Reflection.*;

@Name("php\\lang\\Module")
public class WrapModule extends BaseObject {
    protected ModuleEntity module;
    protected boolean registered = false;

    public WrapModule(Environment env, ModuleEntity module) {
        super(env);
        this.module = module;
    }

    public WrapModule(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }

    @Signature({
            @Arg("source"),
            @Arg(value = "compiled", optional = @Optional("false")),
            @Arg(value = "debugInformation", optional = @Optional("true"))
    })
    public Memory __construct(Environment env, Memory... args) throws Throwable {
        InputStream is = Stream.getInputStream(env, args[0]);
        try {
            if (is != null) {
                is = new BufferedInputStream(is);
            }

            Context context = new Context(is, Stream.getPath(args[0]), env.getDefaultCharset());
            if (args[1].toBoolean()) {
                ModuleDumper moduleDumper = new ModuleDumper(context, env, args[2].toBoolean());
                module = moduleDumper.load(context.getInputStream(env.getDefaultCharset()));
            } else {
                AbstractCompiler compiler = env.scope.createCompiler(env, context);
                module = compiler.compile(false);
            }
            register(env);
        } finally {
            Stream.closeStream(env, is);
        }
        return Memory.NULL;
    }

    protected void loadModule(Environment env) {
        if (!module.isLoaded()) {
            synchronized (env.scope) {
                if (!module.isLoaded()) {
                    env.scope.loadModule(module);
                    env.scope.addUserModule(module);
                }
            }
        }
    }

    protected void register(Environment env, Memory... args) {
        if (registered)
            return;

        loadModule(env);
        env.registerModule(module);

        registered = true;
    }

    @Signature
    public String getName() {
        return module.getName();
    }

    @Signature(@Arg(value = "variables", type = HintType.ARRAY, optional = @Optional("NULL")))
    public Memory call(Environment env, Memory... args) throws Throwable {
        if (!registered)
            register(env);

        if (args[0].isNull())
            return module.include(env);
        else
            return module.include(env, args[0].toImmutable().toValue(ArrayMemory.class));
    }

    @Signature
    public void cleanData() {
        module.setData(null);

        for (ClassEntity entity : module.getClasses()) {
            if (!entity.isTrait()) {
                entity.setData(null);
            }
        }

        for (FunctionEntity entity : module.getFunctions()) {
            entity.setData(null);
        }

        for (GeneratorEntity entity : module.getGenerators()) {
            entity.setData(null);
        }

        for (ClosureEntity entity : module.getClosures()) {
            entity.setData(null);
        }
    }

    @Signature
    public Memory getClasses(Environment env) {
        Collection<ClassEntity> classes = module.getClasses();

        ArrayMemory result = new ArrayMemory();
        for (ClassEntity aClass : classes) {
            ReflectionClass rf = new ReflectionClass(env);
            rf.setEntity(aClass);

            result.put(aClass.getName(), rf);
        }

        return result.toConstant();
    }

    @Signature
    public Memory getFunctions(Environment env) {
        Collection<FunctionEntity> functions = module.getFunctions();

        ArrayMemory result = new ArrayMemory();
        for (FunctionEntity func : functions) {
            result.put(func.getName(), new ReflectionFunction(env, func));
        }

        return result.toConstant();
    }

    @Signature
    public Memory getConstants(Environment env) {
        Collection<ConstantEntity> constants = module.getConstants();

        ArrayMemory result = new ArrayMemory();
        for (ConstantEntity constant : constants) {
            result.put(constant.getName(), constant.getValue(env));
        }

        return result.toConstant();
    }

    @Signature({
            @Arg("target"),
            @Arg(value = "saveDebugInfo", optional = @Optional("true"))
    })
    public Memory dump(Environment env, Memory... args) throws IOException {
        ModuleDumper moduleDumper = new ModuleDumper(module.getContext(), env, args[1].toBoolean());

        OutputStream os = Stream.getOutputStream(env, args[0]);
        try {
            moduleDumper.save(module, os);
        } finally {
            Stream.closeStream(env, os);
        }
        return Memory.NULL;
    }
}
