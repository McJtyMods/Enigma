package mcjty.enigma.varia;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import javax.annotation.Nonnull;

public class BlockPosDim {

    @Nonnull private final BlockPos pos;
    private final int dimension;

    public BlockPosDim(@Nonnull BlockPos pos, int dimension) {
        this.pos = pos;
        this.dimension = dimension;
    }

    @Nonnull
    public BlockPos getPos() {
        return pos;
    }

    public int getDimension() {
        return dimension;
    }

    public IAreaIterator getIterator() {
        return new IAreaIterator() {
            private boolean first = true;

            @Override
            public boolean advance() {
                boolean f = first;
                first = false;
                return f;
            }

            @Override
            public BlockPos current() {
                return pos;
            }

            @Override
            public World getWorld() {
                return BlockPosDim.this.getWorld();
            }
        };
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlockPosDim that = (BlockPosDim) o;

        if (dimension != that.dimension) return false;
        if (!pos.equals(that.pos)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = pos.hashCode();
        result = 31 * result + dimension;
        return result;
    }

    public static double distance(BlockPos p1, BlockPos p2) {
        double d0 = p1.getX() - p2.getX();
        double d1 = p1.getY() - p2.getY();
        double d2 = p1.getZ() - p2.getZ();
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public WorldServer getWorld() {
        WorldServer world = DimensionManager.getWorld(getDimension());
        if (world == null) {
            world = DimensionManager.getWorld(0).getMinecraftServer().worldServerForDimension(getDimension());
        }
        return world;
    }
}
