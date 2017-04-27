package mcjty.enigma.code;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

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

    public void execute(EnigmaFunctionContext context) {
        for (Action action : actions) {
            action.execute(context);
        }
    }
}
