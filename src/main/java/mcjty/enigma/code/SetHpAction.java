package mcjty.enigma.code;

import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
import org.apache.commons.lang3.StringUtils;

public class SetHpAction extends Action {
    private final Expression<EnigmaFunctionContext> hp;

    public SetHpAction(Expression<EnigmaFunctionContext> hp) {
        this.hp = hp;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "SetHP: " + hp);

    }

    @Override
    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        checkPlayer(context);
        double hp = ObjectTools.asIntSafe(this.hp.eval(context));
        context.getPlayer().setHealth((float) hp);
    }
}
