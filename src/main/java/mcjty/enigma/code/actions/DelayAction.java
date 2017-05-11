package mcjty.enigma.code.actions;

import mcjty.enigma.code.*;
import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;

public class DelayAction extends Action {
    @Nonnull private final Expression<EnigmaFunctionContext> ticks;
    @Nonnull private final ActionBlock actionBlock;

    public DelayAction(Expression<EnigmaFunctionContext> ticks, ActionBlock actionBlock) {
        this.ticks = ticks;
        this.actionBlock = actionBlock;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "Delay");
        actionBlock.dump(indent);
    }

    @Override
    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        ScopeInstance scope = context.getScopeInstance();
        if (scope == null) {
            scope = RootScope.getRootInstance(context.getWorld());
        }
        scope.addTimedAction(new ScopeInstance.TimedAction(actionBlock, ticks, scope.getTicker() + ObjectTools.asIntSafe(ticks.eval(context)), false));
    }
}
