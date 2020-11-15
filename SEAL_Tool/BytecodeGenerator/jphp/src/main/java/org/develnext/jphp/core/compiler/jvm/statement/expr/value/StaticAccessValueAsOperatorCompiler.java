package org.develnext.jphp.core.compiler.jvm.statement.expr.value;

import org.develnext.jphp.core.compiler.jvm.statement.ExpressionStmtCompiler;
import org.develnext.jphp.core.compiler.jvm.statement.expr.BaseExprCompiler;
import org.develnext.jphp.core.tokenizer.token.expr.operator.StaticAccessOperatorExprToken;
import org.develnext.jphp.core.tokenizer.token.expr.value.*;

public class StaticAccessValueAsOperatorCompiler extends BaseExprCompiler<StaticAccessOperatorExprToken> {
    public StaticAccessValueAsOperatorCompiler(ExpressionStmtCompiler exprCompiler) {
        super(exprCompiler);
    }

    @Override
    public void write(StaticAccessOperatorExprToken token, boolean returnValue) {
        StaticAccessExprToken origin = token.getOrigin();

        BaseExprCompiler<StaticAccessExprToken> compiler = (BaseExprCompiler<StaticAccessExprToken>) expr.getCompiler(StaticAccessExprToken.class);
        compiler.write(origin, returnValue);
    }
}
