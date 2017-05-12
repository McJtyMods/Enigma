package mcjty.enigma.code.actions;

import mcjty.enigma.blocks.MimicTE;
import mcjty.enigma.blocks.ModBlocks;
import mcjty.enigma.code.Action;
import mcjty.enigma.code.EnigmaFunctionContext;
import mcjty.enigma.code.ExecutionException;
import mcjty.enigma.parser.Expression;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import mcjty.enigma.varia.AreaTools;
import mcjty.enigma.varia.IAreaIterator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;

public class SetMimicAction extends Action {
    private final Expression<EnigmaFunctionContext> positionOrArea;
    private final Expression<EnigmaFunctionContext> block;

    public SetMimicAction(Expression<EnigmaFunctionContext> positionOrArea, Expression<EnigmaFunctionContext> block) {
        this.positionOrArea = positionOrArea;
        this.block = block;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "SetMimic: " + positionOrArea + " to " + block);

    }

    @Override
    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        Progress progress = ProgressHolder.getProgress(context.getWorld());
        Object blockname = block.eval(context);
        IBlockState namedBlock = progress.getNamedBlock(blockname);
        if (namedBlock == null) {
            throw new ExecutionException("Cannot find named block '" + blockname + "'!");
        }

        Object pos = positionOrArea.eval(context);
        IAreaIterator iterator = AreaTools.getAreaIterator(progress, pos);
        World w = iterator.getWorld();
        while (iterator.advance()) {
            w.setBlockState(iterator.current(), ModBlocks.mimic.getDefaultState(), 3);
            TileEntity te = w.getTileEntity(iterator.current());
            if (te instanceof MimicTE) {
                MimicTE mimic = (MimicTE) te;
                mimic.setToMimic(namedBlock);
            }
        }
    }
}
