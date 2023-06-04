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

import java.util.ArrayList;
import java.util.List;

public class MoveBlocksAction extends Action {
    private final Expression<EnigmaFunctionContext> area;
    private final Expression<EnigmaFunctionContext> destination;

    public MoveBlocksAction(Expression<EnigmaFunctionContext> area, Expression<EnigmaFunctionContext> destination) {
        this.area = area;
        this.destination = destination;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "MoveBlocks: " + area + " to " + destination);
    }

    private static class Remembered {
        IBlockState state;
        NBTTagCompound tc;

        public Remembered(IBlockState state, NBTTagCompound tc) {
            this.state = state;
            this.tc = tc;
        }
    }

    @Override
    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        Progress progress = ProgressHolder.getProgress(context.getWorld());

        IAreaIterator iterator = AreaTools.getAreaIterator(progress, this.area.eval(context));
        IPositional destination = AreaTools.getPositional(progress, this.destination.eval(context));

        int xdim = iterator.getTopRight().getX() - iterator.getBottomLeft().getX() + 1;
        int ydim = iterator.getTopRight().getY() - iterator.getBottomLeft().getY() + 1;
        int zdim = iterator.getTopRight().getZ() - iterator.getBottomLeft().getZ() + 1;
        List<Remembered> remembered = new ArrayList<>(xdim * ydim * zdim);

        World wsrc = iterator.getWorld();
        while (iterator.advance()) {
            BlockPos current = iterator.current();
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
            remembered.add(new Remembered(blockState, tc));
            wsrc.setBlockToAir(current);
        }

        iterator.restart();
        WorldServer wdest = destination.getWorld();
        int dx = destination.getPos().getX() - iterator.getBottomLeft().getX();
        int dy = destination.getPos().getY() - iterator.getBottomLeft().getY();
        int dz = destination.getPos().getZ() - iterator.getBottomLeft().getZ();
        BlockPos.MutableBlockPos dest = new BlockPos.MutableBlockPos();
        int i = 0;
        while (iterator.advance()) {
            BlockPos current = iterator.current();
            dest.setPos(current.getX() + dx, current.getY() + dy, current.getZ() + dz);
            Remembered r = remembered.get(i);
            i++;
            wdest.setBlockToAir(dest);
            wdest.setBlockState(dest, r.state, 3);
            NBTTagCompound tc = r.tc;
            if (tc != null) {
                tc.setInteger("x", dest.getX());
                tc.setInteger("y", dest.getY());
                tc.setInteger("z", dest.getZ());
                TileEntity tileEntity = TileEntity.create(wdest, tc);
                if (tileEntity != null) {
                    wdest.getChunkFromBlockCoords(dest).addTileEntity(tileEntity);
                    tileEntity.markDirty();
                    wdest.notifyBlockUpdate(dest, r.state, r.state, 3);
                }
            }
        }
    }
}
