package mcjty.enigma.code.actions;

import mcjty.enigma.code.Action;
import mcjty.enigma.code.ActionBlock;
import mcjty.enigma.code.EnigmaFunctionContext;
import mcjty.enigma.code.ExecutionException;
import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class IfAction extends Action {
    @Nonnull private final Expression<EnigmaFunctionContext> condition;
    @Nullable private final ActionBlock positiveBlock;
    @Nullable private final ActionBlock negativeBlock;

    public IfAction(Expression<EnigmaFunctionContext> condition, ActionBlock positiveBlock, ActionBlock negativeBlock) {
        this.condition = condition;
        this.positiveBlock = positiveBlock;
        this.negativeBlock = negativeBlock;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "If");
        if (positiveBlock != null) {
            positiveBlock.dump(indent);
        }
        if (negativeBlock != null) {
            System.out.println(StringUtils.repeat(' ', indent) + "Else");
            negativeBlock.dump(indent);
        }
    }

    @Override
    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        if (ObjectTools.asBoolSafe(condition.eval(context))) {
            if (positiveBlock != null) {
                positiveBlock.execute(context);
            }
        } else {
            if (negativeBlock != null) {
                negativeBlock.execute(context);
            }
        }
    }
}
