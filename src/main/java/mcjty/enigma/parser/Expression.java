package mcjty.enigma.parser;

import net.minecraft.world.World;

public interface Expression {

    Object eval(World world);

}
