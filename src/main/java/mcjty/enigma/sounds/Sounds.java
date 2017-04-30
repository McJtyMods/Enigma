package mcjty.enigma.sounds;

import mcjty.enigma.Enigma;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;

public class Sounds {

    public static void init() {
        registerSound(new ResourceLocation(Enigma.MODID, "locking"));
        registerSound(new ResourceLocation(Enigma.MODID, "rockbreaking"));
        registerSound(new ResourceLocation(Enigma.MODID, "rockbreaking2"));
        registerSound(new ResourceLocation(Enigma.MODID, "stonedoor"));
    }

    private static SoundEvent registerSound(ResourceLocation rl){
        SoundEvent ret = new SoundEvent(rl).setRegistryName(rl);
        ((FMLControlledNamespacedRegistry) SoundEvent.REGISTRY).register(ret);
        return ret;
    }

}
