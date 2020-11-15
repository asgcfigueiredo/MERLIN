package jphp.runtime.lang.spl;

import jphp.runtime.Memory;
import jphp.runtime.annotation.Reflection;
import jphp.runtime.env.Environment;
import jphp.runtime.lang.IObject;

@Reflection.Name("Countable")
public interface Countable extends IObject {

    @Reflection.Signature
    public Memory count(Environment env, Memory... args);
}
