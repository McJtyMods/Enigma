package mcjty.enigma.code;

import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ExpressionContext;
import mcjty.enigma.parser.ExpressionFunction;
import mcjty.enigma.progress.PlayerProgress;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import mcjty.enigma.varia.BlockPosDim;
import mcjty.enigma.varia.InventoryHelper;
import mcjty.lib.tools.InventoryTools;
import mcjty.lib.tools.ItemStackTools;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.DimensionManager;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static mcjty.enigma.varia.StringRegister.STRINGS;

public class EnigmaExpressionContext implements ExpressionContext<EnigmaFunctionContext> {

    private final Progress progress;

    public EnigmaExpressionContext(Progress progress) {
        this.progress = progress;
    }

    private static final Map<String, ExpressionFunction<EnigmaFunctionContext>> FUNCTIONS = new HashMap<>();

    static {
        FUNCTIONS.put("state", (context, o) -> {
            Progress progress = ProgressHolder.getProgress(context.getWorld());
            return progress.getState(o[0]);
        });
        FUNCTIONS.put("pstate", (context, o) -> {
            Progress progress = ProgressHolder.getProgress(context.getWorld());
            PlayerProgress playerProgress = progress.getPlayerProgress(context.getPlayer().getPersistentID());
            return playerProgress.getState(o[0]);
        });
        FUNCTIONS.put("isblock", (context, o) -> {
            Progress progress = ProgressHolder.getProgress(context.getWorld());
            BlockPosDim namedPosition = progress.getNamedPosition(o[0]);
            if (namedPosition == null) {
                throw new RuntimeException("Cannot find position " + o[0] + "!");
            }
            IBlockState state = progress.getNamedBlock(o[1]);
            if (state == null) {
                throw new RuntimeException("Cannot find block state " + o[1] + "!");
            }
            return DimensionManager.getWorld(namedPosition.getDimension()).getBlockState(namedPosition.getPos()).equals(state);
        });
        FUNCTIONS.put("hasitem", (context, o) -> {
            Progress progress = ProgressHolder.getProgress(context.getWorld());
            ItemStack stack = progress.getNamedItemStack(o[0]);
            if (ItemStackTools.isValid(stack)) {
                List<ItemStack> items = InventoryTools.getMainInventory(context.getPlayer());
                for (ItemStack item : items) {
                    if (ItemStackTools.isValid(item)) {
                        if (InventoryHelper.stackEqualExact(item, stack)) {
                            return true;
                        }
                    }
                }
            } else {
                throw new RuntimeException("Cannot find item " + o[0] + "!");
            }
            return false;
        });
        FUNCTIONS.put("hasitemmain", (context, o) -> {
            Progress progress = ProgressHolder.getProgress(context.getWorld());
            ItemStack stack = progress.getNamedItemStack(o[0]);
            if (ItemStackTools.isValid(stack)) {
                if (InventoryHelper.stackEqualExact(stack, context.getPlayer().getHeldItem(EnumHand.MAIN_HAND))) {
                    return true;
                }
            } else {
                throw new RuntimeException("Cannot find item " + o[0] + "!");
            }
            return false;
        });
        FUNCTIONS.put("hasitemoff", (context, o) -> {
            Progress progress = ProgressHolder.getProgress(context.getWorld());
            ItemStack stack = progress.getNamedItemStack(o[0]);
            if (ItemStackTools.isValid(stack)) {
                if (InventoryHelper.stackEqualExact(stack, context.getPlayer().getHeldItem(EnumHand.OFF_HAND))) {
                    return true;
                }
            } else {
                throw new RuntimeException("Cannot find item " + o[0] + "!");
            }
            return false;
        });
        FUNCTIONS.put("distance", (context, o) -> {
            Progress progress = ProgressHolder.getProgress(context.getWorld());
            BlockPosDim pos1 = progress.getNamedPosition(o[0]);
            if (pos1 == null) {
                throw new RuntimeException("Cannot find position " + o[0] + "!");
            }
            BlockPos pos2;
            if (o.length > 1) {
                BlockPosDim p2 = progress.getNamedPosition(o[1]);
                if (p2 == null) {
                    throw new RuntimeException("Cannot find position " + o[1] + "!");
                }
                if (pos1.getDimension() != p2.getDimension()) {
                    return -1;
                }
                pos2 = p2.getPos();
            } else {
                EntityPlayer player = context.getPlayer();
                if (player.getEntityWorld().provider.getDimension() != pos1.getDimension()) {
                    return -1;
                }
                pos2 = player.getPosition();
            }
            return BlockPosDim.distance(pos1.getPos(), pos2);
        });

    }

    @Nullable
    @Override
    public Expression<EnigmaFunctionContext> getVariable(String var) {
        int i = STRINGS.get(var);
        return context -> {
            return progress.isNamedVariable(i) ? progress.getNamedVariable(i) : var;
        };
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
