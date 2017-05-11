package mcjty.enigma.code.actions;

import mcjty.enigma.code.Action;
import mcjty.enigma.code.EnigmaFunctionContext;
import mcjty.enigma.code.ExecutionException;
import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import mcjty.enigma.varia.Area;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.StringUtils;

public class AreaAction extends Action {
    private final Expression<EnigmaFunctionContext> name;
    private final Expression<EnigmaFunctionContext> x1;
    private final Expression<EnigmaFunctionContext> y1;
    private final Expression<EnigmaFunctionContext> z1;
    private final Expression<EnigmaFunctionContext> x2;
    private final Expression<EnigmaFunctionContext> y2;
    private final Expression<EnigmaFunctionContext> z2;
    private final Expression<EnigmaFunctionContext> dimension;

    public AreaAction(Expression<EnigmaFunctionContext> name,
                      Expression<EnigmaFunctionContext> x1, Expression<EnigmaFunctionContext> y1, Expression<EnigmaFunctionContext> z1,
                      Expression<EnigmaFunctionContext> x2, Expression<EnigmaFunctionContext> y2, Expression<EnigmaFunctionContext> z2,
                      Expression<EnigmaFunctionContext> dimension) {
        this.name = name;
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
        this.dimension = dimension;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "Area: " + name);
    }

    @Override
    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        Progress progress = ProgressHolder.getProgress(context.getWorld());
        BlockPos p1 = new BlockPos(ObjectTools.asIntSafe(x1.eval(context)), ObjectTools.asIntSafe(y1.eval(context)), ObjectTools.asIntSafe(z1.eval(context)));
        BlockPos p2 = new BlockPos(ObjectTools.asIntSafe(x2.eval(context)), ObjectTools.asIntSafe(y2.eval(context)), ObjectTools.asIntSafe(z2.eval(context)));
        String name = ObjectTools.asStringSafe(this.name.eval(context));
        int dim = ObjectTools.asIntSafe(dimension.eval(context));
        progress.addNamedArea(name, new Area(p1, p2, dim));
        ProgressHolder.save(context.getWorld());
    }
}
