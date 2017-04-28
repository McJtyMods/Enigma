package mcjty.enigma.code;

import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static mcjty.enigma.varia.StringRegister.STRINGS;

public class Scope {

    private final ScopeID id;

    private final List<ActionBlock> onActivate = new ArrayList<>();
    private final List<ActionBlock> onInit = new ArrayList<>();
    private final List<ActionBlock> onSetup = new ArrayList<>();
    private final List<Pair<ActionBlock, Expression<EnigmaFunctionContext>>> onDelay = new ArrayList<>();
    private final List<Pair<ActionBlock, Expression<EnigmaFunctionContext>>> onRightClickBlock = new ArrayList<>();
    private final List<Pair<ActionBlock, Expression<EnigmaFunctionContext>>> onLeftClickBlock = new ArrayList<>();
    private final List<Scope> nestedScopes = new ArrayList<>();
    private final List<Scope> nestedPlayerScopes = new ArrayList<>();

    private Expression<EnigmaFunctionContext> condition;

    public Scope(ScopeID id) {
        this.id = id;
    }

    public ScopeID getId() {
        return id;
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

    public List<Scope> getNestedPlayerScopes() {
        return nestedPlayerScopes;
    }

    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event, EnigmaFunctionContext context, @Nonnull Integer position) {
        for (Pair<ActionBlock, Expression<EnigmaFunctionContext>> pair : onRightClickBlock) {
            if (ObjectTools.equals(position, pair.getValue().eval(context))) {
                pair.getKey().execute(context);
            }
        }
    }

    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event, EnigmaFunctionContext context, @Nonnull Integer position) {
        for (Pair<ActionBlock, Expression<EnigmaFunctionContext>> pair : onLeftClickBlock) {
            if (ObjectTools.equals(position, pair.getValue().eval(context))) {
                pair.getKey().execute(context);
            }
        }
    }

    public void onActivate(EnigmaFunctionContext context) {
        for (ActionBlock actionBlock : onActivate) {
            actionBlock.execute(context);
        }
    }

    public void onInit(EnigmaFunctionContext context) {
        for (ActionBlock actionBlock : onInit) {
            actionBlock.execute(context);
        }
    }

    public void onSetup(EnigmaFunctionContext context) {
        for (ActionBlock actionBlock : onSetup) {
            actionBlock.execute(context);
        }
    }

    public void onDeactivate(EnigmaFunctionContext context) {
//        for (ActionBlock actionBlock : onStart) {
//            actionBlock.execute(world, null);
//        }
    }

    public void addOnActivate(ActionBlock actionBlock) {
        onActivate.add(actionBlock);
    }

    public void addOnInit(ActionBlock actionBlock) {
        onInit.add(actionBlock);
    }

    public void addOnSetup(ActionBlock actionBlock) {
        onSetup.add(actionBlock);
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

    public void addPlayerScope(Scope scope) {
        nestedPlayerScopes.add(scope);
    }

    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "Scope(" + STRINGS.get(id.getId()) + ") " + ":");
        for (ActionBlock block : onSetup) {
            System.out.println(StringUtils.repeat(' ', indent+4) + "On Setup:");
            block.dump(indent+4);
        }
        for (ActionBlock block : onInit) {
            System.out.println(StringUtils.repeat(' ', indent+4) + "On Init:");
            block.dump(indent+4);
        }
        for (ActionBlock block : onActivate) {
            System.out.println(StringUtils.repeat(' ', indent+4) + "On Activate:");
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
        for (Scope scope : nestedPlayerScopes) {
            scope.dump(indent+4);
        }
    }
}
