package mcjty.enigma.commands;

import mcjty.enigma.Enigma;
import mcjty.enigma.code.RootScope;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class CmdReset extends CommandBase {
    @Override
    public String getName() {
        return "e_reset";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "e_reset";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        Enigma.setup.getLogger().info("Reset status:");
        RootScope.reset(server.getEntityWorld());
        ITextComponent component = new TextComponentString(TextFormatting.RED + "Total Enigma State Reset!");
        if (sender instanceof EntityPlayer) {
            ((EntityPlayer) sender).sendStatusMessage(component, false);
        } else {
            sender.sendMessage(component);
        }
    }
}
