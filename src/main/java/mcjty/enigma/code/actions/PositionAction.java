package mcjty.enigma.code.actions;

import mcjty.enigma.code.Action;
import mcjty.enigma.code.EnigmaFunctionContext;
import mcjty.enigma.code.ExecutionException;
import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import mcjty.enigma.varia.BlockPosDim;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.StringUtils;

public class PositionAction extends Action {
    private final Expression<EnigmaFunctionContext> name;
    private final Expression<EnigmaFunctionContext> x;
    private final Expression<EnigmaFunctionContext> y;
    private final Expression<EnigmaFunctionContext> z;
    private final Expression<EnigmaFunctionContext> dimension;

    public PositionAction(Expression<EnigmaFunctionContext> name, Expression<EnigmaFunctionContext> x, Expression<EnigmaFunctionContext> y, Expression<EnigmaFunctionContext> z, Expression<EnigmaFunctionContext> dimension) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimension = dimension;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "Position: " + name + " at " + x + "," + y + "," + z);
    }

    @Override
    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        Progress progress = ProgressHolder.getProgress(context.getWorld());
        BlockPos p = new BlockPos(ObjectTools.asIntSafe(x.eval(context)), ObjectTools.asIntSafe(y.eval(context)), ObjectTools.asIntSafe(z.eval(context)));
        String name = ObjectTools.asStringSafe(this.name.eval(context));
        int dim = ObjectTools.asIntSafe(dimension.eval(context));
        progress.addNamedPosition(name, new BlockPosDim(p, dim));
        ProgressHolder.save(context.getWorld());
    }
}
