package org.develnext.jphp.core.compiler.jvm.statement.expr;

import org.develnext.jphp.core.compiler.jvm.statement.ExpressionStmtCompiler;
import org.develnext.jphp.core.tokenizer.token.stmt.ThrowStmtToken;
import jphp.runtime.Memory;
import jphp.runtime.env.Environment;
import jphp.runtime.env.TraceInfo;

public class ThrowCompiler extends BaseStatementCompiler<ThrowStmtToken> {
    public ThrowCompiler(ExpressionStmtCompiler exprCompiler) {
        super(exprCompiler);
    }

    @Override
    public void write(ThrowStmtToken token) {
        expr.writePushEnv();
        expr.writePushTraceInfo(token.getException());
        expr.writeExpression(token.getException(), true, false, true);
        expr.writePopBoxing();
        expr.writeSysDynamicCall(Environment.class, "__throwException", void.class, TraceInfo.class, Memory.class);
    }
}
