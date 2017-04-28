package mcjty.enigma.code;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EnigmaFunctionContext {
    @Nullable private final World world;
    @Nullable private final EntityPlayer player;

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
}
