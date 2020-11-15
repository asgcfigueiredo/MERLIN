package org.develnext.jphp.core.tokenizer.token.expr.operator;

import jphp.runtime.Memory;
import org.develnext.jphp.core.tokenizer.TokenType;
import org.develnext.jphp.core.tokenizer.TokenMeta;
import org.develnext.jphp.core.tokenizer.token.expr.OperatorExprToken;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;

public class MinusExprToken extends OperatorExprToken {
    public MinusExprToken(TokenMeta meta) {
        super(meta, TokenType.T_J_MINUS);
    }

    @Override
    public int getPriority() {
        return 60;
    }

    @Override
    public String getCode() {
        return "minus";
    }

    @Override
    public Memory calc(Environment env, TraceInfo trace, Memory o1, Memory o2) {
        return o1.minus(o2);
    }
}
