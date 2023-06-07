package mcjty.enigma.code.actions;

import mcjty.enigma.code.*;
import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;

public class ForAction extends Action {
    @Nonnull private final Expression<EnigmaFunctionContext> varname;
    @Nonnull private final Expression<EnigmaFunctionContext> start;
    @Nonnull private final Expression<EnigmaFunctionContext> end;
    @Nonnull private final Expression<EnigmaFunctionContext> actionBlockName;

    public ForAction(Expression<EnigmaFunctionContext> varname,
                     Expression<EnigmaFunctionContext> start,
                     Expression<EnigmaFunctionContext> end,
                     Expression<EnigmaFunctionContext> actionBlockName) {
        this.varname = varname;
        this.start = start;
        this.end = end;
        this.actionBlockName = actionBlockName;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "For: " + varname + " from " + start + " to " + end + " -> " + actionBlockName);
    }

    @Override
    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        String var = ObjectTools.asStringSafe(varname.eval(context));
        int f1 = ObjectTools.asIntSafe(start.eval(context));
        int f2 = ObjectTools.asIntSafe(end.eval(context));

        Object e = actionBlockName.eval(context);
        ScopeInstance scopeInstance = context.getScopeInstance();
        if (scopeInstance == null) {
            scopeInstance = RootScope.getRootInstance(context.getWorld());
        }
        ActionBlock block = scopeInstance.getScope().findNamedBlock(ObjectTools.asStringSafe(e));
        if (block == null) {
            throw new ExecutionException("Cannot find code block with name: " + e);
        }
        for (int i = f1 ; i < f2 ; i++) {
            context.setLocalVar(var, i);
            block.execute(context);
        }
    }
}
