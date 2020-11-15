package jphp.runtime.ext.core.classes;

import jphp.runtime.Memory;
import jphp.runtime.annotation.Reflection;
import jphp.runtime.annotation.Reflection.Arg;
import jphp.runtime.annotation.Reflection.Signature;
import jphp.runtime.common.HintType;
import jphp.runtime.env.Environment;
import jphp.runtime.lang.BaseObject;
import jphp.runtime.lang.ForeachIterator;
import jphp.runtime.lang.spl.ArrayAccess;
import jphp.runtime.lang.spl.Countable;
import jphp.runtime.lang.spl.Traversable;
import jphp.runtime.memory.ArrayMemory;
import jphp.runtime.memory.LongMemory;
import jphp.runtime.memory.StringMemory;
import jphp.runtime.reflection.ClassEntity;

import java.util.LinkedHashMap;
import java.util.Map;

@Reflection.Name("EnvironmentVariables")
@Reflection.Namespace("php\\lang")
public class WrapEnvironmentVariables extends BaseObject implements Traversable, ArrayAccess, Countable {
    private Map<String, String> userEnv;

    public WrapEnvironmentVariables(Environment env) {
        super(env);
        init(env);
    }

    public WrapEnvironmentVariables(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }

    private void init(Environment env) {
        userEnv = env.getUserValue("env", Map.class);

        if (userEnv == null) {
            env.setUserValue("env", userEnv = new LinkedHashMap<>());
        }

        userEnv.putAll(System.getenv());
    }

    @Signature
    public void __construct(Environment env) {
        init(env);
    }

    @Signature
    public Memory __debugInfo() {
        return ArrayMemory.ofStringMap(userEnv).toConstant();
    }

    @Override
    @Signature(value = @Arg("offset"), result = @Arg(type = HintType.BOOLEAN))
    synchronized public Memory offsetExists(Environment env, Memory... args) {
        return userEnv.containsKey(args[0].toString()) ? Memory.TRUE : Memory.FALSE;
    }

    @Override
    @Signature(@Arg("offset"))
    public Memory offsetGet(Environment env, Memory... args) {
        String value = userEnv.get(args[0].toString());
        return value == null ? Memory.NULL : StringMemory.valueOf(value);
    }

    @Override
    @Signature({@Arg("offset"), @Arg("value")})
    synchronized public Memory offsetSet(Environment env, Memory... args) {
        userEnv.put(args[0].toString(), args[1].toString());
        return Memory.NULL;
    }

    @Override
    @Signature(@Arg("offset"))
    synchronized public Memory offsetUnset(Environment env, Memory... args) {
        userEnv.remove(args[0].toString());
        return Memory.NULL;
    }

    @Override
    @Signature
    public Memory count(Environment env, Memory... args) {
        return LongMemory.valueOf(userEnv.size());
    }

    @Override
    public ForeachIterator getNewIterator(Environment env, boolean getReferences, boolean getKeyReferences) {
        return ForeachIterator.of(env, userEnv);
    }

    @Override
    public ForeachIterator getNewIterator(Environment env) {
        return ForeachIterator.of(env, userEnv);
    }
}
