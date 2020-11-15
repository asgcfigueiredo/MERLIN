package jphp.runtime.lang.spl.iterator;

import jphp.runtime.Memory;
import jphp.runtime.annotation.Reflection;
import jphp.runtime.env.Environment;

@Reflection.Name("SeekableIterator")
public interface SeekableIterator extends Iterator {

    @Reflection.Signature({@Reflection.Arg("position")})
    public Memory seek(Environment env, Memory... args);
}
