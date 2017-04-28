package mcjty.enigma.code;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class EnigmaFunctionContext {
    @Nullable private final World world;
    @Nullable private final EntityPlayer player;
    private boolean canceled = false;

    public static final EnigmaFunctionContext EMPTY = new EnigmaFunctionContext(null, null);

    public EnigmaFunctionContext(@Nullable World world, @Nullable EntityPlayer player) {
        this.world = world;
        this.player = player;
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
}
