package org.develnext.jphp.core.tokenizer.token.expr.operator;

import jphp.runtime.Memory;
import org.develnext.jphp.core.tokenizer.TokenType;
import org.develnext.jphp.core.tokenizer.TokenMeta;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;

public class BooleanOrExprToken extends LogicOperatorExprToken {
    public BooleanOrExprToken(TokenMeta meta) {
        super(meta, TokenType.T_BOOLEAN_OR);
    }

    @Override
    public int getPriority() {
        return 130;
    }

    @Override
    public Memory calc(Environment env, TraceInfo trace, Memory o1, Memory o2) {
        return o1.toBoolean() || o2.toBoolean() ? Memory.TRUE : Memory.FALSE;
    }
}
