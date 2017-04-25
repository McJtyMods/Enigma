package mcjty.enigma.code;

import net.minecraft.entity.player.EntityPlayer;

public abstract class Action {
    public void dump(int indent) {
    }

    public abstract void execute(EntityPlayer player);
}
