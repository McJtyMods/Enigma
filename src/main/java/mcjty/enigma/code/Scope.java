package mcjty.enigma.code;

import mcjty.enigma.network.EnigmaMessages;
import mcjty.enigma.network.PacketAddMessage;
import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import mcjty.enigma.varia.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static mcjty.enigma.varia.StringRegister.STRINGS;

public class Scope {

    private final ScopeID id;
    private Scope parent;

    private final List<ActionBlock> onActivate = new ArrayList<>();
    private final List<ActionBlock> onDeactivate = new ArrayList<>();
    private final List<ActionBlock> onInit = new ArrayList<>();
    private final List<ActionBlock> onSetup = new ArrayList<>();
    private final List<ActionBlock> onLogin = new ArrayList<>();
    private final List<DelayedAction> onDelay = new ArrayList<>();
    private final List<Pair<ActionBlock, Expression<EnigmaFunctionContext>>> onRightClickItem = new ArrayList<>();
    private final List<Pair<ActionBlock, Expression<EnigmaFunctionContext>>> onRightClickBlock = new ArrayList<>();
    private final List<Pair<ActionBlock, Expression<EnigmaFunctionContext>>> onRightClickPosition = new ArrayList<>();
    private final List<Pair<ActionBlock, Expression<EnigmaFunctionContext>>> onLeftClickBlock = new ArrayList<>();
    private final List<Pair<ActionBlock, Expression<EnigmaFunctionContext>>> onLeftClickPosition = new ArrayList<>();
    private final List<ActionBlock> onDeath = new ArrayList<>();
    private final List<Scope> nestedScopes = new ArrayList<>();
    private final List<Scope> nestedPlayerScopes = new ArrayList<>();
    private final Map<String, ActionBlock> namedActionBlocks = new HashMap<>();

    public static class DelayedAction {
        private final ActionBlock actionBlock;
        private final Expression<EnigmaFunctionContext> delay;
        private final boolean repeating;

        public DelayedAction(ActionBlock actionBlock, Expression<EnigmaFunctionContext> delay, boolean repeating) {
            this.actionBlock = actionBlock;
            this.delay = delay;
            this.repeating = repeating;
        }

        public ActionBlock getActionBlock() {
            return actionBlock;
        }

        public Expression<EnigmaFunctionContext> getDelay() {
            return delay;
        }

        public boolean isRepeating() {
            return repeating;
        }
    }

    private Expression<EnigmaFunctionContext> condition;

    public Scope(ScopeID id) {
        this.id = id;
    }

    public ScopeID getId() {
        return id;
    }

    public void addNamedActionBlock(String name, ActionBlock actionBlock) {
        namedActionBlocks.put(name, actionBlock);
    }

    public ActionBlock findNamedBlock(String name) {
        Scope other = this;
        while (true) {
            ActionBlock block = other.namedActionBlocks.get(name);
            if (block == null && other.parent != null) {
                other = other.parent;
                continue;
            }
            return block;
        }
    }


    public boolean isScopeConditionTrue(EnigmaFunctionContext context) throws ExecutionException {
        return condition == null || ObjectTools.asBoolSafe(condition.eval(context));
    }

    public void setCondition(Expression<EnigmaFunctionContext> condition) {
        this.condition = condition;
    }

    public List<DelayedAction> getOnDelay() {
        return onDelay;
    }

    public List<Scope> getNestedScopes() {
        return nestedScopes;
    }

    public List<Scope> getNestedPlayerScopes() {
        return nestedPlayerScopes;
    }

    public void onRightClickItem(PlayerInteractEvent.RightClickItem event, EnigmaFunctionContext context, @Nonnull ItemStack stack) {
        try {
            for (Pair<ActionBlock, Expression<EnigmaFunctionContext>> pair : onRightClickItem) {
                Object nameditem = pair.getValue().eval(context);
                Progress progress = ProgressHolder.getProgress(context.getWorld());
                ItemStack namedItemStack = progress.getNamedItemStack(nameditem);
                if (InventoryHelper.stackEqualExact(stack, namedItemStack))
                    pair.getKey().execute(context);
            }
        } catch (ExecutionException e) {
            handleException(e);
        }
    }

    public void onRightClickPosition(PlayerInteractEvent.RightClickBlock event, EnigmaFunctionContext context, @Nonnull Integer position) {
        try {
            for (Pair<ActionBlock, Expression<EnigmaFunctionContext>> pair : onRightClickPosition) {
                if (ObjectTools.equals(position, pair.getValue().eval(context))) {
                    pair.getKey().execute(context);
                }
            }
        } catch (ExecutionException e) {
            handleException(e);
        }
    }

    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event, EnigmaFunctionContext context, @Nonnull Integer blockName) {
        try {
            for (Pair<ActionBlock, Expression<EnigmaFunctionContext>> pair : onRightClickBlock) {
                if (ObjectTools.equals(blockName, pair.getValue().eval(context))) {
                    pair.getKey().execute(context);
                }
            }
        } catch (ExecutionException e) {
            handleException(e);
        }
    }

    public void onLeftClickPosition(PlayerInteractEvent.LeftClickBlock event, EnigmaFunctionContext context, @Nonnull Integer position) {
        try {
            for (Pair<ActionBlock, Expression<EnigmaFunctionContext>> pair : onLeftClickPosition) {
                if (ObjectTools.equals(position, pair.getValue().eval(context))) {
                    pair.getKey().execute(context);
                }
            }
        } catch (ExecutionException e) {
            handleException(e);
        }
    }

    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event, EnigmaFunctionContext context, @Nonnull Integer blockName) {
        try {
            for (Pair<ActionBlock, Expression<EnigmaFunctionContext>> pair : onLeftClickBlock) {
                if (ObjectTools.equals(blockName, pair.getValue().eval(context))) {
                    pair.getKey().execute(context);
                }
            }
        } catch (ExecutionException e) {
            handleException(e);
        }
    }

    private void performActions(EnigmaFunctionContext context, List<ActionBlock> actions) {
        try {
            for (ActionBlock block : actions) {
                block.execute(context);
            }
        } catch (ExecutionException e) {
            handleException(e);
        }
    }

    public void onPlayerDeath(LivingDeathEvent event, EnigmaFunctionContext context) {
        performActions(context, onDeath);
    }

    public void onLogin(EnigmaFunctionContext context) {
        performActions(context, onLogin);
    }

    public void onActivate(EnigmaFunctionContext context) {
        performActions(context, onActivate);
    }

    public void onInit(EnigmaFunctionContext context) {
        performActions(context, onInit);
    }

    public void onSetup(EnigmaFunctionContext context) {
        performActions(context, onSetup);
    }

    public void onDeactivate(EnigmaFunctionContext context) {
        performActions(context, onDeactivate);
    }

    public void addOnActivate(ActionBlock actionBlock) {
        onActivate.add(actionBlock);
    }

    public void addOnDeactivate(ActionBlock actionBlock) {
        onDeactivate.add(actionBlock);
    }

    public void addOnInit(ActionBlock actionBlock) {
        onInit.add(actionBlock);
    }

    public void addOnSetup(ActionBlock actionBlock) {
        onSetup.add(actionBlock);
    }

    public void addOnDelay(ActionBlock actionBlock, Expression<EnigmaFunctionContext> delayPar, boolean repeating) {
        onDelay.add(new DelayedAction(actionBlock, delayPar, repeating));
    }

    public void addOnLogin(ActionBlock actionBlock) {
        onLogin.add(actionBlock);
    }

    public void addOnRightClickItem(ActionBlock actionBlock, Expression<EnigmaFunctionContext> item) {
        onRightClickItem.add(Pair.of(actionBlock, item));
    }

    public void addOnRightClickPosition(ActionBlock actionBlock, Expression<EnigmaFunctionContext> position) {
        onRightClickPosition.add(Pair.of(actionBlock, position));
    }

    public void addOnRightClickBlock(ActionBlock actionBlock, Expression<EnigmaFunctionContext> block) {
        onRightClickBlock.add(Pair.of(actionBlock, block));
    }

    public void addOnLeftClickPosition(ActionBlock actionBlock, Expression<EnigmaFunctionContext> position) {
        onLeftClickPosition.add(Pair.of(actionBlock, position));
    }

    public void addOnLeftClickBlock(ActionBlock actionBlock, Expression<EnigmaFunctionContext> block) {
        onLeftClickBlock.add(Pair.of(actionBlock, block));
    }

    public void addOnDeath(ActionBlock actionBlock) {
        onDeath.add(actionBlock);
    }

    public void setParentScope(Scope parent) {
        this.parent = parent;
    }

    public void addScope(Scope scope) {
        nestedScopes.add(scope);
        scope.setParentScope(this);
    }

    public void addPlayerScope(Scope scope) {
        nestedPlayerScopes.add(scope);
        scope.setParentScope(this);
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
        for (ActionBlock block : onDeactivate) {
            System.out.println(StringUtils.repeat(' ', indent+4) + "On Deactivate:");
            block.dump(indent+4);
        }
        for (DelayedAction action : onDelay) {
            System.out.println(StringUtils.repeat(' ', indent+4) + "On Delay:");
            action.actionBlock.dump(indent+4);
        }
        for (Pair<ActionBlock, Expression<EnigmaFunctionContext>> pair : onRightClickBlock) {
            System.out.println(StringUtils.repeat(' ', indent+4) + "On Right Click Block (" + pair.getValue() + "):");
            pair.getKey().dump(indent+4);
        }
        for (Pair<ActionBlock, Expression<EnigmaFunctionContext>> pair : onRightClickPosition) {
            System.out.println(StringUtils.repeat(' ', indent+4) + "On Right Click Position (" + pair.getValue() + "):");
            pair.getKey().dump(indent+4);
        }
        for (Pair<ActionBlock, Expression<EnigmaFunctionContext>> pair : onLeftClickBlock) {
            System.out.println(StringUtils.repeat(' ', indent+4) + "On Left Click Block (" + pair.getValue() + "):");
            pair.getKey().dump(indent+4);
        }
        for (Pair<ActionBlock, Expression<EnigmaFunctionContext>> pair : onLeftClickPosition) {
            System.out.println(StringUtils.repeat(' ', indent+4) + "On Left Click Position (" + pair.getValue() + "):");
            pair.getKey().dump(indent+4);
        }
        for (Scope scope : nestedScopes) {
            scope.dump(indent+4);
        }
        for (Scope scope : nestedPlayerScopes) {
            scope.dump(indent+4);
        }
    }

    public static void handleException(ExecutionException e) {
        e.printStackTrace();
        if (e.getCause() != null) {
            e.getCause().printStackTrace();
        }
        EnigmaMessages.INSTANCE.sendToAll(new PacketAddMessage(TextFormatting.RED + e.getMessage(), 400));
    }
}
