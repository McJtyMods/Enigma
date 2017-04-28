package mcjty.enigma.code;

import mcjty.enigma.Enigma;
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
    public void execute(EnigmaFunctionContext context) {
        Enigma.logger.info(message.eval(context));
    }
}
