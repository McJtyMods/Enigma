package mcjty.enigma.code;

import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Scope {

    private final List<ActionBlock> onStart = new ArrayList<>();
    private final List<Pair<ActionBlock, Expression>> onDelay = new ArrayList<>();
    private final List<Pair<ActionBlock, Expression>> onRightClickBlock = new ArrayList<>();
    private final List<Pair<ActionBlock, Expression>> onLeftClickBlock = new ArrayList<>();
    private final List<Scope> nestedScopes = new ArrayList<>();

    private Boolean active = null;      // If null then active state is not known

    private Expression condition;

    public void forActiveScopes(World world, Consumer<Scope> consumer) {
        Progress progress = ProgressHolder.getProgress(world);
        for (Scope scope : nestedScopes) {
            if (scope.isActive(world)) {
                consumer.accept(scope);
                scope.forActiveScopes(world, consumer);
            }
        }
    }

    // Make sure this scope is active. This does not check the condition:
    // It is assumed the condition is valid
    private void activate(World world) {
        if (active != null && active) {
            return;
        }

        for (Scope nestedScope : nestedScopes) {
            nestedScope.active = false;             // Set known state to not active so that on activation we call 'onStart'
        }
        if (active != null) {
            // If we didn't know our state then we don't call 'start' because then we are just loading
            // from start
            start(world);
            System.out.println("Scope.activate");
        }
        active = true;

        // Possibly activate children
        for (Scope scope : nestedScopes) {
            if (scope.isActive(world)) {
                scope.activate(world);
            }
        }
    }

    // Deactivate this scope. This does not check the condition
    private void deactivate(World world) {
        if (active != null && !active) {
            return;
        }

        // Possibly deactivate children
        for (Scope scope : nestedScopes) {
            scope.deactivate(world);
        }

        if (active != null) {
            // If we didn't know our state then we don't call 'stop' because then we are just loading
            // from start
            stop(world);
            System.out.println("Scope.deactivate");
        }
        active = false;
    }

    public void checkActivity(World world) {
        if (isActive(world)) {
            activate(world);
            for (Scope scope : nestedScopes) {
                scope.checkActivity(world);
            }
        } else {
            deactivate(world);
        }
    }

    public boolean isActive(World world) {
        return condition == null || ObjectTools.asBoolSafe(condition.eval(world));
    }

    public void setCondition(Expression condition) {
        this.condition = condition;
    }

    public List<Scope> getNestedScopes() {
        return nestedScopes;
    }

    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event, @Nonnull Integer position) {
        EntityPlayer player = event.getEntityPlayer();
        for (Pair<ActionBlock, Expression> pair : onRightClickBlock) {
            World world = player.getEntityWorld();
            if (position.equals(pair.getValue().eval(world))) {
                pair.getKey().execute(world, player);
            }
        }
    }

    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event, @Nonnull Integer position) {
        EntityPlayer player = event.getEntityPlayer();
        for (Pair<ActionBlock, Expression> pair : onLeftClickBlock) {
            World world = player.getEntityWorld();
            if (position.equals(pair.getValue().eval(world))) {
                pair.getKey().execute(world, player);
            }
        }
    }

    private void start(World world) {
        for (ActionBlock actionBlock : onStart) {
            actionBlock.execute(world, null);
        }
    }

    private void stop(World world) {
//        for (ActionBlock actionBlock : onStart) {
//            actionBlock.execute(world, null);
//        }
    }

    public void addOnStart(ActionBlock actionBlock) {
        onStart.add(actionBlock);
    }

    public void addOnDelay(ActionBlock actionBlock, Expression delayPar) {
        onDelay.add(Pair.of(actionBlock, delayPar));
    }

    public void addOnRightClickBlock(ActionBlock actionBlock, Expression position) {
        onRightClickBlock.add(Pair.of(actionBlock, position));
    }

    public void addOnLeftClickBlock(ActionBlock actionBlock, Expression position) {
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
        for (Pair<ActionBlock, Expression> pair : onDelay) {
            System.out.println(StringUtils.repeat(' ', indent+4) + "On Delay (" + pair.getValue() + "):");
            pair.getKey().dump(indent+4);
        }
        for (Pair<ActionBlock, Expression> pair : onRightClickBlock) {
            System.out.println(StringUtils.repeat(' ', indent+4) + "On Right Click Block (" + pair.getValue() + "):");
            pair.getKey().dump(indent+4);
        }
        for (Pair<ActionBlock, Expression> pair : onLeftClickBlock) {
            System.out.println(StringUtils.repeat(' ', indent+4) + "On Left Click Block (" + pair.getValue() + "):");
            pair.getKey().dump(indent+4);
        }
        for (Scope scope : nestedScopes) {
            scope.dump(indent+4);
        }
    }
}
