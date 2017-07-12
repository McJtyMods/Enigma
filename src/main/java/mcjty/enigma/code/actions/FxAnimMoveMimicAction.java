package mcjty.enigma.code.actions;

import mcjty.enigma.blocks.MimicTE;
import mcjty.enigma.code.Action;
import mcjty.enigma.code.EnigmaFunctionContext;
import mcjty.enigma.code.ExecutionException;
import mcjty.enigma.fxanim.FxAnimationHandler;
import mcjty.enigma.fxanim.animations.MoveMimicAnimation;
import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import mcjty.enigma.varia.AreaTools;
import mcjty.enigma.varia.IAreaIterator;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;

public class FxAnimMoveMimicAction extends Action {
    private final Expression<EnigmaFunctionContext> ticks;
    private final Expression<EnigmaFunctionContext> pos;
    private final Expression<EnigmaFunctionContext> dx;
    private final Expression<EnigmaFunctionContext> dy;
    private final Expression<EnigmaFunctionContext> dz;

    public FxAnimMoveMimicAction(Expression<EnigmaFunctionContext> ticks, Expression<EnigmaFunctionContext> pos,
                                 Expression<EnigmaFunctionContext> dx,
                                 Expression<EnigmaFunctionContext> dy,
                                 Expression<EnigmaFunctionContext> dz) {
        this.ticks = ticks;
        this.pos = pos;
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "FxAnimMoveMimic:");

    }

    @Override
    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        checkPlayer(context);

        int t = ObjectTools.asIntSafe(ticks.eval(context));

        Progress progress = ProgressHolder.getProgress(context.getWorld());
        Object p = pos.eval(context);
        IAreaIterator iterator = AreaTools.getAreaIterator(progress, p);

        double dx = ObjectTools.asDoubleSafe(this.dx.eval(context));
        double dy = ObjectTools.asDoubleSafe(this.dy.eval(context));
        double dz = ObjectTools.asDoubleSafe(this.dz.eval(context));

        World world = iterator.getWorld();

        while (iterator.advance()) {
            BlockPos b1 = iterator.current();

            TileEntity te = world.getTileEntity(b1);
            if (te instanceof MimicTE) {
                MimicTE mimicTE = (MimicTE) te;
                mimicTE.setOffset(dx, dy, dz);
            } else {
                throw new ExecutionException("Position '" + p + "' does not seem to have a mimic!");
            }

            MoveMimicAnimation animation = new MoveMimicAnimation(
                    b1,
                    new Vec3d(b1.getX(), b1.getY(), b1.getZ()),
                    new Vec3d(b1.getX() + dx, b1.getY() + dy, b1.getZ() + dz), t);
            FxAnimationHandler.startAnimationServer((EntityPlayerMP) context.getPlayer(), animation);
        }
    }

}
