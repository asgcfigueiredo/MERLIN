package jphp.runtime.lang.spl.iterator;

import jphp.runtime.Memory;
import jphp.runtime.annotation.Reflection;
import jphp.runtime.env.Environment;
import jphp.runtime.lang.IObject;

@Reflection.Name("IteratorAggregate")
public interface IteratorAggregate extends IObject {

    @Reflection.Signature
    public Memory getIterator(Environment env, Memory... args);
}
