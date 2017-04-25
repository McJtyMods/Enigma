package mcjty.enigma.progress;

import mcjty.lib.tools.WorldTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

public class ProgressHolder extends WorldSavedData {

    public static final String NAME = "EnigmaProgress";
    private static ProgressHolder instance = null;

    private Progress progress = new Progress();

    public ProgressHolder(String name) {
        super(name);
    }

    public static void save(World world) {
        ProgressHolder progressHolder = getProgressHolder(world);
        WorldTools.saveData(world, NAME, progressHolder);
        progressHolder.markDirty();
    }

    public static void clearInstance() {
        if (instance != null) {
            instance = null;
        }
    }

    public static ProgressHolder getProgressHolder(World world) {
        if (world.isRemote) {
            return null;
        }
        if (instance != null) {
            return instance;
        }
        instance = WorldTools.loadData(world, ProgressHolder.class, NAME);
        if (instance == null) {
            instance = new ProgressHolder(NAME);
        }
        return instance;
    }

    public static Progress getProgress(World world) {
        ProgressHolder holder = getProgressHolder(world);
        return holder.progress;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        progress.readFromNBT(nbt);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        return progress.writeToNBT(compound);
    }
}
