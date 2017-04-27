package mcjty.enigma.parser;

import javax.annotation.Nonnull;
import java.util.List;

public class ParsingContext<T> {
    @Nonnull private final List<TokenizedLine<T>> lines;
    private int currentline;
    private int currentindent;

    public ParsingContext(@Nonnull List<TokenizedLine<T>> lines) {
        this.lines = lines;
        currentline = 0;
        currentindent = 0;
    }

    public boolean hasLines() {
        return currentline < lines.size();
    }

    public TokenizedLine<T> getLine() {
        return lines.get(currentline);
    }

    public void nextLine() {
        currentline++;
    }

    public int getCurrentIndent() {
        return currentindent;
    }

    public void setCurrentIndent(int currentindent) {
        this.currentindent = currentindent;
    }
}
