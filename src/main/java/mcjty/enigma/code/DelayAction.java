package mcjty.enigma.code;

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
        ScopeInstance root = RootScope.getRootInstance(context.getWorld());
        root.addTimedAction(new ScopeInstance.TimedAction(actionBlock, ticks, root.getTicker() + ObjectTools.asIntSafe(ticks.eval(context)), false));
    }
}
