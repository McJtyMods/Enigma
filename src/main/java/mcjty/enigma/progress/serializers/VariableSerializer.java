package mcjty.enigma.progress.serializers;

import mcjty.enigma.progress.NBTData;
import mcjty.enigma.varia.BlockPosDim;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import static mcjty.enigma.varia.StringRegister.STRINGS;

public class VariableSerializer implements NBTData<Integer, Object> {
    @Override
    public Integer getKey(NBTTagCompound tag) {
        return STRINGS.get(tag.getString("s"));
    }

    @Override
    public Object getValue(NBTTagCompound tag) {
        Object v = null;
        if (tag.hasKey("v")) {
            v = tag.getInteger("v");
        } else if (tag.hasKey("vs")) {
            v = tag.getString("vs");
        } else if (tag.hasKey("vb")) {
            v = tag.getBoolean("vb");
        } else if (tag.hasKey("vd")) {
            v = tag.getDouble("vd");
        } else if (tag.hasKey("vpx")) {
            v = new BlockPosDim(new BlockPos(tag.getInteger("vpx"), tag.getInteger("vpy"), tag.getInteger("vzz")),
                    tag.getInteger("vpd"));
        }
        return v;
    }

    @Override
    public void serialize(NBTTagCompound tc, Integer key, Object v) {
        tc.setString("s", STRINGS.get(key));
        if (v != null) {
            if (v instanceof Integer) {
                tc.setInteger("v", (Integer) v);
            } else if (v instanceof String) {
                tc.setString("vs", (String) v);
            } else if (v instanceof Boolean) {
                tc.setBoolean("vb", (Boolean) v);
            } else if (v instanceof Double) {
                tc.setDouble("vd", (Double) v);
            } else if (v instanceof BlockPosDim) {
                BlockPosDim p = (BlockPosDim) v;
                tc.setInteger("vpx", p.getPos().getX());
                tc.setInteger("vpy", p.getPos().getY());
                tc.setInteger("vpz", p.getPos().getZ());
                tc.setInteger("vpd", p.getDimension());
            }
        }
    }
}
