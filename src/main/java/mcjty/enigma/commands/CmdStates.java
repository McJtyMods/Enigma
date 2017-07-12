package mcjty.enigma.commands;

import mcjty.enigma.Enigma;
import mcjty.enigma.progress.PlayerProgress;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.Map;
import java.util.UUID;

import static mcjty.enigma.varia.StringRegister.STRINGS;

public class CmdStates extends CommandBase {
    @Override
    public String getName() {
        return "e_states";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "e_states";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        ITextComponent component = new TextComponentString(TextFormatting.GREEN + "See log for info!");
        if (sender instanceof EntityPlayer) {
            ((EntityPlayer) sender).sendStatusMessage(component, false);
        } else {
            sender.sendMessage(component);
        }
        Enigma.logger.info("Current status:");
        Progress progress = ProgressHolder.getProgress(server.getEntityWorld());

        for (Map.Entry<Integer, Integer> entry : progress.getStates().entrySet()) {
            String name = STRINGS.get(entry.getKey());
            String value = STRINGS.get(entry.getValue());
            Enigma.logger.info("State: " + name + " = " + value);
        }

        for (Map.Entry<UUID, PlayerProgress> entry : progress.getPlayerProgress().entrySet()) {
            EntityPlayerMP player = server.getPlayerList().getPlayerByUUID(entry.getKey());
            Enigma.logger.info("Player: " + entry.getKey() + (player == null ? "" : (" (" + player.getDisplayNameString() + ")")));
            for (Map.Entry<Integer, Integer> pp : entry.getValue().getStates().entrySet()) {
                String name = STRINGS.get(pp.getKey());
                String value = STRINGS.get(pp.getValue());
                Enigma.logger.info("    State: " + name + " = " + value);
            }
        }

    }
}
