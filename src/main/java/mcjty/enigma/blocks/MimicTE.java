package mcjty.enigma.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;

public class MimicTE extends TileEntity {

    private IBlockState toMimic = null;

    // Client side data
    double dx = 0;
    double dy = 0;
    double dz = 0;

    public IBlockState getToMimic() {
        return toMimic;
    }

    public double getDx() {
        return dx;
    }

    public double getDy() {
        return dy;
    }

    public double getDz() {
        return dz;
    }

    public void setOffset(double dx, double dy, double dz) {
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbtTag = new NBTTagCompound();
        this.writeToNBT(nbtTag);
        return new SPacketUpdateTileEntity(pos, 1, nbtTag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
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
