package mcjty.enigma.code.actions;

import mcjty.enigma.code.Action;
import mcjty.enigma.code.ActionBlock;
import mcjty.enigma.code.EnigmaFunctionContext;
import mcjty.enigma.code.ExecutionException;
import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;

public class ForAction extends Action {
    @Nonnull private final Expression<EnigmaFunctionContext> varname;
    @Nonnull private final Expression<EnigmaFunctionContext> start;
    @Nonnull private final Expression<EnigmaFunctionContext> end;
    @Nonnull private final ActionBlock actionBlock;

    public ForAction(Expression<EnigmaFunctionContext> varname,
                     Expression<EnigmaFunctionContext> start,
                     Expression<EnigmaFunctionContext> end,
                     ActionBlock actionBlock) {
        this.varname = varname;
        this.start = start;
        this.end = end;
        this.actionBlock = actionBlock;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "For: " + varname + " from " + start + " to " + end);
    }

    @Override
    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        String var = ObjectTools.asStringSafe(varname.eval(context));
        int f1 = ObjectTools.asIntSafe(start.eval(context));
        int f2 = ObjectTools.asIntSafe(end.eval(context));

        for (int i = f1 ; i < f2 ; i++) {
            context.setLocalVar(var, i);
            actionBlock.execute(context);
        }
    }
}
