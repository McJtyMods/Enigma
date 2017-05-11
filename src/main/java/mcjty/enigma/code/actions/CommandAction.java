package mcjty.enigma.code.actions;

import mcjty.enigma.code.Action;
import mcjty.enigma.code.EnigmaFunctionContext;
import mcjty.enigma.code.ExecutionException;
import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
import mcjty.enigma.varia.DummyCommandSender;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.lang3.StringUtils;

public class CommandAction extends Action {
    private final Expression<EnigmaFunctionContext> command;

    public CommandAction(Expression<EnigmaFunctionContext> command) {
        this.command = command;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "Command: " + command);

    }

    @Override
    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        MinecraftServer server = context.getWorld().getMinecraftServer();
//        server.commandManager.executeCommand(new DummyCommandSender(context.getWorld(), context.getPlayer()), ObjectTools.asStringSafe(command.eval(context)));
        server.commandManager.executeCommand(context.hasPlayer() ? context.getPlayer() : new DummyCommandSender(context.getWorld(), context.getPlayer()), ObjectTools.asStringSafe(command.eval(context)));
    }
}
