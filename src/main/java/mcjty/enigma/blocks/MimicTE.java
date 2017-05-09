package mcjty.enigma.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class MimicTE extends TileEntity {

    private IBlockState toMimic = null;

    public IBlockState getToMimic() {
        return toMimic;
    }

    public void setToMimic(IBlockState toMimic) {
        this.toMimic = toMimic;
        markDirty();
        if (getWorld() != null) {
            IBlockState state = getWorld().getBlockState(getPos());
            getWorld().notifyBlockUpdate(getPos(), state, state, 3);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("mimic")) {
            String mimic = compound.getString("mimic");
            int meta = compound.getInteger("meta");
            Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(mimic));
            if (block != null) {
                toMimic = block.getStateFromMeta(meta);
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        if (toMimic != null) {
            compound.setString("mimic", toMimic.getBlock().getRegistryName().toString());
            compound.setInteger("meta", toMimic.getBlock().getMetaFromState(toMimic));
        }
        return compound;
    }
}
