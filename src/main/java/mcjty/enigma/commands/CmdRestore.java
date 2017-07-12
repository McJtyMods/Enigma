package mcjty.enigma.commands;

import mcjty.enigma.snapshot.SnapshotTools;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.io.File;
import java.io.IOException;

public class CmdRestore extends CommandBase {
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
            ITextComponent component = new TextComponentString(TextFormatting.RED + "File missing!");
            if (sender instanceof EntityPlayer) {
                ((EntityPlayer) sender).sendStatusMessage(component, false);
            } else {
                sender.sendMessage(component);
            }
            return;
        }
        String fn = args[0];

        World world = sender.getEntityWorld();
        try {
            File dataDir = new File(((WorldServer) world).getChunkSaveLocation(), "enigma");
            dataDir.mkdirs();
            File file = new File(dataDir, fn);
            SnapshotTools.restoreChunkSnapshot(world, file);
            ITextComponent component = new TextComponentString(TextFormatting.GREEN + "Restored snapshot from '" + fn + "'");
            if (sender instanceof EntityPlayer) {
                ((EntityPlayer) sender).sendStatusMessage(component, false);
            } else {
                sender.sendMessage(component);
            }
        } catch (IOException e) {
            ITextComponent component = new TextComponentString(TextFormatting.RED + "Error reading snapshot from '" + fn + "'!");
            if (sender instanceof EntityPlayer) {
                ((EntityPlayer) sender).sendStatusMessage(component, false);
            } else {
                sender.sendMessage(component);
            }
        }
    }
}
