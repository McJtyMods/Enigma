package mcjty.enigma.commands;

import mcjty.enigma.Enigma;
import mcjty.enigma.progress.PlayerProgress;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import mcjty.lib.compat.CompatCommandBase;
import mcjty.lib.tools.ChatTools;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.Map;
import java.util.UUID;

import static mcjty.enigma.varia.StringRegister.STRINGS;

public class CmdReset extends CompatCommandBase {
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
        Enigma.logger.info("Reset status:");
        Progress progress = ProgressHolder.getProgress(server.getEntityWorld());
        progress.reset();
        ProgressHolder.save(server.getEntityWorld());
        ChatTools.addChatMessage(sender, new TextComponentString(TextFormatting.RED + "Total Enigma State Reset!"));
    }
}
