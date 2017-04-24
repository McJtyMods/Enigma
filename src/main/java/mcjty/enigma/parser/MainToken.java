package mcjty.enigma.parser;

import java.util.HashMap;
import java.util.Map;

public enum MainToken {
    STATE,
    VAR,
    ON(true),
    WHILE,
    MESSAGE,
    TAG;

    private final boolean hasSecondaryToken;

    private static final Map<String, MainToken> MAP = new HashMap<>();

    static {
        for (MainToken token : values()) {
            MAP.put(token.name().toLowerCase(), token);
        }
    }

    MainToken(boolean hasSecondaryToken) {
        this.hasSecondaryToken = hasSecondaryToken;
    }

    MainToken() {
        this(false);
    }

    public boolean isHasSecondaryToken() {
        return hasSecondaryToken;
    }

    public static MainToken getTokenByName(String name) {
        return MAP.get(name.toLowerCase());
    }
}
