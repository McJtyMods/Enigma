package mcjty.enigma.code.actions;

import mcjty.enigma.code.*;
import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CallAction extends Action {
    @Nonnull Expression<EnigmaFunctionContext> actionBlockName;

    public CallAction(Expression<EnigmaFunctionContext> actionBlockName) {
        this.actionBlockName = actionBlockName;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "Call: " + actionBlockName);
    }

    @Override
    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        Object e = actionBlockName.eval(context);
        ScopeInstance scopeInstance = context.getScopeInstance();
        if (scopeInstance == null) {
            scopeInstance = RootScope.getRootInstance(context.getWorld());
        }
        ActionBlock block = scopeInstance.getScope().findNamedBlock(ObjectTools.asStringSafe(e));
        if (block == null) {
            throw new ExecutionException("Cannot find code block with name: " + e);
        }
        block.execute(context);
    }
}
