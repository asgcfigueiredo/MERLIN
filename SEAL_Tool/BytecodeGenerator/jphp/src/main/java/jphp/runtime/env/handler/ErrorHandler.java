package jphp.runtime.env.handler;

import jphp.runtime.env.message.SystemMessage;
import jphp.runtime.exceptions.support.ErrorException;
import jphp.runtime.exceptions.support.ErrorType;
import jphp.runtime.Memory;
import jphp.runtime.env.DieException;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.invoke.Invoker;
import jphp.runtime.lang.BaseException;
import jphp.runtime.memory.ArrayMemory;
import jphp.runtime.memory.LongMemory;
import jphp.runtime.memory.StringMemory;

public class ErrorHandler {
    public final Invoker invoker;
    public final Memory invokerMemory;
    public final int errorHandlerFlags;

    public ErrorHandler(Invoker invoker, Memory invokerMemory, int errorHandlerFlags) {
        this.invoker = invoker;
        this.invokerMemory = invokerMemory;
        this.errorHandlerFlags = errorHandlerFlags;
    }

    public boolean onError(Environment env, SystemMessage message) {
        if (ErrorType.check(errorHandlerFlags, message.getType())){
            TraceInfo trace = message.getTrace().trace;
            int argCount = invoker.getArgumentCount();
            if (argCount < 4) argCount = 4;
            else if (argCount > 5) argCount = 5;

            Memory[] args = new Memory[argCount];
            args[0] = LongMemory.valueOf(message.getType().value);
            args[1] = new StringMemory(message.getMessage());
            args[2] = new StringMemory(trace.getFileName());
            args[3] = LongMemory.valueOf(trace.getStartLine() + 1);
            if (argCount > 4)
                args[4] = new ArrayMemory(false, message.getTrace().args);

            try {
                invoker.setTrace(null);
                return (invoker.call(args).toValue() != Memory.FALSE);
            } catch (ErrorException | DieException | BaseException e){
                throw e;
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }
        return false;
    }
}
