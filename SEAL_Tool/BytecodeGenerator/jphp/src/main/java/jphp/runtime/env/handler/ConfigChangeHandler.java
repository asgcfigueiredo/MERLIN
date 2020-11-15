package jphp.runtime.env.handler;

import jphp.runtime.Memory;
import jphp.runtime.env.Environment;

@FunctionalInterface
public interface ConfigChangeHandler {
    void onChange(Environment env, Memory value);
}
