package mcjty.enigma.code;

import mcjty.enigma.fxanim.FxAnimationHandler;
import mcjty.enigma.fxanim.animations.MoveAnimation;
import mcjty.enigma.fxanim.animations.MoveBlockAnimation;
import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import mcjty.enigma.varia.BlockPosDim;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.StringUtils;

public class FxAnimMoveBlockAction extends Action {
    private final Expression<EnigmaFunctionContext> ticks;
    private final Expression<EnigmaFunctionContext> pos1;
    private final Expression<EnigmaFunctionContext> dx;
    private final Expression<EnigmaFunctionContext> dy;
    private final Expression<EnigmaFunctionContext> dz;

    public FxAnimMoveBlockAction(Expression<EnigmaFunctionContext> ticks, Expression<EnigmaFunctionContext> pos1,
                                 Expression<EnigmaFunctionContext> dx,
                                 Expression<EnigmaFunctionContext> dy,
                                 Expression<EnigmaFunctionContext> dz) {
        this.ticks = ticks;
        this.pos1 = pos1;
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "FxAnimMoveBlock:");

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
        double dx = ObjectTools.asDoubleSafe(this.dx.eval(context));
        double dy = ObjectTools.asDoubleSafe(this.dy.eval(context));
        double dz = ObjectTools.asDoubleSafe(this.dz.eval(context));
        BlockPos b1 = namedPosition1.getPos();

        MoveBlockAnimation animation = new MoveBlockAnimation(
                b1,
                new Vec3d(b1.getX(), b1.getY(), b1.getZ()),
                new Vec3d(b1.getX() + dx, b1.getY() + dy, b1.getZ() + dz), t);
        FxAnimationHandler.startAnimationServer((EntityPlayerMP)context.getPlayer(), animation);
    }

}
