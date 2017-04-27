package mcjty.enigma.parser;

public class ParsedExpression<T> {
    private final Expression<T> expression;
    private final boolean isConstant;
    private final String debug;

    public ParsedExpression(Expression<T> expression, boolean isConstant, String debug) {
        this.expression = expression;
        this.isConstant = isConstant;
        this.debug = debug;
    }

    public Expression<T> getExpression() {
        return expression;
    }

    public boolean isConstant() {
        return isConstant;
    }

    public String getDebug() {
        return debug;
    }
}
