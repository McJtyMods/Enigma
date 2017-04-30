package mcjty.enigma.varia;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

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

}
