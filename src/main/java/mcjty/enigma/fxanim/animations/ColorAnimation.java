package mcjty.enigma.fxanim.animations;

import io.netty.buffer.ByteBuf;
import mcjty.enigma.Enigma;
import mcjty.enigma.fxanim.FxAnimation;
import mcjty.enigma.overlays.OverlayRenderer;
import net.minecraft.entity.player.EntityPlayer;

public class ColorAnimation implements FxAnimation {

    public static final String FXANIM_COLOR = "color";

    private final double startA;
    private final double startR;
    private final double startG;
    private final double startB;
    private final double endA;
    private final double endR;
    private final double endG;
    private final double endB;
    private final int totalTicks;
    private int currentTick = 0;

    public ColorAnimation(double startA, double startR, double startG, double startB, double endA, double endR, double endG, double endB, int totalTicks) {
        this.startA = startA;
        this.startR = startR;
        this.startG = startG;
        this.startB = startB;
        this.endA = endA;
        this.endR = endR;
        this.endG = endG;
        this.endB = endB;
        this.totalTicks = totalTicks;
    }

    public ColorAnimation(ByteBuf buf) {
        startA = buf.readDouble();
        startR = buf.readDouble();
        startG = buf.readDouble();
        startB = buf.readDouble();
        endA = buf.readDouble();
        endR = buf.readDouble();
        endG = buf.readDouble();
        endB = buf.readDouble();
        totalTicks = buf.readInt();
    }

    @Override
    public String getID() {
        return FXANIM_COLOR;
    }

    @Override
    public void writeToBuf(ByteBuf buf) {
        buf.writeDouble(startA);
        buf.writeDouble(startR);
        buf.writeDouble(startG);
        buf.writeDouble(startB);
        buf.writeDouble(endA);
        buf.writeDouble(endR);
        buf.writeDouble(endG);
        buf.writeDouble(endB);
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
        float a = (float) (startA + (endA - startA) * factor);
        float r = (float) (startR + (endR - startR) * factor);
        float g = (float) (startG + (endG - startG) * factor);
        float b = (float) (startB + (endB - startB) * factor);
        OverlayRenderer.setOverlayColor(a, r, g, b);
        currentTick++;
    }
}
