package mcjty.enigma.commands;

import mcjty.enigma.snapshot.SnapshotTools;
import mcjty.lib.compat.CompatCommandBase;
import mcjty.lib.tools.ChatTools;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

import java.io.File;
import java.io.IOException;

public class CmdRestore extends CompatCommandBase {
    @Override
    public String getName() {
        return "e_restore";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "e_restore";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            ChatTools.addChatMessage(sender, new TextComponentString(TextFormatting.RED + "File missing!"));
            return;
        }
        String fn = args[0];

        World world = sender.getEntityWorld();
        try {
            File dataDir = new File(((WorldServer) world).getChunkSaveLocation(), "enigmasnap");
            dataDir.mkdirs();
            File file = new File(dataDir, fn);
            SnapshotTools.restoreChunkSnapshot(world, file);
            ChatTools.addChatMessage(sender, new TextComponentString(TextFormatting.GREEN + "Restored snapshot from '" + fn + "'"));
        } catch (IOException e) {
            ChatTools.addChatMessage(sender, new TextComponentString(TextFormatting.RED + "Error reading snapshot from '" + fn + "'!"));
        }
    }
}
