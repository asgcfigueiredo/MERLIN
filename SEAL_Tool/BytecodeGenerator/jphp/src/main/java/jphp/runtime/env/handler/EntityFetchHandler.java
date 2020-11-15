package jphp.runtime.env.handler;

import jphp.runtime.env.CompileScope;

@FunctionalInterface
public interface EntityFetchHandler {
    void fetch(CompileScope scope, String name, String lowerName);
}
