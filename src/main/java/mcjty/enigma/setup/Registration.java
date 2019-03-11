package mcjty.enigma.setup;


import mcjty.enigma.Enigma;
import mcjty.enigma.blocks.MimicTE;
import mcjty.enigma.blocks.ModBlocks;
import mcjty.enigma.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber
public class Registration {

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(ModBlocks.mimic);
        GameRegistry.registerTileEntity(MimicTE.class, Enigma.MODID + "_mimic");

    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(ModItems.key);
        event.getRegistry().register(ModItems.coin);
        event.getRegistry().register(new ItemBlock(ModBlocks.mimic).setRegistryName(ModBlocks.mimic.getRegistryName()));
    }

}
