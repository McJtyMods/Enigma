package mcjty.enigma.code.actions;

import mcjty.enigma.code.Action;
import mcjty.enigma.code.EnigmaFunctionContext;
import mcjty.enigma.code.ExecutionException;
import mcjty.enigma.parser.Expression;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import mcjty.enigma.varia.AreaTools;
import mcjty.enigma.varia.BlockPosDim;
import mcjty.enigma.varia.IAreaIterator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.StringUtils;

public class InvAddItemAction extends Action {
    private final Expression<EnigmaFunctionContext> position;
    private final Expression<EnigmaFunctionContext> itemstack;

    public InvAddItemAction(Expression<EnigmaFunctionContext> position, Expression<EnigmaFunctionContext> itemstack) {
        this.position = position;
        this.itemstack = itemstack;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "InvAddItem: " + position + " with " + itemstack);

    }

    @Override
    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        Progress progress = ProgressHolder.getProgress(context.getWorld());

        Object itemname = itemstack.eval(context);
        ItemStack itemStack = progress.getNamedItemStack(itemname);
        if (itemStack == null) {
            throw new ExecutionException("Cannot find item '" + itemname + "'!");
        }

        Object pos = position.eval(context);
        BlockPosDim namedPosition = progress.getNamedPosition(pos);
        if (namedPosition == null) {
            throw new ExecutionException("Cannot find position '" + pos + "'!");
        }

        World world = namedPosition.getWorld();
        TileEntity te = world.getTileEntity(namedPosition.getPos());
        if (te == null) {
            throw new ExecutionException("Not a tile entity '" + pos + "'!");
        }
        if (!te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
            throw new ExecutionException("Not a tile entity with an inventory '" + pos + "'!");
        }
        IItemHandler handler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if (handler == null) {
            throw new ExecutionException("Not a tile entity with an inventory '" + pos + "'!");
        }
        ItemHandlerHelper.insertItem(handler, itemStack, false);
    }
}
