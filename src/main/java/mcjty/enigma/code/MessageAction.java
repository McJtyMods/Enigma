package mcjty.enigma.code;

import mcjty.enigma.network.EnigmaMessages;
import mcjty.enigma.network.PacketAddMessage;
import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;

public class MessageAction extends Action {
    private final Expression message;
    private final int timeout;

    public MessageAction(Expression message) {
        this(message, 100);
    }

    public MessageAction(Expression message, int timeout) {
        this.message = message;
        this.timeout = timeout;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "Message: " + message);

    }

    @Override
    public void execute(World world, EntityPlayer player) {
        EnigmaMessages.INSTANCE.sendTo(new PacketAddMessage(ObjectTools.asStringSafe(message.eval(world)), timeout), (EntityPlayerMP) player);
    }
}
