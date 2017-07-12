package mcjty.enigma.commands;

import mcjty.enigma.code.EnigmaExpressionContext;
import mcjty.enigma.code.EnigmaFunctionContext;
import mcjty.enigma.code.ExecutionException;
import mcjty.enigma.code.actions.*;
import mcjty.enigma.parser.*;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import mcjty.lib.compat.CompatCommandBase;
import mcjty.lib.tools.ChatTools;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.StringUtils;

public class CmdAction extends CompatCommandBase {
    @Override
    public String getName() {
        return "e_action";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "e_action <action> [<parameters> ...]";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            ChatTools.addChatMessage(sender, new TextComponentString(TextFormatting.RED + "Action missing!"));
            return;
        }

        Progress progress = ProgressHolder.getProgress(server.getEntityWorld());
        String line = StringUtils.join(args, ' ');
        try {
            TokenizedLine<EnigmaFunctionContext> tokenizedLine = RuleParser.getTokenizedLine(line, 0,  new EnigmaExpressionContext(progress));
            MainToken token = tokenizedLine.getMainToken();
            EnigmaFunctionContext context = new EnigmaFunctionContext(server.getEntityWorld(), (EntityPlayer) sender);
            switch (token) {
                case STATE:
                    new SetStateAction(tokenizedLine.getParameters().get(0), tokenizedLine.getParameters().get(1)).execute(context);
                    break;
                case PSTATE:
                    new SetPlayerStateAction(tokenizedLine.getParameters().get(0), tokenizedLine.getParameters().get(1)).execute(context);
                    break;
                case VAR:
                    new SetVariableAction(tokenizedLine.getParameters().get(0), tokenizedLine.getParameters().get(1)).execute(context);
                    break;
                case PVAR:
                    new SetPlayerVariableAction(tokenizedLine.getParameters().get(0), tokenizedLine.getParameters().get(1)).execute(context);
                    break;
                case GIVE:
                    new GiveAction(tokenizedLine.getParameters().get(0)).execute(context);
                    break;
                case SETTING:
                    ProgramParser.parseSettingAction(tokenizedLine).execute(context);
                    break;
                case SETBLOCK:
                    new SetBlockAction(tokenizedLine.getParameters().get(0), tokenizedLine.getParameters().get(1)).execute(context);
                    break;
                case KILL:
                    new KillAction(tokenizedLine.getParameters().get(0)).execute(context);
                    break;
                case TELEPORT:
                    new TeleportAction(tokenizedLine.getParameters().get(0)).execute(context);
                    break;
                default:
                    ChatTools.addChatMessage(sender, new TextComponentString(TextFormatting.RED + "Unknown action!"));
            }
        } catch (ParserException e) {
            ChatTools.addChatMessage(sender, new TextComponentString(TextFormatting.RED + "Error parsing line!"));
        } catch (ExecutionException e) {
            ChatTools.addChatMessage(sender, new TextComponentString(TextFormatting.RED + "Error executing line!"));
        }
    }
}
