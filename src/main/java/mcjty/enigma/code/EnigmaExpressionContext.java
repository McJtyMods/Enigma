package mcjty.enigma.code;

import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ExpressionContext;
import mcjty.enigma.parser.ExpressionFunction;
import mcjty.enigma.parser.ObjectTools;
import mcjty.enigma.progress.PlayerProgress;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import mcjty.lib.compat.CompatInventory;
import mcjty.lib.tools.InventoryTools;
import mcjty.lib.tools.ItemStackTools;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static mcjty.enigma.varia.StringRegister.STRINGS;

public class EnigmaExpressionContext implements ExpressionContext<EnigmaFunctionContext> {

    private static final Map<String, ExpressionFunction<EnigmaFunctionContext>> FUNCTIONS = new HashMap<>();

    static {
        FUNCTIONS.put("state", (context, o) -> {
            Progress progress = ProgressHolder.getProgress(context.getWorld());
            if (o instanceof Integer) {
                return progress.getState((Integer) o);
            } else {
                return progress.getState(ObjectTools.asStringSafe(o));
            }
        });
        FUNCTIONS.put("pstate", (context, o) -> {
            Progress progress = ProgressHolder.getProgress(context.getWorld());
            PlayerProgress playerProgress = progress.getPlayerProgress(context.getPlayer());
            if (o instanceof Integer) {
                return playerProgress.getState((Integer) o);
            } else {
                return playerProgress.getState(ObjectTools.asStringSafe(o));
            }
        });
        FUNCTIONS.put("hasitem", (context, o) -> {
            Progress progress = ProgressHolder.getProgress(context.getWorld());
            ItemStack stack = ItemStackTools.getEmptyStack();
            if (o instanceof Integer) {
                stack = progress.getNamedItemStack((Integer) o);
            } else {
                stack = progress.getNamedItemStack(ObjectTools.asStringSafe(o));
            }
            if (ItemStackTools.isValid(stack)) {
                List<ItemStack> items = InventoryTools.getMainInventory(context.getPlayer());
                for (ItemStack item : items) {
                    if (ItemStackTools.isValid(item)) {
                        if (ItemStack.areItemStackTagsEqual(item, stack)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        });
        FUNCTIONS.put("hasitemmain", (context, o) -> {
            Progress progress = ProgressHolder.getProgress(context.getWorld());
            ItemStack stack = ItemStackTools.getEmptyStack();
            if (o instanceof Integer) {
                stack = progress.getNamedItemStack((Integer) o);
            } else {
                stack = progress.getNamedItemStack(ObjectTools.asStringSafe(o));
            }
            if (ItemStackTools.isValid(stack)) {
                if (ItemStack.areItemStackTagsEqual(stack, context.getPlayer().getHeldItem(EnumHand.MAIN_HAND))) {
                    return true;
                }
            }
            return false;
        });
        FUNCTIONS.put("hasitemoff", (context, o) -> {
            Progress progress = ProgressHolder.getProgress(context.getWorld());
            ItemStack stack = ItemStackTools.getEmptyStack();
            if (o instanceof Integer) {
                stack = progress.getNamedItemStack((Integer) o);
            } else {
                stack = progress.getNamedItemStack(ObjectTools.asStringSafe(o));
            }
            if (ItemStackTools.isValid(stack)) {
                if (ItemStack.areItemStackTagsEqual(stack, context.getPlayer().getHeldItem(EnumHand.OFF_HAND))) {
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
