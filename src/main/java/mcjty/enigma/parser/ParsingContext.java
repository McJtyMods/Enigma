package mcjty.enigma.parser;

import java.util.List;

public class ParsingContext {
    private final List<TokenizedLine> lines;
    private int currentline;
    private int currentindent;

    public ParsingContext(List<TokenizedLine> lines) {
        this.lines = lines;
        currentline = 0;
        currentindent = 0;
    }

    public boolean hasLines() {
        return currentline < lines.size();
    }

    public TokenizedLine getLine() {
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
