package org.develnext.jphp.core.tokenizer.token.expr.operator.cast;

import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.memory.LongMemory;
import jphp.runtime.Memory;
import org.develnext.jphp.core.tokenizer.TokenMeta;
import org.develnext.jphp.core.tokenizer.TokenType;

public class IntCastExprToken extends CastExprToken {
    public IntCastExprToken(TokenMeta meta) {
        super(meta, TokenType.T_INT_CAST);
    }

    @Override
    public Class<?> getResultClass() {
        return Long.TYPE;
    }

    @Override
    public Memory calc(Environment env, TraceInfo trace, Memory o1, Memory o2) {
        return LongMemory.valueOf(o1.toLong());
    }

    @Override
    public String getCode() {
        return "toLong";
    }
}
