package mcjty.enigma.parser;

import javax.annotation.Nullable;

public class EmptyExpressionContext implements ExpressionContext {
    @Nullable
    @Override
    public Expression getVariable(String var) {
        return null;
    }

    @Override
    public boolean isVariable(String var) {
        return false;
    }

    @Nullable
    @Override
    public ExpressionFunction getFunction(String name) {
        return null;
    }

    @Override
    public boolean isFunction(String name) {
        return false;
    }
}
