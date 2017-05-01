package mcjty.enigma.code;

public abstract class Action {
    public void dump(int indent) {
    }

    public abstract void execute(EnigmaFunctionContext context) throws ExecutionException;

    protected void checkPlayer(EnigmaFunctionContext context) throws ExecutionException {
        if (!context.hasPlayer()) {
            throw new ExecutionException("No valid player!");
        }
    }
}
