package mcjty.enigma.progress.serializers;

import mcjty.enigma.progress.NBTData;
import net.minecraft.nbt.NBTTagCompound;

import static mcjty.enigma.varia.StringRegister.STRINGS;

public class StateSerializer implements NBTData<Integer, Integer> {
    @Override
    public Integer getKey(NBTTagCompound tag) {
        return STRINGS.get(tag.getString("s"));
    }

    @Override
    public Integer getValue(NBTTagCompound tag) {
        return STRINGS.get(tag.getString("v"));
    }

    @Override
    public void serialize(NBTTagCompound tc, Integer key, Integer value) {
        tc.setString("s", STRINGS.get(key));
        tc.setString("v", STRINGS.get(value));
    }
}
