package mcjty.enigma.code;

import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ExpressionContext;
import mcjty.enigma.parser.ExpressionFunction;
import mcjty.enigma.progress.PlayerProgress;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import mcjty.enigma.varia.InventoryHelper;
import mcjty.lib.tools.InventoryTools;
import mcjty.lib.tools.ItemStackTools;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnigmaExpressionContext implements ExpressionContext<EnigmaFunctionContext> {

    private static final Map<String, ExpressionFunction<EnigmaFunctionContext>> FUNCTIONS = new HashMap<>();

    static {
        FUNCTIONS.put("state", (context, o) -> {
            Progress progress = ProgressHolder.getProgress(context.getWorld());
            return progress.getState(o);
        });
        FUNCTIONS.put("pstate", (context, o) -> {
            Progress progress = ProgressHolder.getProgress(context.getWorld());
            PlayerProgress playerProgress = progress.getPlayerProgress(context.getPlayer().getPersistentID());
            return playerProgress.getState(o);
        });
        FUNCTIONS.put("hasitem", (context, o) -> {
            Progress progress = ProgressHolder.getProgress(context.getWorld());
            ItemStack stack = progress.getNamedItemStack(o);
            if (ItemStackTools.isValid(stack)) {
                List<ItemStack> items = InventoryTools.getMainInventory(context.getPlayer());
                for (ItemStack item : items) {
                    if (ItemStackTools.isValid(item)) {
                        if (InventoryHelper.stackEqualExact(item, stack)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        });
        FUNCTIONS.put("hasitemmain", (context, o) -> {
            Progress progress = ProgressHolder.getProgress(context.getWorld());
            ItemStack stack = progress.getNamedItemStack(o);
            if (ItemStackTools.isValid(stack)) {
                if (InventoryHelper.stackEqualExact(stack, context.getPlayer().getHeldItem(EnumHand.MAIN_HAND))) {
                    return true;
                }
            }
            return false;
        });
        FUNCTIONS.put("hasitemoff", (context, o) -> {
            Progress progress = ProgressHolder.getProgress(context.getWorld());
            ItemStack stack = progress.getNamedItemStack(o);
            if (ItemStackTools.isValid(stack)) {
                if (InventoryHelper.stackEqualExact(stack, context.getPlayer().getHeldItem(EnumHand.OFF_HAND))) {
                    return true;
                }
            }
            return false;
        });

    }

    @Nullable
    @Override
    public Expression<EnigmaFunctionContext> getVariable(String var) {
        return null;
    }

    @Override
    public boolean isVariable(String var) {
        return false;
    }

    @Nullable
    @Override
    public ExpressionFunction<EnigmaFunctionContext> getFunction(String name) {
        return FUNCTIONS.get(name);
    }

    @Override
    public boolean isFunction(String name) {
        return FUNCTIONS.containsKey(name);
    }
}
