package mcjty.enigma.code;

import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
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
        server.commandManager.executeCommand(context.getPlayer() != null ? context.getPlayer() : server, ObjectTools.asStringSafe(command.eval(context)));
    }
}
