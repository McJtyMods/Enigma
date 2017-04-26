package mcjty.enigma.parser;

public interface Expression<T> {

    Object eval(T world);

}
