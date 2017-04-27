package mcjty.enigma.code;

public abstract class Action {
    public void dump(int indent) {
    }

    public abstract void execute(EnigmaFunctionContext context);

    protected void checkPlayer(EnigmaFunctionContext context) {
        if (!context.hasPlayer()) {
            throw new RuntimeException("message used without valid player!");
        }
    }
}
