package mcjty.enigma.code;

import net.minecraft.world.World;

public class EnigmaFunctionContext {
    private final World world;

    public EnigmaFunctionContext(World world) {
        this.world = world;
    }

    public World getWorld() {
        return world;
    }
}
