package mcjty.enigma.code.actions;

import mcjty.enigma.blocks.MimicTE;
import mcjty.enigma.code.Action;
import mcjty.enigma.code.EnigmaFunctionContext;
import mcjty.enigma.code.ExecutionException;
import mcjty.enigma.fxanim.FxAnimationHandler;
import mcjty.enigma.fxanim.animations.ColorBlockAnimation;
import mcjty.enigma.fxanim.animations.MoveBlockAnimation;
import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import mcjty.enigma.varia.BlockPosDim;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.StringUtils;

public class FxAnimColorBlockAction extends Action {
    private final Expression<EnigmaFunctionContext> ticks;
    private final Expression<EnigmaFunctionContext> pos;
    private final Expression<EnigmaFunctionContext> r1;
    private final Expression<EnigmaFunctionContext> g1;
    private final Expression<EnigmaFunctionContext> b1;
    private final Expression<EnigmaFunctionContext> r2;
    private final Expression<EnigmaFunctionContext> g2;
    private final Expression<EnigmaFunctionContext> b2;

    public FxAnimColorBlockAction(Expression<EnigmaFunctionContext> ticks, Expression<EnigmaFunctionContext> pos,
                                  Expression<EnigmaFunctionContext> r1,
                                  Expression<EnigmaFunctionContext> g1,
                                  Expression<EnigmaFunctionContext> b1,
                                  Expression<EnigmaFunctionContext> r2,
                                  Expression<EnigmaFunctionContext> g2,
                                  Expression<EnigmaFunctionContext> b2) {
        this.ticks = ticks;
        this.pos = pos;
        this.r1 = r1;
        this.g1 = g1;
        this.b1 = b1;
        this.r2 = r2;
        this.g2 = g2;
        this.b2 = b2;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "FxAnimColorBlock:");

    }

    @Override
    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        checkPlayer(context);

        int t = ObjectTools.asIntSafe(ticks.eval(context));

        Progress progress = ProgressHolder.getProgress(context.getWorld());
        Object p = pos.eval(context);
        BlockPosDim namedPosition = progress.getNamedPosition(p);
        if (namedPosition == null) {
            throw new ExecutionException("Cannot find named position '" + p + "'!");
        }
        double r1 = ObjectTools.asDoubleSafe(this.r1.eval(context));
        double g1 = ObjectTools.asDoubleSafe(this.g1.eval(context));
        double b1 = ObjectTools.asDoubleSafe(this.b1.eval(context));
        double r2 = ObjectTools.asDoubleSafe(this.r2.eval(context));
        double g2 = ObjectTools.asDoubleSafe(this.g2.eval(context));
        double b2 = ObjectTools.asDoubleSafe(this.b2.eval(context));
        BlockPos b = namedPosition.getPos();

        TileEntity te = namedPosition.getWorld().getTileEntity(b);
        if (te instanceof MimicTE) {
            MimicTE mimicTE = (MimicTE) te;
            mimicTE.setBlendColor(r2, g2, b2);
        } else {
            throw new ExecutionException("Position '" + p + "' does not seem to have a mimic!");
        }

        ColorBlockAnimation animation = new ColorBlockAnimation(
                b,
                new Vec3d(r1, g1, b1),
                new Vec3d(r2, g2, b2), t);
        FxAnimationHandler.startAnimationServer((EntityPlayerMP)context.getPlayer(), animation);
    }

}
