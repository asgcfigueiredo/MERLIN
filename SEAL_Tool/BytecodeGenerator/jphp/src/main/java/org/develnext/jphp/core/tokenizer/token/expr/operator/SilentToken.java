package org.develnext.jphp.core.tokenizer.token.expr.operator;

import jphp.runtime.common.Association;
import jphp.runtime.Memory;
import org.develnext.jphp.core.tokenizer.TokenMeta;
import org.develnext.jphp.core.tokenizer.TokenType;
import org.develnext.jphp.core.tokenizer.token.expr.OperatorExprToken;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;

/**
 * Silent langMode token
 * @ callfunc()
 */
public class SilentToken extends OperatorExprToken {
    public SilentToken(TokenMeta meta) {
        super(meta, TokenType.T_J_CUSTOM);
    }

    @Override
    public boolean isBinary() {
        return false;
    }

    @Override
    public int getPriority() {
        return 21;
    }

    @Override
    public Memory calc(Environment env, TraceInfo trace, Memory o1, Memory o2) {
        return o1;
    }

    @Override
    public Association getOnlyAssociation() {
        return Association.RIGHT;
    }
}
