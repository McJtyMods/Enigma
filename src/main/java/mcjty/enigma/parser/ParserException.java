package mcjty.enigma.parser;

public class ParserException extends Exception {

    private final int linenumber;

    public ParserException(String s, int linenumber) {
        super(s);
        this.linenumber = linenumber;
    }

    public int getLinenumber() {
        return linenumber;
    }
}
