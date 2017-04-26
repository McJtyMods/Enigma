package mcjty.enigma.parser;

public class StringPointer {

    private final String str;
    private int index;

    public StringPointer(String str) {
        this.str = str;
        index = -1;
    }

    public String getStr() {
        return str;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int index() {
        return index;
    }

    public void inc() {
        index++;
    }

    public void dec() {
        index--;
    }

    public char charAt(int pos) {
        return str.charAt(pos);
    }

    public char current() {
        return str.charAt(index);
    }

    public boolean hasMore() {
        return index < str.length();
    }

    public String substring(int i, int j) {
        return str.substring(i, j);
    }
}
