package mcjty.enigma.parser;

import mcjty.enigma.code.ExecutionException;

public interface Expression<T> {

    Object eval(T context) throws ExecutionException;

}
