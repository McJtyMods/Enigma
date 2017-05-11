package mcjty.enigma.code.actions;

import mcjty.enigma.code.Action;
import mcjty.enigma.code.EnigmaFunctionContext;
import mcjty.enigma.code.ExecutionException;
import mcjty.enigma.network.EnigmaMessages;
import mcjty.enigma.network.PacketAddMessage;
import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
import net.minecraft.entity.player.EntityPlayerMP;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class MessageAction extends Action {
    private final Expression<EnigmaFunctionContext> message;
    private final Expression<EnigmaFunctionContext> timeout;

    public MessageAction(Expression<EnigmaFunctionContext> message,  List<Expression<EnigmaFunctionContext>> parameters) {
        this.message = message;
        this.timeout = parameters.size() > 1 ? parameters.get(1) : (context -> 100);
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "Message: " + message);

    }

    @Override
    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        int t = ObjectTools.asIntSafe(timeout.eval(context));
        if (context.hasPlayer()) {
            EnigmaMessages.INSTANCE.sendTo(new PacketAddMessage(ObjectTools.asStringSafe(message.eval(context)), t), (EntityPlayerMP) context.getPlayer());
        } else {
            EnigmaMessages.INSTANCE.sendToAll(new PacketAddMessage(ObjectTools.asStringSafe(message.eval(context)), t));
        }
    }
}
