package mcjty.enigma.code.actions;

import mcjty.enigma.code.Action;
import mcjty.enigma.code.EnigmaFunctionContext;
import mcjty.enigma.code.ExecutionException;
import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import mcjty.enigma.varia.BlockPosDim;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.apache.commons.lang3.StringUtils;

public class InvSetItemAction extends Action {
    private final Expression<EnigmaFunctionContext> position;
    private final Expression<EnigmaFunctionContext> slot;
    private final Expression<EnigmaFunctionContext> itemstack;

    public InvSetItemAction(Expression<EnigmaFunctionContext> position, Expression<EnigmaFunctionContext> slot, Expression<EnigmaFunctionContext> itemstack) {
        this.position = position;
        this.slot = slot;
        this.itemstack = itemstack;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "InvSetItem: " + position + " with " + itemstack + " in slot " + slot);

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

        int slot = ObjectTools.asIntSafe(this.slot.eval(context));

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
        if (slot < 0 || slot >= handler.getSlots()) {
            throw new ExecutionException("Slot out of bounds '" + slot + "'!");
        }
        if (handler instanceof IItemHandlerModifiable) {
            ((IItemHandlerModifiable) handler).setStackInSlot(slot, itemStack);
        } else {
            throw new ExecutionException("Cannot modify inventory '" + pos + "'!");
        }
    }
}
