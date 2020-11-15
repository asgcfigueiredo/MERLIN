package org.develnext.jphp.core.tokenizer.token.expr.operator;

import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;
import jphp.runtime.memory.KeyValueMemory;
import jphp.runtime.Memory;
import org.develnext.jphp.core.tokenizer.TokenType;
import org.develnext.jphp.core.tokenizer.TokenMeta;
import org.develnext.jphp.core.tokenizer.token.expr.OperatorExprToken;

public class KeyValueExprToken extends OperatorExprToken {
    public KeyValueExprToken(TokenMeta meta) {
        super(meta, TokenType.T_DOUBLE_ARROW);
    }

    @Override
    public int getPriority() {
        return 200;
    }

    @Override
    public String getCode() {
        return "newKeyValue";
    }

    @Override
    public boolean isMutableArguments() {
        return true;
    }

    @Override
    public Memory calc(Environment env, TraceInfo trace, Memory o1, Memory o2) {
        return new KeyValueMemory(o1, o2);
    }
}
