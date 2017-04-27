package mcjty.enigma.parser;

public class ParsedExpression<T> {
    private final Expression<T> expression;
    private final boolean isConstant;

    public ParsedExpression(Expression<T> expression, boolean isConstant) {
        this.expression = expression;
        this.isConstant = isConstant;
    }

    public Expression<T> getExpression() {
        return expression;
    }

    public boolean isConstant() {
        return isConstant;
    }
}
