package jphp.runtime.lang.support;

import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.lang.IObject;

import static jphp.runtime.annotation.Reflection.Ignore;

@Ignore
public interface ICloneableObject<T extends IObject> {
    T __clone(Environment env, TraceInfo trace);
}
