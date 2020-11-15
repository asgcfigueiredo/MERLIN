package jphp.runtime.lang.spl;

import jphp.runtime.annotation.Reflection;
import jphp.runtime.env.Environment;
import jphp.runtime.lang.ForeachIterator;
import jphp.runtime.lang.IObject;

@Reflection.NotRuntime
@Reflection.Name("Traversable")
public interface Traversable extends IObject {
    ForeachIterator getNewIterator(Environment env, boolean getReferences, boolean getKeyReferences);
    ForeachIterator getNewIterator(Environment env);
}
