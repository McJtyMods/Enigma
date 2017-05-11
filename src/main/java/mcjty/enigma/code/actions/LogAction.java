package mcjty.enigma.code.actions;

import mcjty.enigma.Enigma;
import mcjty.enigma.code.Action;
import mcjty.enigma.code.EnigmaFunctionContext;
import mcjty.enigma.code.ExecutionException;
import mcjty.enigma.parser.Expression;
import org.apache.commons.lang3.StringUtils;

public class LogAction extends Action {
    private final Expression<EnigmaFunctionContext> message;

    public LogAction(Expression<EnigmaFunctionContext> message) {
        this.message = message;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "Log: " + message);

    }

    @Override
    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        Enigma.logger.info(message.eval(context));
    }
}
