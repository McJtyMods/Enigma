package mcjty.enigma.parser;

import net.minecraft.world.World;

public interface ExpressionFunction {
    Object eval(World world, Object o);
}
