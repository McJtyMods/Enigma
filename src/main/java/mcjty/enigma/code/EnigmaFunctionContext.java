package mcjty.enigma.code;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import static mcjty.enigma.varia.StringRegister.STRINGS;

public class EnigmaFunctionContext {
    @Nullable private final World world;
    @Nullable private final EntityPlayer player;
    private boolean canceled = false;
    @Nonnull private final Map<Integer, Object> localVariables = new HashMap<>();
    @Nullable private ScopeInstance scopeInstance = null;

    public static final EnigmaFunctionContext EMPTY = new EnigmaFunctionContext(null, null);

    public EnigmaFunctionContext(@Nullable World world, @Nullable EntityPlayer player) {
        this.world = world;
        this.player = player;
    }

    public void setLocalVar(Integer name, Object val) {
        localVariables.put(name, val);
    }

    public void setLocalVar(String name, Object val) {
        localVariables.put(STRINGS.get(name), val);
    }

    public Object getLocalVar(String name) {
        return localVariables.get(STRINGS.get(name));
    }

    public Object getLocalVar(Integer name) {
        return localVariables.get(name);
    }

    public boolean isLocalVar(String name) {
        return localVariables.containsKey(STRINGS.get(name));
    }

    public boolean isLocalVar(Integer name) {
        return localVariables.containsKey(name);
    }

    @Nullable
    public World getWorld() {
        return world;
    }

    @Nullable
    public EntityPlayer getPlayer() { return player; }

    public boolean hasPlayer() {
        return player != null;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public void setScopeInstance(@Nullable ScopeInstance scopeInstance) {
        this.scopeInstance = scopeInstance;
    }

    @Nullable
    public ScopeInstance getScopeInstance() {
        return scopeInstance;
    }
}
