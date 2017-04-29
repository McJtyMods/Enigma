package mcjty.enigma.parser;

public interface ExpressionFunction<T> {
    Object eval(T context, Object... o);
}
