package mcjty.enigma.code.actions;

import mcjty.enigma.code.Action;
import mcjty.enigma.code.EnigmaFunctionContext;
import mcjty.enigma.code.ExecutionException;
import mcjty.enigma.network.EnigmaMessages;
import mcjty.enigma.network.PacketAddMessage;
import mcjty.enigma.network.PacketSettingChat;
import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
import net.minecraft.entity.player.EntityPlayerMP;
import org.apache.commons.lang3.StringUtils;

public class SettingChatAction extends Action {
    private final Expression<EnigmaFunctionContext> chat;

    public SettingChatAction(Expression<EnigmaFunctionContext> chat) {
        this.chat = chat;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "Setting chat: " + chat);

    }

    @Override
    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        checkPlayer(context);
        boolean c = ObjectTools.asBoolSafe(chat.eval(context));
        EnigmaMessages.INSTANCE.sendTo(new PacketSettingChat(c), (EntityPlayerMP) context.getPlayer());
    }
}
