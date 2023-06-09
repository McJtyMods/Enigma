package mcjty.enigma.progress.serializers;

import mcjty.enigma.progress.NBTData;
import mcjty.enigma.varia.Area;
import mcjty.enigma.varia.Sphere;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import static mcjty.enigma.varia.StringRegister.STRINGS;

public class SphereSerializer implements NBTData<Integer, Sphere> {
    @Override
    public Integer getKey(NBTTagCompound tag) {
        return STRINGS.get(tag.getString("s"));
    }

    @Override
    public Sphere getValue(NBTTagCompound tag) {
        BlockPos p = new BlockPos(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z"));
        return new Sphere(p, tag.getInteger("dim"), tag.getInteger("radius"));
    }

    @Override
    public void serialize(NBTTagCompound tc, Integer key, Sphere value) {
        tc.setString("s", STRINGS.get(key));
        value.serializeNBT(tc);
    }
}
