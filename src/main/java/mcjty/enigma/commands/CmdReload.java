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

import java.io.IOException;

public class CmdReload extends CompatCommandBase {
    @Override
    public String getName() {
        return "e_reload";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "e_reload";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        try {
            if (RootScope.reload(server.getEntityWorld(), (EntityPlayer) sender)) {
                ChatTools.addChatMessage(sender, new TextComponentString(TextFormatting.YELLOW + "Rules reloaded!"));
            }
        } catch (IOException e) {
            ChatTools.addChatMessage(sender, new TextComponentString(TextFormatting.RED + "Could not reload rules!"));
        }
    }
}
