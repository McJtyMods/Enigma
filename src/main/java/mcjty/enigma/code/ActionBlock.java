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
}
