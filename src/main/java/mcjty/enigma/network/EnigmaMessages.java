package mcjty.enigma.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class EnigmaMessages {
    public static SimpleNetworkWrapper INSTANCE;
    private static int ID = 0;

    public static int nextID() {
        return ID++;
    }

    public static void registerMessages(String channelName) {
        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(channelName);

        // Server side
//        net.registerMessage(PacketGetGridStatus.Handler.class, PacketGetGridStatus.class, PacketHandler.nextPacketID(), Side.SERVER);

        // Client side
        INSTANCE.registerMessage(PacketAddMessage.Handler.class, PacketAddMessage.class, nextID(), Side.CLIENT);
        INSTANCE.registerMessage(PacketStartFxAnimation.Handler.class, PacketStartFxAnimation.class, nextID(), Side.CLIENT);
        INSTANCE.registerMessage(PacketSettingChat.Handler.class, PacketSettingChat.class, nextID(), Side.CLIENT);
    }
}
