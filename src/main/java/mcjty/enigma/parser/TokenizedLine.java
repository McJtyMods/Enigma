package mcjty.enigma.parser;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TokenizedLine {
    private final int indentation;
    @Nonnull private final MainToken mainToken;
    @Nullable private final Token secondaryToken;
    private final String[] tokens;
    private final boolean endsWithColon;

    public TokenizedLine(int indentation, @Nonnull MainToken mainToken, @Nullable Token secondaryToken, String[] tokens, boolean endsWithColon) {
        this.indentation = indentation;
        this.mainToken = mainToken;
        this.secondaryToken = secondaryToken;
        this.tokens = tokens;
        this.endsWithColon = endsWithColon;
    }

    public int getIndentation() {
        return indentation;
    }

    @Nonnull
    public MainToken getMainToken() {
        return mainToken;
    }

    @Nullable
    public Token getSecondaryToken() {
        return secondaryToken;
    }

    public String[] getTokens() {
        return tokens;
    }

    public boolean isEndsWithColon() {
        return endsWithColon;
    }

    public void dump() {
        if (mainToken.isHasSecondaryToken()) {
            System.out.println("I:" + indentation + "    " + mainToken + " " + secondaryToken + (endsWithColon ? "     [:]" : ""));
        } else {
            System.out.println("I:" + indentation + "    " + mainToken + (endsWithColon ? "     [:]" : ""));
        }
    }
}
