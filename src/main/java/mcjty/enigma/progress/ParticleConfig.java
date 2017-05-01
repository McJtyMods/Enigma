package mcjty.enigma.progress;

import net.minecraft.util.EnumParticleTypes;

public class ParticleConfig {
    private final EnumParticleTypes particles;
    private final int amount;
    private final double offsetX;
    private final double offsetY;
    private final double offsetZ;
    private final double speed;

    public ParticleConfig(EnumParticleTypes particles, int amount, double offsetX, double offsetY, double offsetZ, double speed) {
        this.particles = particles;
        this.amount = amount;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.speed = speed;
    }

    public EnumParticleTypes getParticles() {
        return particles;
    }

    public int getAmount() {
        return amount;
    }

    public double getOffsetX() {
        return offsetX;
    }

    public double getOffsetY() {
        return offsetY;
    }

    public double getOffsetZ() {
        return offsetZ;
    }

    public double getSpeed() {
        return speed;
    }
}
