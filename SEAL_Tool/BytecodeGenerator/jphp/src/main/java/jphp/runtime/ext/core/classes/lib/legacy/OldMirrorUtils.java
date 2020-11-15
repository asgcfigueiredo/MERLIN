package jphp.runtime.ext.core.classes.lib.legacy;

import jphp.runtime.annotation.Reflection.Name;
import jphp.runtime.env.Environment;
import jphp.runtime.ext.core.classes.lib.MirrorUtils;
import jphp.runtime.reflection.ClassEntity;

@Name("php\\lib\\Mirror")
public class OldMirrorUtils extends MirrorUtils {
    public OldMirrorUtils(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }
}
