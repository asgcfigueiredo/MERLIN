package jphp.runtime.env.handler;

import jphp.runtime.env.CompileScope;
import jphp.runtime.env.Environment;

@FunctionalInterface
public interface ProgramShutdownHandler {
    void onShutdown(CompileScope scope, Environment env);
}
