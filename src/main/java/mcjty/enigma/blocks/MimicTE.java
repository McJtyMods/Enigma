package mcjty.enigma.blocks;

import mcjty.enigma.Enigma;
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

    // Client side: to make sure our first readFromNBT() gets the info from the server but not anymore after that
    boolean offsetSyncHappened = false;
    double dx = 0;
    double dy = 0;
    double dz = 0;

    boolean colorSyncHappened = false;
    boolean blendColor = false;
    double red = 1.0f;
    double green = 1.0f;
    double blue = 1.0f;

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
        offsetSyncHappened = true;
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
    }

    public boolean isBlendColor() {
        return blendColor;
    }

    public double getRed() {
        return red;
    }

    public double getGreen() {
        return green;
    }

    public double getBlue() {
        return blue;
    }

    public void setBlendColor(double r, double g, double b) {
        colorSyncHappened = true;
        red = r;
        green = g;
        blue = b;
        blendColor = Math.abs(r-1) > .0001 || Math.abs(g-1) > .0001 || Math.abs(b-1) > .0001;
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
        if ((!Enigma.proxy.isClient()) && !offsetSyncHappened) {
            offsetSyncHappened = true;
            dx = compound.getDouble("dx");
            dy = compound.getDouble("dy");
            dz = compound.getDouble("dz");
        }
        if ((!Enigma.proxy.isClient()) && !colorSyncHappened) {
            colorSyncHappened = true;
            double r = compound.getDouble("r");
            double g = compound.getDouble("g");
            double b = compound.getDouble("b");
            setBlendColor(r, g, b);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        if (toMimic != null) {
            compound.setString("mimic", toMimic.getBlock().getRegistryName().toString());
            compound.setInteger("meta", toMimic.getBlock().getMetaFromState(toMimic));
        }
        compound.setDouble("dx", dx);
        compound.setDouble("dy", dy);
        compound.setDouble("dz", dz);
        compound.setDouble("r", red);
        compound.setDouble("g", green);
        compound.setDouble("b", blue);
        return compound;
    }
}
