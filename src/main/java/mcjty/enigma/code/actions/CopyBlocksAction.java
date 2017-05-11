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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.apache.commons.lang3.StringUtils;

public class CopyBlocksAction extends Action {
    private final Expression<EnigmaFunctionContext> area;
    private final Expression<EnigmaFunctionContext> destination;

    public CopyBlocksAction(Expression<EnigmaFunctionContext> area, Expression<EnigmaFunctionContext> destination) {
        this.area = area;
        this.destination = destination;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "CopyBlocks: " + area + " to " + destination);

    }

    @Override
    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        Progress progress = ProgressHolder.getProgress(context.getWorld());

        Object area = this.area.eval(context);
        IAreaIterator iterator = AreaTools.getAreaIterator(progress, area);

        Object destination = this.destination.eval(context);
        BlockPosDim namedPosition = progress.getNamedPosition(destination);
        if (namedPosition == null) {
            throw new ExecutionException("Cannot find named position '" + destination + "'!");
        }

        World wsrc = iterator.getWorld();
        WorldServer wdest = namedPosition.getWorld();
        int dx = namedPosition.getPos().getX() - iterator.getCorner().getX();
        int dy = namedPosition.getPos().getY() - iterator.getCorner().getY();
        int dz = namedPosition.getPos().getZ() - iterator.getCorner().getZ();
        BlockPos.MutableBlockPos dest = new BlockPos.MutableBlockPos();
        while (iterator.advance()) {
            BlockPos current = iterator.current();
            dest.setPos(current.getX() + dx, current.getY() + dy, current.getZ() + dz);
            IBlockState blockState = wsrc.getBlockState(current);

            TileEntity tileEntity = wsrc.getTileEntity(current);
            NBTTagCompound tc = null;
            if (tileEntity != null) {
                tc = new NBTTagCompound();
                tileEntity.writeToNBT(tc);
                tc.removeTag("x");
                tc.removeTag("y");
                tc.removeTag("z");
            }

            wdest.setBlockState(dest, blockState, 3);

            if (tc != null) {
                tc.setInteger("x", dest.getX());
                tc.setInteger("y", dest.getY());
                tc.setInteger("z", dest.getZ());
                tileEntity = TileEntity.create(wdest, tc);
                if (tileEntity != null) {
                    wdest.getChunkFromBlockCoords(dest).addTileEntity(tileEntity);
                    tileEntity.markDirty();
                    wdest.notifyBlockUpdate(dest, blockState, blockState, 3);
                }
            }
        }
    }
}
