package mcjty.enigma.progress;

import net.minecraft.nbt.NBTTagCompound;

public interface NBTData<K, V> {

    K getKey(NBTTagCompound tag);
    V getValue(NBTTagCompound tag);

    void serialize(NBTTagCompound tc, K key, V value);

}
