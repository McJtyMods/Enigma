package mcjty.enigma.network;

import io.netty.buffer.ByteBuf;
import mcjty.enigma.fxanim.FxAnimation;
import mcjty.enigma.fxanim.FxAnimationHandler;
import mcjty.enigma.varia.NetworkTools;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketStartFxAnimation implements IMessage {

    private String animationID;
    private FxAnimation animation;

    @Override
    public void fromBytes(ByteBuf buf) {
        animationID = NetworkTools.readString(buf);
        animation = FxAnimationHandler.createAnimation(animationID, buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NetworkTools.writeString(buf, animationID);
        animation.writeToBuf(buf);
    }

    public PacketStartFxAnimation() {
    }

    public PacketStartFxAnimation(FxAnimation animation) {
        this.animationID = animation.getID();
        this.animation = animation;
    }

    public static class Handler implements IMessageHandler<PacketStartFxAnimation, IMessage> {
        @Override
        public IMessage onMessage(PacketStartFxAnimation message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketStartFxAnimation message, MessageContext ctx) {
            FxAnimationHandler.startAnimationClient(message.animation);
        }
    }
}
