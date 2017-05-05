package mcjty.enigma.fxanim.animations;

import io.netty.buffer.ByteBuf;
import mcjty.enigma.Enigma;
import mcjty.enigma.fxanim.FxAnimation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

public class MoveAnimation implements FxAnimation {

    public static final String FXANIM_MOVE = "move";

    private final Vec3d start;
    private final Vec3d end;
    private final int totalTicks;
    private int currentTick = 0;

    public MoveAnimation(Vec3d start, Vec3d end, int totalTicks) {
        this.start = start;
        this.end = end;
        this.totalTicks = totalTicks;
    }

    public MoveAnimation(ByteBuf buf) {
        start = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        end = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        totalTicks = buf.readInt();
    }

    @Override
    public String getID() {
        return FXANIM_MOVE;
    }

    @Override
    public void writeToBuf(ByteBuf buf) {
        buf.writeDouble(start.xCoord);
        buf.writeDouble(start.yCoord);
        buf.writeDouble(start.zCoord);
        buf.writeDouble(end.xCoord);
        buf.writeDouble(end.yCoord);
        buf.writeDouble(end.zCoord);
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
        double x = start.xCoord + (end.xCoord - start.xCoord) * factor;
        double y = start.yCoord + (end.yCoord - start.yCoord) * factor;
        double z = start.zCoord + (end.zCoord - start.zCoord) * factor;
        player.setPosition(x, y, z);
        currentTick++;
    }
}
