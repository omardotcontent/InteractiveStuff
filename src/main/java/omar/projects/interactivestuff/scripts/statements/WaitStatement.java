package omar.projects.interactivestuff.scripts.statements;

import me.abdelaziz.ast.Expression;
import me.abdelaziz.ast.Statement;
import me.abdelaziz.runtime.Environment;
import omar.projects.interactivestuff.handlers.BackgroundLoopHandler;
import omar.projects.interactivestuff.scripts.ScriptExecutionContext;

import java.util.List;

public final class WaitStatement implements Statement {
    private final Expression timeExpr;
    private final List<Statement> body;

    public WaitStatement(Expression timeExpr, List<Statement> body) {
        this.timeExpr = timeExpr;
        this.body = List.copyOf(body);
    }

    @Override
    public void execute(Environment environment) {
        int ticks = timeExpr.evaluate(environment).asInt();
        BackgroundLoopHandler.getInstance().waitTicks(ticks, ScriptExecutionContext.deferred(() -> {
            for (Statement statement : body) statement.execute(environment);
        }));
    }
}
