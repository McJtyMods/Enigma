package mcjty.enigma.code;

import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;

public class GiveAction extends Action {
    private final Expression item;
    private final Expression tag;

    public GiveAction(Expression item, Expression tag) {
        this.item = item;
        this.tag = tag;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "Give: " + item + " (" + tag + ")");

    }

    @Override
    public void execute(World world, EntityPlayer player) {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(ObjectTools.asStringSafe(this.item.eval(world))));
        if (item != null) {
            player.inventory.addItemStackToInventory(new ItemStack(item));
            player.openContainer.detectAndSendChanges();
        }
        // @todo error reporting
    }
}
