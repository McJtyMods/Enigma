package mcjty.enigma.varia;

import net.minecraft.util.math.BlockPos;

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
}
