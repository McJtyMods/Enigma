package mcjty.enigma.varia;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class AreaIterator implements IAreaIterator {
    private BlockPos.MutableBlockPos mp;
    @Nonnull private final Area area;

    public AreaIterator(Area area) {
        this.mp = null;
        this.area = area;
    }

    @Override
    public boolean advance() {
        BlockPos pos1 = area.getPos1();
        BlockPos pos2 = area.getPos2();
        if (mp == null) {
            mp = new BlockPos.MutableBlockPos(pos1);
            return true;
        }
        if (mp.getX() < pos2.getX() - 1) {
            mp.setPos(mp.getX() + 1, mp.getY(), mp.getZ());
            return true;
        }
        if (mp.getY() < pos2.getY() - 1) {
            mp.setPos(pos1.getX(), mp.getY() + 1, mp.getZ());
            return true;
        }
        if (mp.getZ() < pos2.getZ() - 1) {
            mp.setPos(pos1.getX(), pos1.getY(), mp.getZ() + 1);
            return true;
        }
        return false;
    }

    @Override
    public BlockPos current() {
        return mp;
    }

    @Override
    public World getWorld() {
        return area.getWorld();
    }
}
