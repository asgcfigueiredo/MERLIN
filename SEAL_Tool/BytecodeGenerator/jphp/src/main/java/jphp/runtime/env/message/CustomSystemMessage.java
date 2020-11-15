package jphp.runtime.env.message;

import jphp.runtime.exceptions.support.ErrorType;
import jphp.runtime.common.Messages;
import jphp.runtime.env.CallStackItem;
import jphp.runtime.env.Environment;

public class CustomSystemMessage extends SystemMessage {
    protected final ErrorType type;

    public CustomSystemMessage(ErrorType type, CallStackItem trace, Messages.Item message, Object... args) {
        super(trace, message, args);
        this.type = type;
    }

    public CustomSystemMessage(ErrorType type, Environment environment, Messages.Item message, Object... args) {
        super(environment, message, args);
        this.type = type;
    }

    @Override
    public ErrorType getType() {
        return type;
    }
}
