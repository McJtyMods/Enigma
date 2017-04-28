package mcjty.enigma.code;

import mcjty.enigma.parser.Expression;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import mcjty.lib.tools.ItemStackTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.StringUtils;

public class TakeAction extends Action {
    private final Expression<EnigmaFunctionContext> item;

    public TakeAction(Expression<EnigmaFunctionContext> item) {
        this.item = item;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "Take: " + item);

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
            // @todo implement this
//            player.inventory.addItemStackToInventory(stack.copy());
//            player.openContainer.detectAndSendChanges();
        }
        // @todo error reporting
    }

}
