package jphp.runtime.ext.core.reflection;

import jphp.runtime.Memory;
import jphp.runtime.env.Environment;

import static jphp.runtime.annotation.Reflection.Name;
import static jphp.runtime.annotation.Reflection.Signature;

@Name("Reflector")
public interface Reflector {

    @Signature
    Memory __toString(Environment env, Memory... args);
}
