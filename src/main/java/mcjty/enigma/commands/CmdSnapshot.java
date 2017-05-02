package mcjty.enigma.commands;

import mcjty.enigma.snapshot.FakeWorld;
import mcjty.lib.compat.CompatCommandBase;
import mcjty.lib.tools.ChatTools;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkGenerator;

import java.util.ArrayList;
import java.util.List;

public class CmdSnapshot extends CompatCommandBase {
    @Override
    public String getName() {
        return "e_snapshot";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "e_snapshot";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        ChatTools.addChatMessage(sender, new TextComponentString(TextFormatting.GREEN + "Making a snapshot!"));
        World world = sender.getEntityWorld();
        BlockPos pos = sender.getPosition();
        Chunk curchunk = world.getChunkFromBlockCoords(pos);

//        FakeWorld fakeWorld = new FakeWorld(world);
        IChunkGenerator chunkGenerator = world.provider.createChunkGenerator();
        Chunk origchunk = chunkGenerator.provideChunk(pos.getX() >> 4, pos.getZ() >> 4);
//        origchunk.populateChunk(fakeWorld.getChunkProvider(), chunkGenerator);

        List<BlockPos> diffWithOrig = new ArrayList<>();
        for (int y = 0 ; y < world.getHeight() ; y++) {
            for (int x = 0 ; x < 16 ; x++) {
                for (int z = 0 ; z < 16 ; z++) {
                    IBlockState curstate = curchunk.getBlockState(x, y, z);
                    IBlockState origstate = origchunk.getBlockState(x, y, z);
                    if (!curstate.equals(origstate)) {
                        int xx = (curchunk.getPos().chunkXPos << 4) + x;
                        int zz = (curchunk.getPos().chunkZPos << 4) + z;
                        BlockPos p = new BlockPos(xx, y, zz);
                        diffWithOrig.add(p);
                    }
                }
            }
        }
        for (BlockPos blockPos : diffWithOrig) {
            System.out.println("blockPos = " + blockPos);
        }

    }
}
