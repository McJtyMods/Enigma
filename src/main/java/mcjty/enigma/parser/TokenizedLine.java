package mcjty.enigma.parser;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class TokenizedLine<T> {
    private final int indentation;
    private final int lineNumber;
    @Nonnull private final MainToken mainToken;
    @Nullable private final Token secondaryToken;
    @Nonnull private final List<Expression<T>> parameters;
    private final boolean endsWithColon;

    public TokenizedLine(int indentation, int lineNumber, @Nonnull MainToken mainToken, @Nullable Token secondaryToken, @Nonnull List<Expression<T>> parameters, boolean endsWithColon) {
        this.indentation = indentation;
        this.lineNumber = lineNumber;
        this.mainToken = mainToken;
        this.secondaryToken = secondaryToken;
        this.parameters = parameters;
        this.endsWithColon = endsWithColon;
    }

    public int getIndentation() {
        return indentation;
    }

    public int getLineNumber() {
        return lineNumber;
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
    public List<Expression<T>> getParameters() {
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
            for (Expression parameter : parameters) {
                System.out.print("{" + parameter + "}" + " ");
            }

        }

        System.out.println(endsWithColon ? "     [:]" : "");
    }
}
