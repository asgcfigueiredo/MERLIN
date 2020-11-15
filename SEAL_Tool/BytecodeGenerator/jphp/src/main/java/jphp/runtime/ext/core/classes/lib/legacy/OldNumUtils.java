package jphp.runtime.ext.core.classes.lib.legacy;

import jphp.runtime.annotation.Reflection.Name;
import jphp.runtime.env.Environment;
import jphp.runtime.ext.core.classes.lib.NumUtils;
import jphp.runtime.reflection.ClassEntity;

@Name("php\\lib\\Number")
public class OldNumUtils extends NumUtils {
    public OldNumUtils(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }
}
