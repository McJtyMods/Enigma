package mcjty.enigma.parser;

import java.util.HashMap;
import java.util.Map;

public enum Token {
    OBTAINITEM,
    OBTAINTAG,
    BLOCKAT,
    DELAY,
    OPEN,
    START;

    private static final Map<String, Token> MAP = new HashMap<>();

    static {
        for (Token token : values()) {
            MAP.put(token.name().toLowerCase(), token);
        }
    }

    public static Token getTokenByName(String name) {
        return MAP.get(name.toLowerCase());
    }

}
