package mcjty.enigma.code;

import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import mcjty.enigma.varia.Area;
import mcjty.enigma.varia.BlockPosDim;
import org.apache.commons.lang3.StringUtils;

public class Area2Action extends Action {
    private final Expression<EnigmaFunctionContext> name;
    private final Expression<EnigmaFunctionContext> pos1;
    private final Expression<EnigmaFunctionContext> pos2;

    public Area2Action(Expression<EnigmaFunctionContext> name,
                       Expression<EnigmaFunctionContext> pos1,
                       Expression<EnigmaFunctionContext> pos2) {
        this.name = name;
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "Area: " + name);
    }

    @Override
    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        Progress progress = ProgressHolder.getProgress(context.getWorld());

        Object pos1 = this.pos1.eval(context);
        BlockPosDim namedPosition1 = progress.getNamedPosition(pos1);
        if (namedPosition1 == null) {
            throw new ExecutionException("Cannot find named position '" + pos1 + "'!");
        }
        Object pos2 = this.pos2.eval(context);
        BlockPosDim namedPosition2 = progress.getNamedPosition(pos2);
        if (namedPosition2 == null) {
            throw new ExecutionException("Cannot find named position '" + pos2 + "'!");
        }

        String name = ObjectTools.asStringSafe(this.name.eval(context));
        progress.addNamedArea(name, new Area(namedPosition1.getPos(), namedPosition2.getPos(), namedPosition1.getDimension()));
        ProgressHolder.save(context.getWorld());
    }
}
