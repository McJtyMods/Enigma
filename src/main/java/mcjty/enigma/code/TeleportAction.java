package mcjty.enigma.code;

import mcjty.enigma.parser.Expression;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import mcjty.enigma.varia.BlockPosDim;
import mcjty.enigma.varia.TeleportationTools;
import org.apache.commons.lang3.StringUtils;

public class TeleportAction extends Action {
    private final Expression<EnigmaFunctionContext> position;

    public TeleportAction(Expression<EnigmaFunctionContext> position) {
        this.position = position;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "Teleport: " + position);
    }

    @Override
    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        Progress progress = ProgressHolder.getProgress(context.getWorld());
        Object pos = position.eval(context);
        BlockPosDim namedPosition = progress.getNamedPosition(pos);
        if (namedPosition == null) {
            throw new ExecutionException("Cannot find named position '" + pos + "'!");
        }
        TeleportationTools.teleportToDimension(context.getPlayer(), namedPosition.getDimension(), namedPosition.getPos().getX(), namedPosition.getPos().getY(), namedPosition.getPos().getZ());
    }
}
