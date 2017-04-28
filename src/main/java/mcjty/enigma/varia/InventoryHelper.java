package mcjty.enigma.varia;

import mcjty.lib.tools.ItemStackTools;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class InventoryHelper {

    public static boolean stackEqualExact(@Nonnull ItemStack stack1, ItemStack stack2) {
        if (ItemStackTools.isEmpty(stack2)) {
            return false;
        }
        return stack1.getItem() == stack2.getItem() && (!stack1.getHasSubtypes() || stack1.getMetadata() == stack2.getMetadata()) && ItemStack.areItemStackTagsEqual(stack1, stack2);
    }

}
