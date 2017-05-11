package mcjty.enigma.varia;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import javax.annotation.Nonnull;

public class Area {
    @Nonnull private final BlockPos pos1;
    @Nonnull private final BlockPos pos2;
    private final int dimension;

    public Area(@Nonnull BlockPos pos1, @Nonnull BlockPos pos2, int dimension) {
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.dimension = dimension;
    }

    @Nonnull
    public BlockPos getPos1() {
        return pos1;
    }

    @Nonnull
    public BlockPos getPos2() {
        return pos2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Area area = (Area) o;

        if (dimension != area.dimension) return false;
        if (!pos1.equals(area.pos1)) return false;
        if (!pos2.equals(area.pos2)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = pos1.hashCode();
        result = 31 * result + pos2.hashCode();
        result = 31 * result + dimension;
        return result;
    }

    public int getDimension() {
        return dimension;
    }

    public WorldServer getWorld() {
        WorldServer world = DimensionManager.getWorld(getDimension());
        if (world == null) {
            world = DimensionManager.getWorld(0).getMinecraftServer().worldServerForDimension(getDimension());
        }
        return world;
    }
}
