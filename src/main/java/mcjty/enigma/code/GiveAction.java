package mcjty.enigma.code;

import mcjty.enigma.network.EnigmaMessages;
import mcjty.enigma.network.PacketAddMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;

public class GiveAction extends Action {
    private final String item;
    private final String tag;

    public GiveAction(String item, String tag) {
        this.item = item;
        this.tag = tag;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "Give: " + item + " (" + tag + ")");

    }

    @Override
    public void execute(World world, EntityPlayer player) {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(this.item));
        player.inventory.addItemStackToInventory(new ItemStack(item));
        player.openContainer.detectAndSendChanges();
    }
}
