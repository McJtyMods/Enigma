package mcjty.enigma.parser;

import java.util.HashMap;
import java.util.Map;

public enum Token {
    OBTAINITEM(1),      // on obtainitem <item>
    OBTAINTAG(1),       // on obtaintag <tagname>
    BLOCKAT(3),         // on blockat <blockname> = <blocktype>
    DELAY(1),           // on delay <time>
    OPEN(1),            // on open <name>
    START(0);           // on start

    private static final Map<String, Token> MAP = new HashMap<>();
    private final int parameters;

    static {
        for (Token token : values()) {
            MAP.put(token.name().toLowerCase(), token);
        }
    }

    Token(int parameters) {
        this.parameters = parameters;
    }

    public int getParameters() {
        return parameters;
    }

    public static Token getTokenByName(String name) {
        return MAP.get(name.toLowerCase());
    }

}
