package mcjty.enigma.parser;

import java.util.HashMap;
import java.util.Map;

public enum MainToken {
    STATE(false, 2),        // state <statename> <name>
    PSTATE(false, 2),       // pstate <statename> <name>
    VAR(false, 2),          // var <varname> <value>
    ON(true, 0),
    POSITION(false, 5),     // position <name>, <x>, <y>, <z>, <dim>
    SCOPE(false, 2),        // scope <id>, <expression>
    PSCOPE(false, 2),       // pscope <id>, <expression>
    CANCEL(false, 0),       // cancel
    IF(false, 1),           // if <expression>
    ELSE(false, 0),         // if <expression>
    MESSAGE(false, 1),      // message <msg>
    LOG(false, 1),          // log <msg>
    GIVE(false, 1),         // give <item>
    ITEMSTACK(false, 1),    // itemstack <name>
    ITEM(false,1),          // item <name>
    AMOUNT(false,1),        // amount <number>
    META(false,1),          // meta <value>
    DESCRIPTION(false,1),   // description <string>
    TAG(false, 2);          // tag <object> <tagname>

    private final boolean hasSecondaryToken;
    private final int parameters;

    private static final Map<String, MainToken> MAP = new HashMap<>();

    static {
        for (MainToken token : values()) {
            MAP.put(token.name().toLowerCase(), token);
        }
    }

    MainToken(boolean hasSecondaryToken, int parameters) {
        this.hasSecondaryToken = hasSecondaryToken;
        this.parameters = parameters;
    }

    public boolean isHasSecondaryToken() {
        return hasSecondaryToken;
    }

    public int getParameters() {
        return parameters;
    }

    public static MainToken getTokenByName(String name) {
        return MAP.get(name.toLowerCase());
    }
}
