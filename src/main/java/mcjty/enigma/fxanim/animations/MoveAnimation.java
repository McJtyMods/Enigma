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
        buf.writeDouble(start.x);
        buf.writeDouble(start.y);
        buf.writeDouble(start.z);
        buf.writeDouble(end.x);
        buf.writeDouble(end.y);
        buf.writeDouble(end.z);
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
        double x = start.x + (end.x - start.x) * factor;
        double y = start.y + (end.y - start.y) * factor;
        double z = start.z + (end.z - start.z) * factor;
        player.setPosition(x, y, z);
        currentTick++;
    }
}
