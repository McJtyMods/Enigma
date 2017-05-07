package mcjty.enigma.progress.serializers;

import mcjty.enigma.progress.MobConfig;
import mcjty.enigma.progress.NBTData;
import mcjty.lib.tools.ItemStackTools;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import static mcjty.enigma.varia.StringRegister.STRINGS;

public class MobConfigSerializer implements NBTData<Integer, MobConfig> {
    @Override
    public Integer getKey(NBTTagCompound tag) {
        return STRINGS.get(tag.getString("s"));
    }

    @Override
    public MobConfig getValue(NBTTagCompound nbt) {
        String mob = nbt.getString("mob");
        Double hp = null;
        if (nbt.hasKey("hp")) {
            hp = nbt.getDouble("hp");
        }
        Double damage = null;
        if (nbt.hasKey("damage")) {
            hp = nbt.getDouble("damage");
        }
        String tag = null;
        if (nbt.hasKey("tag")) {
            tag = nbt.getString("tag");
        }
        return new MobConfig(mob, tag, hp, damage, getNamedItem(nbt, "item"), getNamedItem(nbt, "helmet"), getNamedItem(nbt, "chest"),
                getNamedItem(nbt, "leggings"), getNamedItem(nbt, "boots"), nbt.getBoolean("aggressive"));
    }

    private ItemStack getNamedItem(NBTTagCompound tag, String tn) {
        if (tag.hasKey(tn)) {
            return ItemStackTools.loadFromNBT(tag.getCompoundTag(tn));
        }
        return ItemStackTools.getEmptyStack();
    }

    @Override
    public void serialize(NBTTagCompound tc, Integer key, MobConfig value) {
        tc.setString("s", STRINGS.get(key));
        tc.setString("mob", value.getMobId());
        if (value.getHp() != null) {
            tc.setDouble("hp", value.getHp());
        }
        if (value.getDamage() != null) {
            tc.setDouble("damage", value.getDamage());
        }
        if (value.getTag() != null) {
            tc.setString("tag", value.getTag());
        }
        writeItem(tc, "item", value.getHeldItem());
        writeItem(tc, "helmet", value.getHelmet());
        writeItem(tc, "chest", value.getChestplate());
        writeItem(tc, "leggings", value.getLeggings());
        writeItem(tc, "boots", value.getBoots());
        tc.setBoolean("aggressive", value.isAggressive());
    }

    private void writeItem(NBTTagCompound tc, String tn, ItemStack i) {
        if (i != null && ItemStackTools.isValid(i)) {
            NBTTagCompound tcitem = new NBTTagCompound();
            i.writeToNBT(tcitem);
            tc.setTag(tn, tcitem);
        }
    }
}
