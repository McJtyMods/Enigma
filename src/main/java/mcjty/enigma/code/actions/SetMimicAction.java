package mcjty.enigma.code.actions;

import mcjty.enigma.blocks.MimicTE;
import mcjty.enigma.blocks.ModBlocks;
import mcjty.enigma.code.Action;
import mcjty.enigma.code.EnigmaFunctionContext;
import mcjty.enigma.code.ExecutionException;
import mcjty.enigma.parser.Expression;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import mcjty.enigma.varia.BlockPosDim;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import org.apache.commons.lang3.StringUtils;

public class SetMimicAction extends Action {
    private final Expression<EnigmaFunctionContext> position;
    private final Expression<EnigmaFunctionContext> block;

    public SetMimicAction(Expression<EnigmaFunctionContext> position, Expression<EnigmaFunctionContext> block) {
        this.position = position;
        this.block = block;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "SetMimic: " + position + " to " + block);

    }

    @Override
    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        Progress progress = ProgressHolder.getProgress(context.getWorld());
        Object pos = position.eval(context);
        BlockPosDim namedPosition = progress.getNamedPosition(pos);
        if (namedPosition == null) {
            throw new ExecutionException("Cannot find named position '" + pos + "'!");
        }
        Object blockname = block.eval(context);
        IBlockState namedBlock = progress.getNamedBlock(blockname);
        if (namedBlock == null) {
            throw new ExecutionException("Cannot find named block '" + blockname + "'!");
        }
        WorldServer world = DimensionManager.getWorld(namedPosition.getDimension());
        world.setBlockState(namedPosition.getPos(), ModBlocks.mimic.getDefaultState(), 3);
        TileEntity te = world.getTileEntity(namedPosition.getPos());
        if (te instanceof MimicTE) {
            MimicTE mimic = (MimicTE) te;
            mimic.setToMimic(namedBlock);
        }
    }
}
