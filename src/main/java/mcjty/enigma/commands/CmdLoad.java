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
import net.minecraft.world.WorldServer;

import java.io.File;
import java.io.IOException;

public class CmdLoad extends CommandBase {
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
            ITextComponent component = new TextComponentString(TextFormatting.RED + "Script name missing!");
            if (sender instanceof EntityPlayer) {
                ((EntityPlayer) sender).sendStatusMessage(component, false);
            } else {
                sender.sendMessage(component);
            }
            return;
        }
        File dataDir = new File(((WorldServer) server.getEntityWorld()).getChunkSaveLocation(), "enigma");
        dataDir.mkdirs();
        File file = new File(dataDir, args[0]);
        try {
            RootScope.setRoot(RootScope.readRules(server.getEntityWorld(), file));
            ITextComponent component = new TextComponentString(TextFormatting.YELLOW + "Rules loaded!");
            if (sender instanceof EntityPlayer) {
                ((EntityPlayer) sender).sendStatusMessage(component, false);
            } else {
                sender.sendMessage(component);
            }
        } catch (IOException e) {
            ITextComponent component = new TextComponentString(TextFormatting.RED + "Error loading rules!");
            if (sender instanceof EntityPlayer) {
                ((EntityPlayer) sender).sendStatusMessage(component, false);
            } else {
                sender.sendMessage(component);
            }
        }
    }
}
