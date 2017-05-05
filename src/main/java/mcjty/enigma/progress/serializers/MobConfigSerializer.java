package mcjty.enigma.progress.serializers;

import mcjty.enigma.progress.MobConfig;
import mcjty.enigma.progress.NBTData;
import net.minecraft.nbt.NBTTagCompound;

import static mcjty.enigma.varia.StringRegister.STRINGS;

public class MobConfigSerializer implements NBTData<Integer, MobConfig> {
    @Override
    public Integer getKey(NBTTagCompound tag) {
        return STRINGS.get(tag.getString("s"));
    }

    @Override
    public MobConfig getValue(NBTTagCompound tag) {
        String mob = tag.getString("mob");
        Double hp = null;
        if (tag.hasKey("hp")) {
            hp = tag.getDouble("hp");
        }
        String namedItem = null;
        if (tag.hasKey("item")) {
            namedItem = tag.getString("item");
        }
        return new MobConfig(mob, hp, namedItem);
    }

    @Override
    public void serialize(NBTTagCompound tc, Integer key, MobConfig value) {
        tc.setString("s", STRINGS.get(key));
        tc.setString("mob", value.getMobId());
        if (value.getHp() != null) {
            tc.setDouble("hp", value.getHp());
        }
        if (value.getNamedItem() != null) {
            tc.setString("item", value.getNamedItem());
        }
    }
}
