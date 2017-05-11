package mcjty.enigma.code.actions;

import mcjty.enigma.code.Action;
import mcjty.enigma.code.EnigmaFunctionContext;
import mcjty.enigma.code.ExecutionException;
import mcjty.enigma.parser.Expression;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import mcjty.enigma.varia.AreaTools;
import mcjty.enigma.varia.IAreaIterator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;

public class SetBlockAction extends Action {
    private final Expression<EnigmaFunctionContext> positionOrArea;
    private final Expression<EnigmaFunctionContext> block;

    public SetBlockAction(Expression<EnigmaFunctionContext> positionOrArea, Expression<EnigmaFunctionContext> block) {
        this.positionOrArea = positionOrArea;
        this.block = block;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "SetBlock: " + positionOrArea + " to " + block);

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
            w.setBlockState(iterator.current(), namedBlock, 3);
        }
    }
}
