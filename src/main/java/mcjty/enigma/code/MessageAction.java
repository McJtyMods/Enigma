package mcjty.enigma.code;

import net.minecraft.entity.player.EntityPlayer;
import org.apache.commons.lang3.StringUtils;

public class MessageAction extends Action {
    private final String message;

    public MessageAction(String message) {
        this.message = message;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "Message: " + message);

    }

    @Override
    public void execute(EntityPlayer player) {

    }
}
