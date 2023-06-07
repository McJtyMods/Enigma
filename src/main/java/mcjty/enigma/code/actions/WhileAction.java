package mcjty.enigma.code.actions;

import mcjty.enigma.code.Action;
import mcjty.enigma.code.ActionBlock;
import mcjty.enigma.code.EnigmaFunctionContext;
import mcjty.enigma.code.ExecutionException;
import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;

public class WhileAction extends Action {
    @Nonnull private final Expression<EnigmaFunctionContext> expression;
    @Nonnull private final ActionBlock actionBlock;

    public WhileAction(Expression<EnigmaFunctionContext> expression,
                       ActionBlock actionBlock) {
        this.expression = expression;
        this.actionBlock = actionBlock;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "While: " + expression);
    }

    @Override
    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        while (ObjectTools.asBoolSafe(expression.eval(context))) {
            actionBlock.execute(context);
        }
    }
}
