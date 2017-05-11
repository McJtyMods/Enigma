package mcjty.enigma.code.actions;

import mcjty.enigma.code.Action;
import mcjty.enigma.code.EnigmaFunctionContext;
import mcjty.enigma.code.ExecutionException;
import mcjty.enigma.fxanim.FxAnimationHandler;
import mcjty.enigma.fxanim.animations.MoveAnimation;
import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import mcjty.enigma.varia.BlockPosDim;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.StringUtils;

public class FxAnimMoveAction extends Action {
    private final Expression<EnigmaFunctionContext> ticks;
    private final Expression<EnigmaFunctionContext> pos1;
    private final Expression<EnigmaFunctionContext> pos2;

    public FxAnimMoveAction(Expression<EnigmaFunctionContext> ticks, Expression<EnigmaFunctionContext> pos1, Expression<EnigmaFunctionContext> pos2) {
        this.ticks = ticks;
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "FxAnimMove:");

    }

    @Override
    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        checkPlayer(context);

        int t = ObjectTools.asIntSafe(ticks.eval(context));

        Progress progress = ProgressHolder.getProgress(context.getWorld());
        Object p1 = pos1.eval(context);
        BlockPosDim namedPosition1 = progress.getNamedPosition(p1);
        if (namedPosition1 == null) {
            throw new ExecutionException("Cannot find named position '" + p1 + "'!");
        }
        Object p2 = pos2.eval(context);
        BlockPosDim namedPosition2 = progress.getNamedPosition(p2);
        if (namedPosition2 == null) {
            throw new ExecutionException("Cannot find named position '" + p2 + "'!");
        }

        BlockPos b1 = namedPosition1.getPos();
        BlockPos b2 = namedPosition2.getPos();
        MoveAnimation animation = new MoveAnimation(
                new Vec3d(b1.getX() + .5, b1.getY() + .5, b1.getZ() + .5),
                new Vec3d(b2.getX() + .5, b2.getY() + .5, b2.getZ() + .5), t);
        FxAnimationHandler.startAnimationServer((EntityPlayerMP)context.getPlayer(), animation);
    }

}
