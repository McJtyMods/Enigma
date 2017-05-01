package mcjty.enigma.code;

import mcjty.enigma.network.EnigmaMessages;
import mcjty.enigma.network.PacketAddMessage;
import net.minecraft.util.text.TextFormatting;

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
        try {
            for (Action action : actions) {
                action.execute(context);
            }
        } catch (IndexOutOfBoundsException e) {
            EnigmaMessages.INSTANCE.sendToAll(new PacketAddMessage(TextFormatting.RED + "Out of bounds exception!", 400));
        } catch (NumberFormatException e) {
            EnigmaMessages.INSTANCE.sendToAll(new PacketAddMessage(TextFormatting.RED + "Number format exception!", 400));
        } catch (ExecutionException e) {
            EnigmaMessages.INSTANCE.sendToAll(new PacketAddMessage(TextFormatting.RED + e.getMessage(), 400));
        } catch(Exception e) {
            EnigmaMessages.INSTANCE.sendToAll(new PacketAddMessage(TextFormatting.RED + "Exception: " + e.getMessage(), 400));
        }
    }
}
