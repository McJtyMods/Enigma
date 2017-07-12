package mcjty.enigma.commands;

import mcjty.enigma.code.RootScope;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;

public class CmdReload extends CommandBase {
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
                ITextComponent component = new TextComponentString(TextFormatting.YELLOW + "Rules reloaded!");
                if (sender instanceof EntityPlayer) {
                    ((EntityPlayer) sender).sendStatusMessage(component, false);
                } else {
                    sender.sendMessage(component);
                }
            }
        } catch (IOException e) {
            ITextComponent component = new TextComponentString(TextFormatting.RED + "Could not reload rules!");
            if (sender instanceof EntityPlayer) {
                ((EntityPlayer) sender).sendStatusMessage(component, false);
            } else {
                sender.sendMessage(component);
            }
        }
    }
}
