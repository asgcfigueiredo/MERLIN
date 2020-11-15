package jphp.runtime.lang.spl;

import jphp.runtime.Memory;
import jphp.runtime.annotation.Reflection;
import jphp.runtime.env.Environment;

@Reflection.Name("Serializable")
public interface Serializable extends Traversable {

    @Reflection.Signature
    public Memory serialize(Environment env, Memory... args);

    @Reflection.Signature({@Reflection.Arg("serialized")})
    public Memory unserialize(Environment env, Memory... args);
}
