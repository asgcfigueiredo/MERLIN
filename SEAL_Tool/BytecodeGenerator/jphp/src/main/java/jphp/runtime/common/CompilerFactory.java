package jphp.runtime.common;

import jphp.runtime.env.Context;
import jphp.runtime.env.Environment;

abstract public class CompilerFactory {
    abstract public AbstractCompiler getCompiler(Environment env, Context context) throws Throwable;
}
