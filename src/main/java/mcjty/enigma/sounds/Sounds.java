package mcjty.enigma.sounds;

import mcjty.enigma.Enigma;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;

public class Sounds {

    private static SoundEvent locking;

    public static void init() {
        locking = registerSound(new ResourceLocation(Enigma.MODID, "locking"));
    }

    private static SoundEvent registerSound(ResourceLocation rl){
        SoundEvent ret = new SoundEvent(rl).setRegistryName(rl);
        ((FMLControlledNamespacedRegistry) SoundEvent.REGISTRY).register(ret);
        return ret;
    }

}
