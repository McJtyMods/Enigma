package mcjty.enigma.code.actions;

import mcjty.enigma.code.Action;
import mcjty.enigma.code.EnigmaFunctionContext;
import mcjty.enigma.code.ExecutionException;
import mcjty.enigma.parser.Expression;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import mcjty.enigma.varia.InventoryHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
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
    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        checkPlayer(context);

        Progress progress = ProgressHolder.getProgress(context.getWorld());
        ItemStack stack;
        Object st = item.eval(context);
        stack = progress.getNamedItemStack(st);

        if (stack != null && !stack.isEmpty()) {
            EntityPlayer player = context.getPlayer();
            List<ItemStack> inventory = Collections.unmodifiableList(player.inventory.mainInventory);
            for (int i = 0 ; i < inventory.size() ; i++) {
                ItemStack s = inventory.get(i);
                if (InventoryHelper.stackEqualExact(stack, s)) {
                    player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                }
            }
            player.openContainer.detectAndSendChanges();
        } else {
            throw new ExecutionException("Cannot find item '" + st + "'!");
        }
    }

}
