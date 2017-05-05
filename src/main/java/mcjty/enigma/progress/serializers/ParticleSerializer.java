package mcjty.enigma.progress.serializers;

import mcjty.enigma.progress.NBTData;
import mcjty.enigma.progress.ParticleConfig;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;

import static mcjty.enigma.varia.StringRegister.STRINGS;

public class ParticleSerializer implements NBTData<Integer, ParticleConfig> {
    @Override
    public Integer getKey(NBTTagCompound tag) {
        return STRINGS.get(tag.getString("s"));
    }

    @Override
    public ParticleConfig getValue(NBTTagCompound tag) {
        String pn = tag.getString("pn");
        int amount = tag.getInteger("a");
        double ox = tag.getDouble("ox");
        double oy = tag.getDouble("oy");
        double oz = tag.getDouble("oz");
        double speed = tag.getDouble("speed");
        return new ParticleConfig(EnumParticleTypes.getByName(pn), amount, ox, oy, oz, speed);
    }

    @Override
    public void serialize(NBTTagCompound tc, Integer key, ParticleConfig value) {
        tc.setString("s", STRINGS.get(key));
        tc.setString("pn", value.getParticles().getParticleName());
        tc.setInteger("a", value.getAmount());
        tc.setDouble("ox", value.getOffsetX());
        tc.setDouble("oy", value.getOffsetY());
        tc.setDouble("oz", value.getOffsetZ());
        tc.setDouble("speed", value.getSpeed());
    }
}
