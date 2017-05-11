package mcjty.enigma.code.actions;

import mcjty.enigma.code.Action;
import mcjty.enigma.code.EnigmaFunctionContext;
import mcjty.enigma.code.ExecutionException;
import mcjty.enigma.parser.Expression;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import mcjty.enigma.varia.BlockPosDim;
import mcjty.lib.tools.ItemStackTools;
import mcjty.lib.tools.WorldTools;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import org.apache.commons.lang3.StringUtils;

public class DropAction extends Action {
    private final Expression<EnigmaFunctionContext> item;
    private final Expression<EnigmaFunctionContext> position;

    public DropAction(Expression<EnigmaFunctionContext> item, Expression<EnigmaFunctionContext> position) {
        this.item = item;
        this.position = position;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "Drop: " + item);

    }

    @Override
    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        Progress progress = ProgressHolder.getProgress(context.getWorld());
        Object st = item.eval(context);
        ItemStack stack = progress.getNamedItemStack(st);

        Object pos = position.eval(context);
        BlockPosDim namedPosition = progress.getNamedPosition(pos);
        if (namedPosition == null) {
            throw new ExecutionException("Cannot find named position '" + pos + "'!");
        }

        if (stack != null && ItemStackTools.isValid(stack)) {
            WorldServer world = namedPosition.getWorld();
            BlockPos p = namedPosition.getPos();
            EntityItem entityItem = new EntityItem(world, p.getX(), p.getY(), p.getZ(), stack.copy());
            WorldTools.spawnEntity(world, entityItem);
        } else {
            throw new ExecutionException("Cannot find item '" + st + "'!");
        }
    }

}
