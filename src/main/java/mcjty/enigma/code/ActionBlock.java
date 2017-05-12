package mcjty.enigma.code;

import java.util.ArrayList;
import java.util.List;

public class ActionBlock {
    private final List<Action> actions = new ArrayList<>();

    public void addAction(Action a) {
        actions.add(a);
    }

    public void dump(int indent) {
        for (Action action : actions) {
            action.dump(indent+4);
        }
    }

    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        try {
            for (Action action : actions) {
                action.execute(context);
            }
        } catch (IndexOutOfBoundsException e) {
            throw new ExecutionException("Index Out of Bounds exception", e);
        } catch (NumberFormatException e) {
            throw new ExecutionException("Number Format exception", e);
        } catch(Exception e) {
            throw new ExecutionException("General exception: " + e.getMessage(), e);
        }
    }
}
