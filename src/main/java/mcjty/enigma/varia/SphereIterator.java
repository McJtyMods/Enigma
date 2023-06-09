package mcjty.enigma.varia;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class SphereIterator implements IAreaIterator {
    private BlockPos.MutableBlockPos mp;
    @Nonnull private final Sphere sphere;

    public SphereIterator(Sphere sphere) {
        this.mp = null;
        this.sphere = sphere;
    }

    @Override
    public void restart() {
        mp = null;
    }

    @Override
    public BlockPos getBottomLeft() {
        BlockPos center = sphere.getPos();
        int radius = sphere.getRadius();
        return new BlockPos(center.getX() - radius, center.getY() - radius, center.getZ() - radius);
    }

    @Override
    public BlockPos getTopRight() {
        BlockPos center = sphere.getPos();
        int radius = sphere.getRadius();
        return new BlockPos(center.getX() + radius, center.getY() + radius, center.getZ() + radius);
    }

    @Override
    public boolean advance() {
        if (mp == null) {
            mp = new BlockPos.MutableBlockPos(getBottomLeft());
        }
        return advanceToNextPosInSphere(mp);
    }

    private boolean advanceToNextPosInSphere(BlockPos.MutableBlockPos mp) {
        BlockPos center = sphere.getPos();
        int radius = sphere.getRadius();
        while (true) {
            if (mp.distanceSq(center) <= radius * radius) {
                return true;
            }
            if (mp.getX() < center.getX() + radius) {
                mp.setPos(mp.getX() + 1, mp.getY(), mp.getZ());
            } else if (mp.getY() < center.getY() + radius) {
                mp.setPos(center.getX() - radius, mp.getY() + 1, mp.getZ());
            } else if (mp.getZ() < center.getZ() + radius) {
                mp.setPos(center.getX() - radius, center.getY() - radius, mp.getZ() + 1);
            } else {
                return false;
            }
        }
    }

    @Override
    public BlockPos current() {
        return mp;
    }

    @Override
    public World getWorld() {
        return sphere.getWorld();
    }
}
