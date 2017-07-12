package mcjty.enigma.commands;

import mcjty.enigma.code.EnigmaExpressionContext;
import mcjty.enigma.code.EnigmaFunctionContext;
import mcjty.enigma.code.ExecutionException;
import mcjty.enigma.parser.ExpressionException;
import mcjty.enigma.parser.ExpressionParser;
import mcjty.enigma.parser.ParsedExpression;
import mcjty.enigma.parser.StringPointer;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.StringUtils;

public class CmdEval extends CommandBase {
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
            ITextComponent component = new TextComponentString(TextFormatting.RED + "Expression missing!");
            if (sender instanceof EntityPlayer) {
                ((EntityPlayer) sender).sendStatusMessage(component, false);
            } else {
                sender.sendMessage(component);
            }
            return;
        }
        Progress progress = ProgressHolder.getProgress(server.getEntityWorld());
        String expression = StringUtils.join(args, ' ');
        try {
            ParsedExpression<EnigmaFunctionContext> parsed = ExpressionParser.eval(new StringPointer(expression), new EnigmaExpressionContext(progress));
            Object result = parsed.getExpression().eval(new EnigmaFunctionContext(server.getEntityWorld(), (EntityPlayer) sender));
            ITextComponent component = new TextComponentString(TextFormatting.GREEN + "Result: " + result);
            if (sender instanceof EntityPlayer) {
                ((EntityPlayer) sender).sendStatusMessage(component, false);
            } else {
                sender.sendMessage(component);
            }
        } catch (ExpressionException e) {
            ITextComponent component = new TextComponentString(TextFormatting.RED + "Parse Error: " + e.getMessage());
            if (sender instanceof EntityPlayer) {
                ((EntityPlayer) sender).sendStatusMessage(component, false);
            } else {
                sender.sendMessage(component);
            }
        } catch (ExecutionException e) {
            ITextComponent component = new TextComponentString(TextFormatting.RED + "Exec Error: " + e.getMessage());
            if (sender instanceof EntityPlayer) {
                ((EntityPlayer) sender).sendStatusMessage(component, false);
            } else {
                sender.sendMessage(component);
            }
        }
    }
}
