package mcjty.enigma.progress.serializers;

import mcjty.enigma.Enigma;
import mcjty.enigma.progress.NBTData;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import static mcjty.enigma.varia.StringRegister.STRINGS;

public class NamedBlockSerializer implements NBTData<Integer, IBlockState> {
    @Override
    public Integer getKey(NBTTagCompound tag) {
        return STRINGS.get(tag.getString("s"));
    }

    @Override
    public IBlockState getValue(NBTTagCompound tag) {
        String regname = tag.getString("reg");
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(regname));
        if (block != null) {
            int meta = tag.getInteger("meta");
            IBlockState state = block.getStateFromMeta(meta);
            return state;
        } else {
            Enigma.logger.warn("Block '" + regname + "' is missing!");
            return null;
        }
    }

    @Override
    public void serialize(NBTTagCompound tc, Integer key, IBlockState state) {
        tc.setString("s", STRINGS.get(key));
        tc.setString("reg", state.getBlock().getRegistryName().toString());
        tc.setInteger("meta", state.getBlock().getMetaFromState(state));
    }
}
