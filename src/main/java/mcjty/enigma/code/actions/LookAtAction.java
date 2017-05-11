package mcjty.enigma.code.actions;

import mcjty.enigma.code.Action;
import mcjty.enigma.code.EnigmaFunctionContext;
import mcjty.enigma.code.ExecutionException;
import mcjty.enigma.parser.Expression;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import mcjty.enigma.varia.BlockPosDim;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.StringUtils;

public class LookAtAction extends Action {
    private final Expression<EnigmaFunctionContext> position;

    public LookAtAction(Expression<EnigmaFunctionContext> position) {
        this.position = position;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "LookAt: " + position);
    }

    @Override
    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        checkPlayer(context);
        Progress progress = ProgressHolder.getProgress(context.getWorld());
        Object pos = position.eval(context);
        BlockPosDim namedPosition = progress.getNamedPosition(pos);
        if (namedPosition == null) {
            throw new ExecutionException("Cannot find named position '" + pos + "'!");
        }
        BlockPos p = namedPosition.getPos();
        EntityPlayer player = context.getPlayer();

        faceLocation(p, player);
        player.setPositionAndUpdate(player.posX, player.posY, player.posZ);
    }

    private void faceLocation(BlockPos lookat, EntityPlayer player) {
        double d0 = lookat.getX() + .5 - player.posX;
        double d1 = lookat.getY() + .5 - player.posY - player.getEyeHeight();
        double d2 = lookat.getZ() + .5 - player.posZ;

        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        float yaw = (float) (Math.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
        float pitch = (float) (-(Math.atan2(d1, d3) * (180D / Math.PI)));
        player.rotationPitch = this.updateRotation(player.rotationPitch, pitch);
        player.rotationYaw = this.updateRotation(player.rotationYaw, yaw);
    }

    private float updateRotation(float angle, float targetAngle) {
        float f = MathHelper.wrapDegrees(targetAngle - angle);
        return angle + f;
    }


}
