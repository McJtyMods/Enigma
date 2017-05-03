package mcjty.enigma.commands;

import mcjty.enigma.Enigma;
import mcjty.enigma.code.RootScope;
import mcjty.lib.compat.CompatCommandBase;
import mcjty.lib.tools.ChatTools;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;

import java.io.File;
import java.io.IOException;

public class CmdLoad extends CompatCommandBase {
    @Override
    public String getName() {
        return "e_load";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "e_load <script>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            ChatTools.addChatMessage(sender, new TextComponentString(TextFormatting.RED + "Script name missing!"));
            return;
        }
        File dataDir = new File(((WorldServer) server.getEntityWorld()).getChunkSaveLocation(), "enigma");
        dataDir.mkdirs();
        File file = new File(dataDir, args[0]);
        try {
            RootScope.setRoot(RootScope.readRules(server.getEntityWorld(), file));
            ChatTools.addChatMessage(sender, new TextComponentString(TextFormatting.YELLOW + "Rules loaded!"));
        } catch (IOException e) {
            ChatTools.addChatMessage(sender, new TextComponentString(TextFormatting.RED + "Error loading rules!"));
        }
    }
}
