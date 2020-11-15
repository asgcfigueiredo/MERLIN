package jphp.runtime.lang.spl;

import jphp.runtime.Memory;
import jphp.runtime.common.HintType;
import jphp.runtime.env.Environment;
import jphp.runtime.lang.IObject;

import static jphp.runtime.annotation.Reflection.*;

@Name("ArrayAccess")
public interface ArrayAccess extends IObject {

    @Signature(value = @Arg("offset"), result = @Arg(type = HintType.BOOLEAN))
    public Memory offsetExists(Environment env, Memory... args);

    @Signature(@Arg("offset"))
    public Memory offsetGet(Environment env, Memory... args);

    @Signature({@Arg("offset"), @Arg("value")})
    public Memory offsetSet(Environment env, Memory... args);

    @Signature(@Arg("offset"))
    public Memory offsetUnset(Environment env, Memory... args);

}
