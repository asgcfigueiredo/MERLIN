package jphp.runtime.ext.core.classes.lib.legacy;

import jphp.runtime.annotation.Reflection.Name;
import jphp.runtime.env.Environment;
import jphp.runtime.ext.core.classes.lib.BinUtils;
import jphp.runtime.reflection.ClassEntity;

@Name("php\\lib\\Binary")
public class OldBinUtils extends BinUtils {
    public OldBinUtils(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }
}
