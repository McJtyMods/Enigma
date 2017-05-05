package mcjty.enigma.progress.serializers;

import mcjty.enigma.progress.NBTData;
import mcjty.enigma.progress.PlayerProgress;
import net.minecraft.nbt.NBTTagCompound;

import java.util.UUID;

public class PlayerSerializer implements NBTData<UUID, PlayerProgress> {
    @Override
    public UUID getKey(NBTTagCompound tag) {
        return tag.getUniqueId("uuid");
    }

    @Override
    public PlayerProgress getValue(NBTTagCompound tag) {
        PlayerProgress p = new PlayerProgress();
        p.readFromNBT(tag);
        return p;
    }

    @Override
    public void serialize(NBTTagCompound tc, UUID key, PlayerProgress value) {
        tc.setUniqueId("uuid", key);
        value.writeToNBT(tc);
    }
}
