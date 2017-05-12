package mcjty.enigma.parser;

import mcjty.enigma.code.ExecutionException;

public interface ExpressionFunction<T> {
    Object eval(T context, Object... o) throws ExecutionException;
}
