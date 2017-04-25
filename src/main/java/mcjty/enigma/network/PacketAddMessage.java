package mcjty.enigma.network;

import io.netty.buffer.ByteBuf;
import mcjty.enigma.overlays.TimedMessages;
import mcjty.enigma.varia.NetworkTools;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketAddMessage implements IMessage {

    private String message;
    private int timeout;

    @Override
    public void fromBytes(ByteBuf buf) {
        message = NetworkTools.readStringUTF8(buf);
        timeout = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NetworkTools.writeStringUTF8(buf, message);
        buf.writeInt(timeout);
    }

    public PacketAddMessage() {
    }

    public PacketAddMessage(String message, int timeout) {
        this.message = message;
        this.timeout = timeout;
    }

    public static class Handler implements IMessageHandler<PacketAddMessage, IMessage> {
        @Override
        public IMessage onMessage(PacketAddMessage message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketAddMessage message, MessageContext ctx) {
            TimedMessages.addMessage(message.message, message.timeout);
        }
    }
}
