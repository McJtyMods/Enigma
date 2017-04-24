package mcjty.enigma.parser;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class TokenizedLine {
    private final int indentation;
    @Nonnull private final MainToken mainToken;
    @Nullable private final Token secondaryToken;
    @Nonnull private final List<String> parameters;
    private final boolean endsWithColon;

    public TokenizedLine(int indentation, @Nonnull MainToken mainToken, @Nullable Token secondaryToken, @Nonnull List<String> parameters, boolean endsWithColon) {
        this.indentation = indentation;
        this.mainToken = mainToken;
        this.secondaryToken = secondaryToken;
        this.parameters = parameters;
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

    @Nonnull
    public List<String> getParameters() {
        return parameters;
    }

    public boolean isEndsWithColon() {
        return endsWithColon;
    }

    public void dump() {
        if (mainToken.isHasSecondaryToken()) {
            System.out.print("I:" + indentation + "    " + mainToken + " " + secondaryToken);
        } else {
            System.out.print("I:" + indentation + "    " + mainToken);
        }

        if (!parameters.isEmpty()) {
            System.out.print(": ");
            for (String parameter : parameters) {
                System.out.print(parameter + " ");
            }

        }

        System.out.println(endsWithColon ? "     [:]" : "");
    }
}
