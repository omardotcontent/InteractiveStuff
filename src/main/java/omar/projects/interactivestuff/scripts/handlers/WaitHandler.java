package omar.projects.interactivestuff.scripts.handlers;

import me.abdelaziz.api.StatementHandler;
import me.abdelaziz.ast.Expression;
import me.abdelaziz.ast.Statement;
import me.abdelaziz.parser.Parser;
import omar.projects.interactivestuff.scripts.statements.WaitStatement;

import java.util.List;

public final class WaitHandler implements StatementHandler
{
    @Override
    public Statement parse(Parser parser) {
        final Expression timeExpr = parser.expression();
        parser.consume("do", "Expected 'do' after wait");

        final List<Statement> waitBlock = parser.parseBlock("end");
        parser.consume("end", "Expected 'end'");

        return new WaitStatement(timeExpr, waitBlock);
    }
}
