package jphp.runtime.ext.core.classes.lib.legacy;

import jphp.runtime.annotation.Reflection.Name;
import jphp.runtime.env.Environment;
import jphp.runtime.ext.core.classes.lib.ItemsUtils;
import jphp.runtime.reflection.ClassEntity;

@Name("php\\lib\\Items")
public class OldItemsUtils extends ItemsUtils {
    public OldItemsUtils(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }
}
