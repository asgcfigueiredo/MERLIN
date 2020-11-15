package org.develnext.jphp.core.tokenizer.token.expr.operator;

import jphp.runtime.Memory;
import org.develnext.jphp.core.tokenizer.TokenType;
import org.develnext.jphp.core.tokenizer.TokenMeta;
import org.develnext.jphp.core.tokenizer.token.expr.OperatorExprToken;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;

public class SmallerOrEqualToken extends OperatorExprToken {
    public SmallerOrEqualToken(TokenMeta meta) {
        super(meta, TokenType.T_IS_SMALLER_OR_EQUAL);
    }

    @Override
    public int getPriority() {
        return 70;
    }

    @Override
    public String getCode() {
        return "smallerEq";
    }

    @Override
    public Class<?> getResultClass() {
        return Boolean.TYPE;
    }

    @Override
    public Memory calc(Environment env, TraceInfo trace, Memory o1, Memory o2) {
        return o1.smallerEq(o2) ? Memory.TRUE : Memory.FALSE;
    }
}
