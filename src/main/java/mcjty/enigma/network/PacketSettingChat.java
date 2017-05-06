package mcjty.enigma.network;

import io.netty.buffer.ByteBuf;
import mcjty.enigma.ForgeClientEventHandlers;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSettingChat implements IMessage {

    private boolean chat;

    @Override
    public void fromBytes(ByteBuf buf) {
        chat = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(chat);
    }

    public PacketSettingChat() {
    }

    public PacketSettingChat(boolean chat) {
        this.chat = chat;
    }

    public static class Handler implements IMessageHandler<PacketSettingChat, IMessage> {
        @Override
        public IMessage onMessage(PacketSettingChat message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketSettingChat message, MessageContext ctx) {
            ForgeClientEventHandlers.cancelChat = !message.chat;
        }
    }
}
