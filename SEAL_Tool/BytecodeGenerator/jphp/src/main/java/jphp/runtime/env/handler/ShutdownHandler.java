package jphp.runtime.env.handler;

import jphp.runtime.Memory;
import jphp.runtime.invoke.Invoker;

public class ShutdownHandler {
    public final Invoker invoker;
    public final Memory[] args;

    public ShutdownHandler(Invoker invoker, Memory[] args) {
        this.invoker = invoker;
        this.args = args;
    }

    public void call() throws Throwable {
        invoker.call(args);
    }
}
