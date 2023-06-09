package mcjty.enigma.progress.serializers;

import mcjty.enigma.progress.NBTData;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.varia.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

import java.util.Set;
import java.util.UUID;

import static mcjty.enigma.varia.StringRegister.STRINGS;

public class SensorSerializer implements NBTData<Integer, Sensor> {
    @Override
    public Integer getKey(NBTTagCompound tag) {
        return STRINGS.get(tag.getString("s"));
    }

    @Override
    public Sensor getValue(NBTTagCompound tag) {
        int type = tag.getInteger("type");
        NBTData<Integer, ?> serializer;
        if (type == 0) {
            serializer = Progress.POSITION_SERIALIZER;
        } else if (type == 1) {
            serializer = Progress.AREA_SERIALIZER;
        } else if (type == 2) {
            serializer = Progress.SPHERE_SERIALIZER;
        } else {
            throw new RuntimeException("Bad type!");
        }
        Sensor sensor = new Sensor((IPositional) serializer.getValue(tag));
        NBTTagList playersTag = tag.getTagList("players", Constants.NBT.TAG_COMPOUND);
        for (int i = 0 ; i < playersTag.tagCount() ; i++) {
            NBTTagCompound playerTag = playersTag.getCompoundTagAt(i);
            sensor.addPlayer(playerTag.getUniqueId("uuid"));
        }
        return sensor;
    }

    @Override
    public void serialize(NBTTagCompound tc, Integer key, Sensor value) {
        IPositional positional = value.getPositional();
        tc.setString("s", STRINGS.get(key));
        if (positional instanceof BlockPosDim) {
            tc.setInteger("type", 0);
        } else if (positional instanceof Area) {
            tc.setInteger("type", 1);
        } else if (positional instanceof Sphere) {
            tc.setInteger("type", 2);
        } else {
            throw new RuntimeException("Cannot serialize positional!");
        }
        positional.serializeNBT(tc);
        Set<UUID> players = value.getPlayers();
        NBTTagList playersTag = new NBTTagList();
        for (UUID player : players) {
            NBTTagCompound playerTag = new NBTTagCompound();
            playerTag.setUniqueId("uuid", player);
            playersTag.appendTag(playerTag);
        }
        tc.setTag("players", playersTag);
    }
}
