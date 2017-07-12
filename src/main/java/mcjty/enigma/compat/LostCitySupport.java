package mcjty.enigma.compat;

import mcjty.lostcities.api.ILostChunkGenerator;
import mcjty.lostcities.api.ILostChunkInfo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class LostCitySupport {

    public static boolean isLostCity(World world) {
        WorldServer ws = (WorldServer) world;
        return ws.getChunkProvider().chunkGenerator instanceof ILostChunkGenerator;
    }

    private static ILostChunkInfo getChunkInfo(World world, BlockPos pos) {
        WorldServer ws = (WorldServer) world;
        ILostChunkGenerator generator = (ILostChunkGenerator) ws.getChunkProvider().chunkGenerator;
        return generator.getChunkInfo(pos.getX() >> 4, pos.getZ() >> 4);
    }

    public static int getFloor0Height(World world, BlockPos pos) {
        WorldServer ws = (WorldServer) world;
        ILostChunkGenerator generator = (ILostChunkGenerator) ws.getChunkProvider().chunkGenerator;
        ILostChunkInfo info = generator.getChunkInfo(pos.getX() >> 4, pos.getZ() >> 4);
        return generator.getRealHeight(info.getCityLevel());
    }
}
