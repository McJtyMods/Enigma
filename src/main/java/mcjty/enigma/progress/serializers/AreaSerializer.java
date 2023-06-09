package mcjty.enigma.progress.serializers;

import mcjty.enigma.progress.NBTData;
import mcjty.enigma.varia.Area;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import static mcjty.enigma.varia.StringRegister.STRINGS;

public class AreaSerializer implements NBTData<Integer, Area> {
    @Override
    public Integer getKey(NBTTagCompound tag) {
        return STRINGS.get(tag.getString("s"));
    }

    @Override
    public Area getValue(NBTTagCompound tag) {
        BlockPos p1 = new BlockPos(tag.getInteger("x1"), tag.getInteger("y1"), tag.getInteger("z1"));
        BlockPos p2 = new BlockPos(tag.getInteger("x2"), tag.getInteger("y2"), tag.getInteger("z2"));
        return new Area(p1, p2, tag.getInteger("dim"));
    }

    @Override
    public void serialize(NBTTagCompound tc, Integer key, Area value) {
        tc.setString("s", STRINGS.get(key));
        value.serializeNBT(tc);
    }
}
