package mcjty.enigma.code;

import mcjty.enigma.parser.Expression;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import mcjty.enigma.varia.InventoryHelper;
import mcjty.lib.tools.InventoryTools;
import mcjty.lib.tools.ItemStackTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class TakeAllAction extends Action {
    private final Expression<EnigmaFunctionContext> item;

    public TakeAllAction(Expression<EnigmaFunctionContext> item) {
        this.item = item;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "Take all: " + item);

    }

    @Override
    public void execute(EnigmaFunctionContext context) {
        checkPlayer(context);

        Progress progress = ProgressHolder.getProgress(context.getWorld());
        ItemStack stack;
        Object st = item.eval(context);
        stack = progress.getNamedItemStack(st);

        if (ItemStackTools.isValid(stack)) {
            EntityPlayer player = context.getPlayer();
            List<ItemStack> inventory = InventoryTools.getMainInventory(player);
            for (int i = 0 ; i < inventory.size() ; i++) {
                ItemStack s = inventory.get(i);
                if (InventoryHelper.stackEqualExact(stack, s)) {
                    player.inventory.setInventorySlotContents(i, ItemStackTools.getEmptyStack());
                }
            }
            player.openContainer.detectAndSendChanges();
        }
    }

}
