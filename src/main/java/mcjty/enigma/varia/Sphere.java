package mcjty.enigma.varia;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import javax.annotation.Nonnull;

public class Sphere implements IPositional<Sphere> {

    @Nonnull private final BlockPos pos;
    private final int dimension;
    private final int radius;
    private final int sqRadius;

    public Sphere(@Nonnull BlockPos pos, int dimension, int radius) {
        this.pos = pos;
        this.dimension = dimension;
        this.radius = radius;
        this.sqRadius = radius * radius;
    }

    @Override
    public Sphere up(int amount) {
        return new Sphere(pos.up(amount), dimension, radius);
    }

    @Override
    public Sphere down(int amount) {
        return new Sphere(pos.down(amount), dimension, radius);
    }

    @Override
    public Sphere west(int amount) {
        return new Sphere(pos.west(amount), dimension, radius);
    }

    @Override
    public Sphere east(int amount) {
        return new Sphere(pos.east(amount), dimension, radius);
    }

    @Override
    public Sphere south(int amount) {
        return new Sphere(pos.south(amount), dimension, radius);
    }

    @Override
    public Sphere north(int amount) {
        return new Sphere(pos.north(amount), dimension, radius);
    }

    @Override
    public void serializeNBT(NBTTagCompound tc) {
        BlockPos pos = getPos();
        tc.setInteger("x", pos.getX());
        tc.setInteger("y", pos.getY());
        tc.setInteger("z", pos.getZ());
        tc.setInteger("dim", getDimension());
        tc.setInteger("radius", radius);
    }

    @Override
    @Nonnull
    public BlockPos getPos() {
        return pos;
    }

    @Override
    public int getDimension() {
        return dimension;
    }

    public int getRadius() {
        return radius;
    }

    @Override
    public boolean isInside(BlockPos pos) {
        return squaredDistance(pos, getPos()) <= sqRadius;
    }

    public IAreaIterator getIterator() {
        return new SphereIterator(this);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Sphere that = (Sphere) o;

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

    public static double squaredDistance(BlockPos p1, BlockPos p2) {
        double d0 = p1.getX() - p2.getX();
        double d1 = p1.getY() - p2.getY();
        double d2 = p1.getZ() - p2.getZ();
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    @Override
    public WorldServer getWorld() {
        WorldServer world = DimensionManager.getWorld(getDimension());
        if (world == null) {
            world = DimensionManager.getWorld(0).getMinecraftServer().getWorld(getDimension());
        }
        return world;
    }
}
