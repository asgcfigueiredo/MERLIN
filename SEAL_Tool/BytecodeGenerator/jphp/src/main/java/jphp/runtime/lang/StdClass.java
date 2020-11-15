package jphp.runtime.lang;

import jphp.runtime.annotation.Reflection;
import jphp.runtime.reflection.ClassEntity;
import jphp.runtime.env.Environment;

@Reflection.Name("stdClass")
public class StdClass extends BaseObject {
    public StdClass(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }

    public StdClass(Environment env) {
        super(env);
    }
}
