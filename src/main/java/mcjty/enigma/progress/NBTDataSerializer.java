package mcjty.enigma.progress;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import java.util.Map;

public class NBTDataSerializer {

    public static <K, V> NBTTagList serialize(Map<K, V> data, NBTData<K, V> serializer) {
        NBTTagList list = new NBTTagList();
        for (Map.Entry<K, V> entry : data.entrySet()) {
            NBTTagCompound tc = new NBTTagCompound();
            serializer.serialize(tc, entry.getKey(), entry.getValue());
            list.appendTag(tc);
        }
        return list;
    }

    public static <K, V> void serialize(NBTTagCompound parent, String name, Map<K, V> data, NBTData<K, V> serializer) {
        parent.setTag(name, NBTDataSerializer.serialize(data, serializer));
    }

    public static <K, V> void deserialize(NBTTagList list, Map<K, V> data, NBTData<K, V> serializer) {
        for (int i = 0 ; i < list.tagCount() ; i++) {
            NBTTagCompound tc = (NBTTagCompound) list.get(i);
            K key = serializer.getKey(tc);
            V value = serializer.getValue(tc);
            data.put(key, value);
        }
    }

    public static <K, V> void deserialize(NBTTagCompound parent, String name, Map<K, V> data, NBTData<K, V> serializer) {
        deserialize(parent.getTagList(name, Constants.NBT.TAG_COMPOUND), data, serializer);
    }
}
