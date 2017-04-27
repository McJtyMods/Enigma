package mcjty.enigma.code;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EnigmaFunctionContext {
    @Nonnull private final World world;
    @Nullable private final EntityPlayer player;

    public EnigmaFunctionContext(@Nonnull World world, @Nullable EntityPlayer player) {
        this.world = world;
        this.player = player;
    }

    @Nonnull
    public World getWorld() {
        return world;
    }

    @Nullable
    public EntityPlayer getPlayer() { return player; }

    public boolean hasPlayer() {
        return player != null;
    }
}
