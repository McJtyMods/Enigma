package mcjty.enigma.parser;

import javax.annotation.Nullable;

public interface ExpressionContext {

    @Nullable
    public Expression getVariable(String var);

    public boolean isVariable(String var);

    @Nullable
    public ExpressionFunction getFunction(String name);

    public boolean isFunction(String name);
}
