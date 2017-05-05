package mcjty.enigma.parser;

import java.util.HashMap;
import java.util.Map;

public enum MainToken {
    STATE(false, 2, 2),        // state <statename> <name>
    PSTATE(false, 2, 2),       // pstate <statename> <name>
    VAR(false, 2, 2),          // var <varname> <value>
    PVAR(false, 2, 2),         // pvar <varname> <value>
    LOCAL(false, 2, 2),        // var <varname> <value>
    RESTORE(false, 1, 1),      // restore <snapshotname>
    ON(true, 0, 0),
    POSITION(false, 5, 5),     // position <name>, <x>, <y>, <z>, <dim>
    LOOKAT(false, 1, 1),       // lookat <position>
    COMMAND(false, 1, 1),      // command <command>
    SCOPE(false, 2, 2),        // scope <id>, <expression>
    PSCOPE(false, 2, 2),       // pscope <id>, <expression>
    CANCEL(false, 0, 0),       // cancel
    IF(false, 1, 1),           // if <expression>
    ELSE(false, 0, 0),         // if <expression>
    MESSAGE(false, 1, 2),      // message <msg> [<time>]
    LOG(false, 1, 1),          // log <msg>
    GIVE(false, 1, 1),         // give <item>
    DROP(false, 2, 2),         // drop <item> <pos>
    TAKE(false, 1, 1),         // take <item>
    TAKEALL(false, 1, 1),      // takeall <item>
    ITEMSTACK(false, 1, 1),    // itemstack <name>
    BLOCKSTATE(false,1, 1),    // blockstate <name>
    SETBLOCK(false, 2, 2),     // setblock <position> <block>
    NAME(false, 1, 1),         // name <name>
    AMOUNT(false, 1, 1),       // amount <number>
    SPEED(false, 1, 1),        // speed <number>
    HP(false, 1, 1),           // hp <number>
    ITEM(false, 1, 1),         // item <itemname>
    OFFSET(false, 3, 3),       // offset <x> <y> <z>
    META(false, 1, 1),         // meta <value>
    TELEPORT(false, 1, 1),     // teleport <position>
    DESCRIPTION(false, 1, 1),  // description <string>
    SOUND(false, 2, 2),        // description <position> <sound>
    PARTICLE(false, 2, 2),     // description <position> <particlesys>
    CREATEPARTICLES(false,1,1),// createparticles <name>
    MOB(false, 1, 1),          // mob <name>
    TAG(false, 2, 2);          // tag <object> <tagname>

    private final boolean hasSecondaryToken;
    private final int minParameters;
    private final int maxParameters;

    private static final Map<String, MainToken> MAP = new HashMap<>();

    static {
        for (MainToken token : values()) {
            MAP.put(token.name().toLowerCase(), token);
        }
    }

    MainToken(boolean hasSecondaryToken, int minParameters, int maxParameters) {
        this.hasSecondaryToken = hasSecondaryToken;
        this.minParameters = minParameters;
        this.maxParameters = maxParameters;
    }

    public boolean isHasSecondaryToken() {
        return hasSecondaryToken;
    }

    public int getMinParameters() {
        return minParameters;
    }

    public int getMaxParameters() {
        return maxParameters;
    }

    public static MainToken getTokenByName(String name) {
        return MAP.get(name.toLowerCase());
    }
}
