package mcjty.enigma.fxanim.animations;

import io.netty.buffer.ByteBuf;
import mcjty.enigma.Enigma;
import mcjty.enigma.fxanim.FxAnimation;
import net.minecraft.entity.player.EntityPlayer;

public class RotateAnimation implements FxAnimation {

    public static final String FXANIM_ROTATE = "rotate";

    private final double startYaw;
    private final double endYaw;
    private final double startPitch;
    private final double endPitch;
    private final int totalTicks;
    private int currentTick = 0;

    public RotateAnimation(double startYaw, double startPitch, double endYaw, double endPitch, int totalTicks) {
        this.startYaw = startYaw;
        this.endYaw = endYaw;
        this.startPitch = startPitch;
        this.endPitch = endPitch;
        this.totalTicks = totalTicks;
    }

    public RotateAnimation(ByteBuf buf) {
        startYaw = buf.readDouble();
        endYaw = buf.readDouble();
        startPitch = buf.readDouble();
        endPitch = buf.readDouble();
        totalTicks = buf.readInt();
    }

    @Override
    public String getID() {
        return FXANIM_ROTATE;
    }

    @Override
    public void writeToBuf(ByteBuf buf) {
        buf.writeDouble(startYaw);
        buf.writeDouble(endYaw);
        buf.writeDouble(startPitch);
        buf.writeDouble(endPitch);
        buf.writeInt(totalTicks);
    }

    @Override
    public boolean isDead() {
        return currentTick >= totalTicks;
    }

    @Override
    public void tick() {
        EntityPlayer player = Enigma.proxy.getClientPlayer();
        double factor = ((double) currentTick) / totalTicks;
        player.rotationYaw = (float) (startYaw + (endYaw - startYaw) * factor);
        player.rotationPitch = (float) (startPitch + (endPitch - startPitch) * factor);
        currentTick++;
    }
}
