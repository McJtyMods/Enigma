package mcjty.enigma.fxanim.animations;

import io.netty.buffer.ByteBuf;
import mcjty.enigma.Enigma;
import mcjty.enigma.blocks.MimicTE;
import mcjty.enigma.fxanim.FxAnimation;
import mcjty.enigma.varia.NetworkTools;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ColorMimicAnimation implements FxAnimation {

    public static final String FXANIM_COLORMIMIC = "colormimic";

    private final BlockPos mimicPos;
    private final Vec3d start;
    private final Vec3d end;
    private final int totalTicks;
    private int currentTick = 0;

    public ColorMimicAnimation(BlockPos mimicPos, Vec3d start, Vec3d end, int totalTicks) {
        this.mimicPos = mimicPos;
        this.start = start;
        this.end = end;
        this.totalTicks = totalTicks;
    }

    public ColorMimicAnimation(ByteBuf buf) {
        mimicPos = NetworkTools.readPos(buf);
        start = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        end = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        totalTicks = buf.readInt();
    }

    @Override
    public String getID() {
        return FXANIM_COLORMIMIC;
    }

    @Override
    public void writeToBuf(ByteBuf buf) {
        NetworkTools.writePos(buf, mimicPos);
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
        World world = Enigma.proxy.getClientWorld();
        TileEntity te = world.getTileEntity(mimicPos);
        if (te instanceof MimicTE) {
            MimicTE mimicTE = (MimicTE)te;
            double factor = ((double) currentTick) / totalTicks;
            double x = start.x + (end.x - start.x) * factor;
            double y = start.y + (end.y - start.y) * factor;
            double z = start.z + (end.z - start.z) * factor;
            mimicTE.setBlendColor(x, y, z);
        }
        currentTick++;
    }
}
