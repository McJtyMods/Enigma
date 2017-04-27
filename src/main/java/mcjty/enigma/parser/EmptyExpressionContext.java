package mcjty.enigma.parser;

import javax.annotation.Nullable;

public class EmptyExpressionContext implements ExpressionContext<Void> {
    @Nullable
    @Override
    public Expression<Void> getVariable(String var) {
        return null;
    }

    @Override
    public boolean isVariable(String var) {
        return false;
    }

    @Nullable
    @Override
    public ExpressionFunction<Void> getFunction(String name) {
        return null;
    }

    @Override
    public boolean isFunction(String name) {
        return false;
    }
}
