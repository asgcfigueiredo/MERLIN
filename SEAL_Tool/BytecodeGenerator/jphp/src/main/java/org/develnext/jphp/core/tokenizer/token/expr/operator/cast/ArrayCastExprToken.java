package org.develnext.jphp.core.tokenizer.token.expr.operator.cast;

import jphp.runtime.Memory;
import org.develnext.jphp.core.tokenizer.TokenMeta;
import org.develnext.jphp.core.tokenizer.TokenType;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;

public class ArrayCastExprToken extends CastExprToken {
    public ArrayCastExprToken(TokenMeta meta) {
        super(meta, TokenType.T_ARRAY_CAST);
    }

    @Override
    public Class<?> getResultClass() {
        return Memory.class;
    }

    @Override
    public Memory calc(Environment env, TraceInfo trace, Memory o1, Memory o2) {
        return o1.toArray();
    }

    @Override
    public String getCode() {
        return "toArray";
    }
}
