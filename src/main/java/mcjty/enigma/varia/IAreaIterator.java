package mcjty.enigma.varia;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IAreaIterator {

    BlockPos getCorner();

    boolean advance();

    BlockPos current();

    World getWorld();
}
