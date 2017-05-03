package mcjty.enigma.commands;

import mcjty.enigma.code.EnigmaExpressionContext;
import mcjty.enigma.code.EnigmaFunctionContext;
import mcjty.enigma.parser.ExpressionException;
import mcjty.enigma.parser.ExpressionParser;
import mcjty.enigma.parser.ParsedExpression;
import mcjty.enigma.parser.StringPointer;
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

public class CmdEval extends CompatCommandBase {
    @Override
    public String getName() {
        return "e_eval";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "e_eval <expression>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            ChatTools.addChatMessage(sender, new TextComponentString(TextFormatting.RED + "Expression missing!"));
            return;
        }
        Progress progress = ProgressHolder.getProgress(server.getEntityWorld());
        String expression = StringUtils.join(args, ' ');
        try {
            ParsedExpression<EnigmaFunctionContext> parsed = ExpressionParser.eval(new StringPointer(expression), new EnigmaExpressionContext(progress));
            Object result = parsed.getExpression().eval(new EnigmaFunctionContext(server.getEntityWorld(), (EntityPlayer) sender));
            ChatTools.addChatMessage(sender, new TextComponentString(TextFormatting.GREEN + "Result: " + result));
        } catch (ExpressionException e) {
            ChatTools.addChatMessage(sender, new TextComponentString(TextFormatting.RED + "Error: " + e.getMessage()));
        }
    }
}
