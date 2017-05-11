package mcjty.enigma.code.actions;

import mcjty.enigma.code.Action;
import mcjty.enigma.code.EnigmaFunctionContext;
import mcjty.enigma.code.ExecutionException;
import mcjty.enigma.parser.Expression;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import mcjty.enigma.varia.BlockPosDim;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.common.DimensionManager;
import org.apache.commons.lang3.StringUtils;

public class SetBlockAction extends Action {
    private final Expression<EnigmaFunctionContext> position;
    private final Expression<EnigmaFunctionContext> block;

    public SetBlockAction(Expression<EnigmaFunctionContext> position, Expression<EnigmaFunctionContext> block) {
        this.position = position;
        this.block = block;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "SetBlock: " + position + " to " + block);

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
        DimensionManager.getWorld(namedPosition.getDimension()).setBlockState(namedPosition.getPos(), namedBlock, 3);
    }
}
