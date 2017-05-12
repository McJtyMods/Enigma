package mcjty.enigma.code.actions;

import mcjty.enigma.blocks.MimicTE;
import mcjty.enigma.code.Action;
import mcjty.enigma.code.EnigmaFunctionContext;
import mcjty.enigma.code.ExecutionException;
import mcjty.enigma.fxanim.FxAnimationHandler;
import mcjty.enigma.fxanim.animations.ColorMimicAnimation;
import mcjty.enigma.fxanim.animations.MoveMimicAnimation;
import mcjty.enigma.parser.Expression;
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

public class FxAnimResetMimicAction extends Action {
    private final Expression<EnigmaFunctionContext> pos;

    public FxAnimResetMimicAction(Expression<EnigmaFunctionContext> pos) {
        this.pos = pos;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "FxAnimResetMimic:");

    }

    @Override
    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        checkPlayer(context);

        Progress progress = ProgressHolder.getProgress(context.getWorld());
        IAreaIterator iterator = AreaTools.getAreaIterator(progress, pos.eval(context));

        World world = iterator.getWorld();

        while (iterator.advance()) {
            BlockPos b1 = iterator.current();

            TileEntity te = world.getTileEntity(b1);
            if (te instanceof MimicTE) {
                MimicTE mimicTE = (MimicTE) te;
                mimicTE.setOffset(0, 0, 0);
                mimicTE.setBlendColor(1.0, 1.0, 1.0);
                MoveMimicAnimation animation = new MoveMimicAnimation(
                        b1,
                        new Vec3d(b1.getX(), b1.getY(), b1.getZ()),
                        new Vec3d(b1.getX(), b1.getY(), b1.getZ()), 1);
                FxAnimationHandler.startAnimationServer((EntityPlayerMP) context.getPlayer(), animation);

                ColorMimicAnimation animation2 = new ColorMimicAnimation(
                        b1,
                        new Vec3d(1, 1, 1),
                        new Vec3d(1, 1, 1), 1);
                FxAnimationHandler.startAnimationServer((EntityPlayerMP)context.getPlayer(), animation2);
            } else {
                throw new ExecutionException("Position '" + pos.eval(context) + "' does not seem to have a mimic!");
            }
        }
    }

}
