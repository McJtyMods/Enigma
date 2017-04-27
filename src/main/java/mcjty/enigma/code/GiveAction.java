package mcjty.enigma.code;

import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;

public class GiveAction extends Action {
    private final Expression<EnigmaFunctionContext> item;
    private final Expression<EnigmaFunctionContext> tag;

    public GiveAction(Expression<EnigmaFunctionContext> item, Expression<EnigmaFunctionContext> tag) {
        this.item = item;
        this.tag = tag;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "Give: " + item + " (" + tag + ")");

    }

    @Override
    public void execute(EnigmaFunctionContext context) {
        checkPlayer(context);
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(ObjectTools.asStringSafe(this.item.eval(context))));
        if (item != null) {
            context.getPlayer().inventory.addItemStackToInventory(new ItemStack(item));
            context.getPlayer().openContainer.detectAndSendChanges();
        }
        // @todo error reporting
    }

}
