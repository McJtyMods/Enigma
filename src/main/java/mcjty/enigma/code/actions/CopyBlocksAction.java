package mcjty.enigma.code.actions;

import mcjty.enigma.code.Action;
import mcjty.enigma.code.EnigmaFunctionContext;
import mcjty.enigma.code.ExecutionException;
import mcjty.enigma.parser.Expression;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import mcjty.enigma.varia.AreaTools;
import mcjty.enigma.varia.IAreaIterator;
import mcjty.enigma.varia.IPositional;
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

        IAreaIterator iterator = AreaTools.getAreaIterator(progress, this.area.eval(context));
        IPositional destination = AreaTools.getPositional(progress, this.destination.eval(context));

        World wsrc = iterator.getWorld();
        WorldServer wdest = destination.getWorld();
        int dx = destination.getPos().getX() - iterator.getBottomLeft().getX();
        int dy = destination.getPos().getY() - iterator.getBottomLeft().getY();
        int dz = destination.getPos().getZ() - iterator.getBottomLeft().getZ();
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
