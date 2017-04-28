package mcjty.enigma.code;

import mcjty.enigma.parser.Expression;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import mcjty.lib.tools.ItemStackTools;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.StringUtils;

public class GiveAction extends Action {
    private final Expression<EnigmaFunctionContext> item;

    public GiveAction(Expression<EnigmaFunctionContext> item) {
        this.item = item;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "Give: " + item);

    }

    @Override
    public void execute(EnigmaFunctionContext context) {
        checkPlayer(context);

        Progress progress = ProgressHolder.getProgress(context.getWorld());
        ItemStack stack;
        Object st = item.eval(context);
        if (st instanceof Integer) {
            stack = progress.getNamedItemStack((Integer) st);
        } else {
            stack = progress.getNamedItemStack(st.toString());
        }

        if (ItemStackTools.isValid(stack)) {
            context.getPlayer().inventory.addItemStackToInventory(stack.copy());
            context.getPlayer().openContainer.detectAndSendChanges();
        }
        // @todo error reporting
    }

}
