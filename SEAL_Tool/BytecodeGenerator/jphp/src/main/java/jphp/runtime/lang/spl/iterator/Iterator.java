package jphp.runtime.lang.spl.iterator;

import jphp.runtime.Memory;
import jphp.runtime.env.Environment;
import jphp.runtime.lang.spl.Traversable;

import static jphp.runtime.annotation.Reflection.Name;
import static jphp.runtime.annotation.Reflection.Signature;

@Name("Iterator")
public interface Iterator extends Traversable {
    @Signature
    public Memory current(Environment env, Memory... args);

    @Signature
    public Memory key(Environment env, Memory... args);

    @Signature
    public Memory next(Environment env, Memory... args);

    @Signature
    public Memory rewind(Environment env, Memory... args);

    @Signature
    public Memory valid(Environment env, Memory... args);
}
