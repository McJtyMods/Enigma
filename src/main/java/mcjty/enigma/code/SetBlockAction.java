package mcjty.enigma.code;

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
    public void execute(EnigmaFunctionContext context) {
        Progress progress = ProgressHolder.getProgress(context.getWorld());
        BlockPosDim namedPosition = progress.getNamedPosition(position.eval(context));
        // @todo error checking
        IBlockState namedBlock = progress.getNamedBlock(block.eval(context));
        // @todo error checking
        DimensionManager.getWorld(namedPosition.getDimension()).setBlockState(namedPosition.getPos(), namedBlock, 3);
    }
}
