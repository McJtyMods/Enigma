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
    BLOCK(false, 1, 1),        // block <name>:
    CALL(false, 1, 1),         // call <name>
    FOR(false, 4, 4),          // for <variable> <start> <end> <name>
    POSITION(false, 5, 5),     // position <name> <x> <y> <z> <dim>
    AREA(false, 3, 8),         // area <name> <x1> <y1> <z1> <x2> <y2> <z2> <dim> or <name> <pos1> <pos2>
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
    SETBLOCK(false, 2, 2),     // setblock <position/area> <block>
    COPYBLOCKS(false, 2, 2),   // copyblocks <area> <position>
    MOVEBLOCKS(false, 2, 2),   // moveblocks <area> <position>
    MIMICAREA(false, 2, 2),    // mimicarea <area> <position>
    SETMIMIC(false, 2, 2),     // setmimic <position/area> <block>
    NAME(false, 1, 1),         // name <name>
    AMOUNT(false, 1, 1),       // amount <number>
    SPEED(false, 1, 1),        // speed <number>
    HP(false, 1, 1),           // hp <number>
    DAMAGE(false, 1, 1),       // damage <number>
    ITEM(false, 1, 1),         // item <itemname>
    HELMET(false, 1, 1),       // helmet <itemname>
    CHESTPLATE(false, 1, 1),   // chestplate <itemname>
    LEGGINGS(false, 1, 1),     // leggings <itemname>
    BOOTS(false, 1, 1),        // boots <itemname>
    OFFSET(false, 3, 3),       // offset <x> <y> <z>
    META(false, 1, 1),         // meta <value>
    TELEPORT(false, 1, 1),     // teleport <position>
    DESCRIPTION(false, 1, 1),  // description <string>
    SOUND(false, 2, 2),        // description <position> <sound>
    PARTICLE(false, 2, 2),     // description <position> <particlesys>
    SETTING(true, 0, 0),       // setting <setting> ...
    CREATEPARTICLES(false,1,1),// createparticles <name>
    MOB(false, 1, 1),          // mob <name>
    SPAWN(false, 2, 2),        // spawn <position> <mob>
    KILL(false, 1, 1),         // kill <mob>
    FXANIM(true, 0, 0),        // fxanim <animname> ...
    AGGRESSIVE(false, 1, 1),   // aggressive <boolean>
    DELAY(false, 1, 1),        // delay <time>
    TAG(false, 1, 1),          // tag <tagname>
    INVADDITEM(false, 2, 2),   // invadditem <position> <item>
    INVSETITEM(false, 3, 3);   // invadditem <position> <slot> <item>

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
