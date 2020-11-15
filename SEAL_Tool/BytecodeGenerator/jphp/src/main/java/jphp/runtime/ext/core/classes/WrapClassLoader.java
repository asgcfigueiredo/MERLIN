package jphp.runtime.ext.core.classes;

import jphp.runtime.annotation.Reflection.Name;
import jphp.runtime.annotation.Reflection.Signature;
import jphp.runtime.env.Environment;
import jphp.runtime.env.SplClassLoader;
import jphp.runtime.ext.CoreExtension;
import jphp.runtime.invoke.Invoker;
import jphp.runtime.lang.BaseObject;
import jphp.runtime.memory.ArrayMemory;
import jphp.runtime.reflection.ClassEntity;
import jphp.runtime.reflection.ModuleEntity;

@Name("php\\lang\\ClassLoader")
abstract public class WrapClassLoader extends BaseObject {
    protected SplClassLoader splClassLoader;

    public WrapClassLoader(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }

    @Signature
    public void __construct() { }

    @Signature
    abstract public boolean loadClass(Environment env, String className) throws Throwable;

    @Signature
    public void register(Environment env, boolean prepend) {
        if (splClassLoader == null) {
            env.registerAutoloader(_getSplClassLoader(env), prepend);
        } else {
            env.exception("ClassLoader is already registered");
        }
    }

    @Signature
    public void unregister(Environment env) {
        if (splClassLoader == null) {
            env.exception("ClassLoader is not registered");
        }

        env.unRegisterAutoloader(splClassLoader);
        splClassLoader = null;
    }

    synchronized protected SplClassLoader _getSplClassLoader(Environment env) {
        if (splClassLoader == null) {
            ArrayMemory callback = new ArrayMemory();
            callback.add(this);
            callback.add("loadClass");

            Invoker invoker = Invoker.valueOf(env, null, callback);
            splClassLoader = new SplClassLoader(invoker, callback);
        }

        return splClassLoader;
    }

    @Name(CoreExtension.NAMESPACE + "util\\LauncherClassLoader")
    public static class WrapLauncherClassLoader extends WrapClassLoader {
        public WrapLauncherClassLoader(Environment env, ClassEntity clazz) {
            super(env, clazz);
        }

        @Override
        @Signature
        public boolean loadClass(Environment env, String className) throws Throwable {
            String file = className.replace('\\', '/');

            if (file.startsWith("\\") || file.startsWith("/")) {
                file = file.substring(1);
            }

            ModuleEntity entity = fetchClass(env, file + ".phb", true);

            if (entity != null) {
                entity.include(env);
                return true;
            }

            entity = fetchClass(env, file + ".php", false);

            if (entity != null) {
                entity.include(env);
                return true;
            }

            return false;
        }

        protected ModuleEntity fetchClass(Environment env, String fileName, boolean compiled) throws Throwable {
            return env.getModuleManager().fetchTemporaryModule("res://" + fileName, compiled);
        }
    }
}
