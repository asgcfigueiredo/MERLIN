package jphp.runtime.lang.support;


import jphp.runtime.Memory;
import jphp.runtime.annotation.Reflection;

@Reflection.Ignore
public interface IStaticVariables {
    Memory getStatic(String name);
    Memory getOrCreateStatic(String name);
}
