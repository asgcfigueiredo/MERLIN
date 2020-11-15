package jphp.runtime.lang.spl.iterator;

import jphp.runtime.Memory;
import jphp.runtime.annotation.Reflection;
import jphp.runtime.env.Environment;

@Reflection.Name("OuterIterator")
public interface OuterIterator extends Iterator {

    @Reflection.Signature
    public Memory getInnerIterator(Environment env, Memory... args);
}
