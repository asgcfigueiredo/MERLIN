package jphp.runtime.ext;

import jphp.runtime.ext.java.JavaException;
import jphp.runtime.ext.support.Extension;
import jphp.runtime.lang.BaseObject;
import jphp.runtime.reflection.ClassEntity;
import jphp.runtime.env.CompileScope;
import jphp.runtime.env.Environment;
import jphp.runtime.ext.java.*;

public class JavaExtension extends Extension {
    @Override
    public String getName() {
        return "Java";
    }

    @Override
    public Status getStatus() {
        return Status.EXPERIMENTAL;
    }

    @Override
    public void onRegister(CompileScope scope) {
        registerClass(scope, JavaException.class);
    }

    public static class JavaObject extends BaseObject {
        public JavaObject(Environment env, ClassEntity clazz) {
            super(env, clazz);
        }
    }
}
