package jphp.runtime.env.message;

import jphp.runtime.exceptions.support.ErrorType;
import jphp.runtime.common.Messages;
import jphp.runtime.env.CallStackItem;
import jphp.runtime.env.Environment;

public class NoticeMessage extends SystemMessage {

    public NoticeMessage(CallStackItem trace, Messages.Item message, Object... args) {
        super(trace, message, args);
    }

    public NoticeMessage(Environment environment, Messages.Item message, Object... args) {
        super(environment, message, args);
    }

    @Override
    public ErrorType getType() {
        return ErrorType.E_NOTICE;
    }
}
