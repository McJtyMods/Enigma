package mcjty.enigma.setup;


import mcjty.enigma.blocks.ModBlocks;
import mcjty.enigma.items.ModItems;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientRegistration {

    @SubscribeEvent
    public static void onModelRegister(ModelRegistryEvent event) {
        ModItems.initModels();
        ModBlocks.initModels();
    }
}
