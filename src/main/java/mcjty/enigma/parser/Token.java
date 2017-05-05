package mcjty.enigma.parser;

import java.util.HashMap;
import java.util.Map;

public enum Token {
    OBTAINITEM(1),      // on obtainitem <item>
    OBTAINTAG(1),       // on obtaintag <tagname>
    BREAKBLOCK(1),      // on breakblock <position>
    RIGHTCLICKBLOCK(1), // on rightclickblock <block>
    RIGHTCLICKPOSITION(1), // on rightclickblock <position>
    RIGHTCLICKITEM(1),  // on rightclickitem <item>
    LEFTCLICKBLOCK(1),  // on leftclickblock <block>
    LEFTCLICKPOSITION(1),  // on leftclickblock <position>
    BLOCKAT(2),         // on blockat <position> <blocktype>
    DELAY(1),           // on delay <time>
    REPEAT(1),          // on repeat <time>
    OPEN(1),            // on open <name>
    LOGIN(0),           // on login
    INIT(0),            // on init
    SETUP(0),           // on setup
    ACTIVATE(0),        // on activate
    DEACTIVATE(0),      // on deactivate
    COLOR(9),           // fxanim color <ticks> <startalpha> <startr> <startg> <startb> <endalpha> <endr> <endg> <endb>
    COLORANDBACK(9),    // fxanim colorandback <ticks> <startalpha> <startr> <startg> <startb> <endalpha> <endr> <endg> <endb>
    MOVE(3),            // fxanim move <ticks> <start> <end>
    ROTATE(5);          // fxanim rotate <ticks> <startyaw> <startpitch> <endyaw> <endpitch>

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
