package omar.projects.interactivestuff.scripts.statements;

import me.abdelaziz.ast.Expression;
import me.abdelaziz.ast.Statement;
import me.abdelaziz.runtime.Environment;
import omar.projects.interactivestuff.handlers.BackgroundLoopHandler;

import java.util.List;

public final class WaitStatement implements Statement {

    private final Expression timeExpr;
    private final List<Statement> body;

    public WaitStatement(final Expression timeExpr, final List<Statement> body) {
        this.timeExpr = timeExpr;
        this.body = body;
    }

    @Override
    public void execute(final Environment environment) {
        final int time = timeExpr.evaluate(environment).asInt();
        BackgroundLoopHandler.getInstance().waitTicks("wait_" + time, time, () -> {
            for (final Statement statement : body)
                statement.execute(environment);
        });
    }
}