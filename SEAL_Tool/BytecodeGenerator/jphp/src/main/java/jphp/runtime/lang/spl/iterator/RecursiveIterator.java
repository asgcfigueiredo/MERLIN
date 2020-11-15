package jphp.runtime.lang.spl.iterator;

import jphp.runtime.Memory;
import jphp.runtime.env.Environment;

import static jphp.runtime.annotation.Reflection.Name;
import static jphp.runtime.annotation.Reflection.Signature;

@Name("RecursiveIterator")
public interface RecursiveIterator extends Iterator {

    @Signature
    public Memory getChildren(Environment env, Memory... args);

    @Signature
    public Memory hasChildren(Environment env, Memory... args);
}
