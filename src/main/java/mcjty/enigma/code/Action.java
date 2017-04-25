package mcjty.enigma.code;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public abstract class Action {
    public void dump(int indent) {
    }

    public abstract void execute(World world, EntityPlayer player);
}
