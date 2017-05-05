package mcjty.enigma.code;

import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
import mcjty.enigma.progress.PlayerProgress;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import net.minecraft.entity.player.EntityPlayerMP;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.BiConsumer;

public class ScopeInstance {

    private final Scope scope;
    private Map<ScopeID, ScopeInstance> nestedScopeInstances = new HashMap<>();
    private Map<Pair<UUID, ScopeID>, ScopeInstance> nestedPlayerScopeInstances = new HashMap<>();
    private List<TimedAction> timedActions = new ArrayList<>();
    private int ticker = 0;

    public static class TimedAction {
        private final ActionBlock actionBlock;
        private final Expression<EnigmaFunctionContext> delay;
        private final int ticks;
        private final boolean repeating;

        public TimedAction(ActionBlock actionBlock, Expression<EnigmaFunctionContext> delay, int ticks, boolean repeating) {
            this.actionBlock = actionBlock;
            this.delay = delay;
            this.ticks = ticks;
            this.repeating = repeating;
        }

        public ActionBlock getActionBlock() {
            return actionBlock;
        }

        public Expression<EnigmaFunctionContext> getDelay() {
            return delay;
        }

        public int getStopTime() {
            return ticks;
        }

        public boolean isRepeating() {
            return repeating;
        }
    }

    private Boolean active = null;

    public ScopeInstance(Scope scope) {
        this.scope = scope;
    }

    public int getTicker() {
        return ticker;
    }

    public void forActiveScopes(EnigmaFunctionContext context, BiConsumer<EnigmaFunctionContext, Scope> consumer) {
        consumer.accept(context, scope);

        for (ScopeInstance scopeInstance : nestedScopeInstances.values()) {
            if (scopeInstance.isActive()) {
                scopeInstance.forActiveScopes(context, consumer);
            }
        }
        for (Map.Entry<Pair<UUID, ScopeID>, ScopeInstance> pair : nestedPlayerScopeInstances.entrySet()) {
            UUID uuid = pair.getKey().getKey();
            ScopeInstance scopeInstance = pair.getValue();
            if (scopeInstance.isActive()) {
                EntityPlayerMP player = context.getWorld().getMinecraftServer().getPlayerList().getPlayerByUUID(uuid);
                if (player == null) {
                    throw new RuntimeException("What? Player is missing!");
                }
                if (scopeInstance.isActive()) {
                    EnigmaFunctionContext newctxt = new EnigmaFunctionContext(context.getWorld(), player);
                    scopeInstance.forActiveScopes(newctxt, consumer);
                    if (newctxt.isCanceled()) {
                        context.setCanceled(true);
                    }
                }
            }
        }
    }

    // Make sure this scope is active. This does not check the condition:
    // It is assumed the condition is valid
    private void activate(EnigmaFunctionContext context) {
        if (active != null && active) {
            return;
        }

        // Because we were not active before we know that we can force-set all nested
        // scopes to be inactive too (or unknown)
        for (ScopeInstance scopeInstance : nestedScopeInstances.values()) {
            scopeInstance.setActive(active);
        }
        for (ScopeInstance scopeInstance : nestedPlayerScopeInstances.values()) {
            scopeInstance.setActive(active);
        }

        if (active != null) {
            // If we didn't know our state then we don't call 'start' because then we are just loading
            // from start
            scope.onActivate(context);
            System.out.println("Scope.activate");
        }

        clearTimers();
        // Install scope specific timers
        ticker = 0;
        for (Scope.DelayedAction action : scope.getOnDelay()) {
            int ticks = ObjectTools.asIntSafe(action.getDelay().eval(context));
            timedActions.add(new TimedAction(action.getActionBlock(), action.getDelay(), ticks, action.isRepeating()));
        }


        // Possibly activate children
        for (Scope child : scope.getNestedScopes()) {
            if (child.isScopeConditionTrue(context)) {
                ScopeInstance scopeInstance = findScopeInstance(child, context);
                scopeInstance.setActive(active);
                scopeInstance.activate(context);
            }
        }
        if (!scope.getNestedPlayerScopes().isEmpty()) {
            for (EntityPlayerMP player : context.getWorld().getMinecraftServer().getPlayerList().getPlayers()) {
                EnigmaFunctionContext newctxt = new EnigmaFunctionContext(context.getWorld(), player);
                for (Scope child : scope.getNestedPlayerScopes()) {
                    if (child.isScopeConditionTrue(newctxt)) {
                        ScopeInstance scopeInstance = findScopeInstance(player.getPersistentID(), child, newctxt);
                        scopeInstance.setActive(active);
                        scopeInstance.activate(newctxt);
                    }
                }
            }
        }

        active = true;
    }

    private void clearTimers() {
        timedActions.clear();
    }

    public void addTimedAction(TimedAction action) {
        timedActions.add(action);
    }

    // Deactivate this scope. This does not check the condition
    private void deactivate(EnigmaFunctionContext context) {
        if (active != null && !active) {
            return;
        }

        // Possibly deactivate children
        for (ScopeInstance scopeInstance : nestedScopeInstances.values()) {
            scopeInstance.deactivate(context);
        }
        for (Map.Entry<Pair<UUID, ScopeID>, ScopeInstance> entry : nestedPlayerScopeInstances.entrySet()) {
            UUID key = entry.getKey().getKey();
            EntityPlayerMP player = context.getWorld().getMinecraftServer().getPlayerList().getPlayerByUUID(key);
            ScopeInstance scopeInstance = entry.getValue();
            EnigmaFunctionContext newctxt = new EnigmaFunctionContext(context.getWorld(), player);
            scopeInstance.deactivate(newctxt);
        }
        clearTimers();

        if (active != null) {
            // If we didn't know our state then we don't call 'stop' because then we are just loading
            // from start
            scope.onDeactivate(context);
            System.out.println("Scope.deactivate");
        }
        active = false;
    }

    public void checkActivity(EnigmaFunctionContext context) {
        if (scope.isScopeConditionTrue(context)) {
            activate(context);
            ticker++;

            for (Scope child : scope.getNestedScopes()) {
                ScopeInstance scopeInstance = findScopeInstance(child, context);
                scopeInstance.checkActivity(context);
            }
            if (!scope.getNestedPlayerScopes().isEmpty()) {
                for (Scope child : scope.getNestedPlayerScopes()) {
                    for (EntityPlayerMP player : context.getWorld().getMinecraftServer().getPlayerList().getPlayers()) {
                        EnigmaFunctionContext newctxt = new EnigmaFunctionContext(context.getWorld(), player);
                        ScopeInstance scopeInstance = findScopeInstance(player.getPersistentID(), child, newctxt);
                        scopeInstance.checkActivity(newctxt);
                    }
                }
            }
            int t = ticker;
            List<TimedAction> newActions = new ArrayList<>(timedActions.size());
            for (TimedAction action : timedActions) {
                if (t >= action.getStopTime()) {
                    action.getActionBlock().execute(context);
                    if (action.isRepeating()) {
                        int ms = ObjectTools.asIntSafe(action.getDelay().eval(context));
                        newActions.add(new TimedAction(action.getActionBlock(), action.getDelay(), t + ms, action.isRepeating()));
                    }
                } else {
                    newActions.add(action);
                }
            }
            timedActions = newActions;

        } else {
            deactivate(context);
        }
    }

    @Nonnull
    private ScopeInstance findScopeInstance(Scope child, EnigmaFunctionContext context) {
        ScopeInstance scopeInstance = nestedScopeInstances.get(child.getId());
        if (scopeInstance == null) {
            scopeInstance = new ScopeInstance(child);
            child.onInit(context);
            Progress progress = ProgressHolder.getProgress(context.getWorld());
            if (!progress.isScopeInitialized(child.getId())) {
                child.onSetup(context);
                progress.setScopeInitialized(child.getId());
                ProgressHolder.save(context.getWorld());
            }
            nestedScopeInstances.put(child.getId(), scopeInstance);
        }
        return scopeInstance;
    }

    @Nonnull
    private ScopeInstance findScopeInstance(UUID uuid, Scope child, EnigmaFunctionContext context) {
        ScopeInstance scopeInstance = nestedPlayerScopeInstances.get(Pair.of(uuid, child.getId()));
        if (scopeInstance == null) {
            scopeInstance = new ScopeInstance(child);
            child.onInit(context);
            Progress progress = ProgressHolder.getProgress(context.getWorld());
            PlayerProgress playerProgress = progress.getPlayerProgress(uuid);
            if (!playerProgress.isScopeInitialized(child.getId())) {
                child.onSetup(context);
                playerProgress.setScopeInitialized(child.getId());
                ProgressHolder.save(context.getWorld());
            }
            nestedPlayerScopeInstances.put(Pair.of(uuid, child.getId()), scopeInstance);
        }
        return scopeInstance;
    }

    public Scope getScope() {
        return scope;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
