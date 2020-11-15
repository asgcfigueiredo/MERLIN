package jphp.runtime.ext.core.classes;

import jphp.runtime.env.Environment;
import jphp.runtime.env.PackageLoader;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.ext.core.classes.stream.ResourceStream;
import jphp.runtime.ext.core.classes.stream.Stream;
import jphp.runtime.Memory;
import jphp.runtime.annotation.Reflection.Arg;
import jphp.runtime.annotation.Reflection.Name;
import jphp.runtime.annotation.Reflection.Signature;
import jphp.runtime.env.*;
import jphp.runtime.env.Package;
import jphp.runtime.ext.CoreExtension;
import jphp.runtime.invoke.DynamicMethodInvoker;
import jphp.runtime.invoke.Invoker;
import jphp.runtime.lang.BaseObject;
import jphp.runtime.lang.ForeachIterator;
import jphp.runtime.memory.ObjectMemory;
import jphp.runtime.memory.StringMemory;
import jphp.runtime.reflection.ClassEntity;

import java.io.InputStream;
import java.util.Scanner;

@Name("php\\lang\\PackageLoader")
abstract public class WrapPackageLoader extends BaseObject {
    protected PackageLoader loader;

    public WrapPackageLoader(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }

    @Signature
    public void __construct() {
    }

    @Signature({@Arg("name")})
    abstract public Memory load(Environment env, Memory... args) throws Throwable;

    @Signature
    public void register(Environment env) {
        if (loader == null) {
            env.getPackageManager().registerLoader(_getPackageLoader(env));
        } else {
            env.exception("PackageLoader is already registered");
        }
    }

    @Signature
    public void unregister(Environment env) {
        if (loader == null) {
            env.exception("PackageLoader is not registered");
        }

        env.getPackageManager().unregisterLoader(loader);
        loader = null;
    }

    synchronized protected PackageLoader _getPackageLoader(final Environment env) {
        if (loader == null) {
            final Invoker invoker = DynamicMethodInvoker.valueOf(env, env.trace(), this, "load");

            loader = new PackageLoader() {
                @Override
                public Package load(String name, TraceInfo trace) {
                    if (invoker == null) {
                        env.error(trace, "Cannot call load() method of packageLoader");
                        return null;
                    }

                    //invoker.setTrace(trace);
                    //invoker.setPushCallTrace(true);
                    Memory ret = invoker.callNoThrow(StringMemory.valueOf(name));

                    if (ret.isNull()) {
                        return null;
                    }

                    if (ret.instanceOf(WrapPackage.class)) {
                        return ret.toObject(WrapPackage.class).getPackage();
                    }

                    env.error(trace, getReflection().getName() + "::load() method must return an instance of php\\lang\\Package");
                    return null;
                }
            };
        }

        return loader;
    }


    @Name(CoreExtension.NAMESPACE + "util\\LauncherPackageLoader")
    public static class WrapLauncherPackageLoader extends WrapPackageLoader {
        public WrapLauncherPackageLoader(Environment env, ClassEntity clazz) {
            super(env, clazz);
        }

        @Override
        @Signature({@Arg("name")})
        public Memory load(Environment env, Memory... args) throws Throwable {
            String name = args[0].toString();
            String packageUrl = "JPHP-INF/packages/" + name;
            Package pkg = new Package();

            ForeachIterator resources = ResourceStream.getResources(env, StringMemory.valueOf(packageUrl)).getNewIterator(env);

            boolean found = false;

            while (resources.next()) {
                int type = 0;

                InputStream inputStream = Stream.getInputStream(env, resources.getValue());

                if (inputStream == null) {
                    continue;
                }

                try {
                    Scanner scanner = new Scanner(inputStream);

                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine().trim();

                        if (line.isEmpty() || line.startsWith("#")) {
                            continue;
                        }

                        if (line.startsWith("[") && line.endsWith("]")) {
                            switch (line) {
                                case "[classes]":
                                    type = 1;
                                    break;
                                case "[functions]":
                                    type = 2;
                                    break;
                                case "[constants]":
                                    type = 3;
                                    break;
                            }
                        } else {
                            switch (type) {
                                case 1:
                                    pkg.addClass(line);
                                    break;
                                case 2:
                                    pkg.addFunction(line);
                                    break;
                                case 3:
                                    pkg.addConstant(line);
                                    break;
                            }
                        }
                    }

                    found = true;
                } finally {
                    Stream.closeStream(env, inputStream);
                }
            }

            if (found) {
                return ObjectMemory.valueOf(new WrapPackage(env, pkg));
            } else {
                return Memory.NULL;
            }
        }
    }
}
