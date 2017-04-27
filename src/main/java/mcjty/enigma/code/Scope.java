package mcjty.enigma.code;

import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Scope {

    private final List<ActionBlock> onStart = new ArrayList<>();
    private final List<Pair<ActionBlock, Expression<EnigmaFunctionContext>>> onDelay = new ArrayList<>();
    private final List<Pair<ActionBlock, Expression<EnigmaFunctionContext>>> onRightClickBlock = new ArrayList<>();
    private final List<Pair<ActionBlock, Expression<EnigmaFunctionContext>>> onLeftClickBlock = new ArrayList<>();
    private final List<Scope> nestedScopes = new ArrayList<>();

    private Boolean active = null;      // If null then active state is not known

    private Expression<EnigmaFunctionContext> condition;

    public void forActiveScopes(EnigmaFunctionContext context, Consumer<Scope> consumer) {
        Progress progress = ProgressHolder.getProgress(context.getWorld());
        for (Scope scope : nestedScopes) {
            if (scope.isActive(context)) {
                consumer.accept(scope);
                scope.forActiveScopes(context, consumer);
            }
        }
    }

    // ONLY for root!
    public void setInactiveForRoot() {
        active = false;
    }

    // Make sure this scope is active. This does not check the condition:
    // It is assumed the condition is valid
    private void activate(EnigmaFunctionContext context) {
        if (active != null && active) {
            return;
        }

        for (Scope nestedScope : nestedScopes) {
            nestedScope.active = false;             // Set known state to not active so that on activation we call 'onStart'
        }
        if (active != null) {
            // If we didn't know our state then we don't call 'start' because then we are just loading
            // from start
            start(context);
            System.out.println("Scope.activate");
        }
        active = true;

        // Possibly activate children
        for (Scope scope : nestedScopes) {
            if (scope.isActive(context)) {
                scope.activate(context);
            }
        }
    }

    // Deactivate this scope. This does not check the condition
    private void deactivate(EnigmaFunctionContext context) {
        if (active != null && !active) {
            return;
        }

        // Possibly deactivate children
        for (Scope scope : nestedScopes) {
            scope.deactivate(context);
        }

        if (active != null) {
            // If we didn't know our state then we don't call 'stop' because then we are just loading
            // from start
            stop(context);
            System.out.println("Scope.deactivate");
        }
        active = false;
    }

    public void checkActivity(EnigmaFunctionContext context) {
        if (isActive(context)) {
            activate(context);
            for (Scope scope : nestedScopes) {
                scope.checkActivity(context);
            }
        } else {
            deactivate(context);
        }
    }

    public boolean isActive(EnigmaFunctionContext context) {
        return condition == null || ObjectTools.asBoolSafe(condition.eval(context));
    }

    public void setCondition(Expression<EnigmaFunctionContext> condition) {
        this.condition = condition;
    }

    public List<Scope> getNestedScopes() {
        return nestedScopes;
    }

    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event, EnigmaFunctionContext context, @Nonnull String position) {
        for (Pair<ActionBlock, Expression<EnigmaFunctionContext>> pair : onRightClickBlock) {
            if (position.equals(pair.getValue().eval(context))) {
                pair.getKey().execute(context);
            }
        }
    }

    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event, EnigmaFunctionContext context, @Nonnull String position) {
        for (Pair<ActionBlock, Expression<EnigmaFunctionContext>> pair : onLeftClickBlock) {
            if (position.equals(pair.getValue().eval(context))) {
                pair.getKey().execute(context);
            }
        }
    }

    private void start(EnigmaFunctionContext context) {
        for (ActionBlock actionBlock : onStart) {
            actionBlock.execute(context);
        }
    }

    private void stop(EnigmaFunctionContext context) {
//        for (ActionBlock actionBlock : onStart) {
//            actionBlock.execute(world, null);
//        }
    }

    public void addOnStart(ActionBlock actionBlock) {
        onStart.add(actionBlock);
    }

    public void addOnDelay(ActionBlock actionBlock, Expression<EnigmaFunctionContext> delayPar) {
        onDelay.add(Pair.of(actionBlock, delayPar));
    }

    public void addOnRightClickBlock(ActionBlock actionBlock, Expression<EnigmaFunctionContext> position) {
        onRightClickBlock.add(Pair.of(actionBlock, position));
    }

    public void addOnLeftClickBlock(ActionBlock actionBlock, Expression<EnigmaFunctionContext> position) {
        onLeftClickBlock.add(Pair.of(actionBlock, position));
    }

    public void addScope(Scope scope) {
        nestedScopes.add(scope);
    }

    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "Scope " + ":");
        for (ActionBlock block : onStart) {
            System.out.println(StringUtils.repeat(' ', indent+4) + "On Start:");
            block.dump(indent+4);
        }
        for (Pair<ActionBlock, Expression<EnigmaFunctionContext>> pair : onDelay) {
            System.out.println(StringUtils.repeat(' ', indent+4) + "On Delay (" + pair.getValue() + "):");
            pair.getKey().dump(indent+4);
        }
        for (Pair<ActionBlock, Expression<EnigmaFunctionContext>> pair : onRightClickBlock) {
            System.out.println(StringUtils.repeat(' ', indent+4) + "On Right Click Block (" + pair.getValue() + "):");
            pair.getKey().dump(indent+4);
        }
        for (Pair<ActionBlock, Expression<EnigmaFunctionContext>> pair : onLeftClickBlock) {
            System.out.println(StringUtils.repeat(' ', indent+4) + "On Left Click Block (" + pair.getValue() + "):");
            pair.getKey().dump(indent+4);
        }
        for (Scope scope : nestedScopes) {
            scope.dump(indent+4);
        }
    }
}
