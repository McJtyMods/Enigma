package mcjty.enigma.parser;

import javax.annotation.Nullable;

public interface ExpressionContext<T> {

    @Nullable
    public Expression<T> getVariable(String var);

    public boolean isVariable(String var);

    @Nullable
    public ExpressionFunction<T> getFunction(String name);

    public boolean isFunction(String name);
}
