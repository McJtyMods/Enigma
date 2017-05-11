package mcjty.enigma.progress.serializers;

import mcjty.enigma.progress.NBTData;
import mcjty.enigma.varia.BlockPosDim;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import static mcjty.enigma.varia.StringRegister.STRINGS;

public class PositionSerializer implements NBTData<Integer, BlockPosDim> {
    @Override
    public Integer getKey(NBTTagCompound tag) {
        return STRINGS.get(tag.getString("s"));
    }

    @Override
    public BlockPosDim getValue(NBTTagCompound tag) {
        BlockPos p = new BlockPos(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z"));
        return new BlockPosDim(p, tag.getInteger("dim"));
    }

    @Override
    public void serialize(NBTTagCompound tc, Integer key, BlockPosDim value) {
        tc.setString("s", STRINGS.get(key));
        BlockPos pos = value.getPos();
        tc.setInteger("x", pos.getX());
        tc.setInteger("y", pos.getY());
        tc.setInteger("z", pos.getZ());
        tc.setInteger("dim", value.getDimension());
    }
}
