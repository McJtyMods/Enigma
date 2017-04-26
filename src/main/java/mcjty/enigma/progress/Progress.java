package mcjty.enigma.progress;

import mcjty.enigma.varia.BlockPosDim;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

import static mcjty.enigma.varia.StringRegister.STRINGS;

public class Progress {

    private final Map<Integer, Integer> states = new HashMap<>();
    private final Map<String, BlockPosDim> namedPositions = new HashMap<>();
    private final Map<BlockPosDim, String> positionsToName = new HashMap<>();

    private boolean rootActivated = false;

    public void setState(String state, String value) {
        states.put(STRINGS.get(state), STRINGS.get(value));
    }

    public Integer getState(String state) {
        return states.get(STRINGS.get(state));
    }

    public Integer getState(Integer state) {
        return states.get(state);
    }

    public boolean isRootActivated() {
        return rootActivated;
    }

    public void setRootActivated(boolean rootActivated) {
        this.rootActivated = rootActivated;
    }

    public void addNamedPosition(String name, @Nonnull BlockPosDim pos) {
//        namedPositions.put(STRINGS.get(name), pos);
        namedPositions.put(name, pos);
        positionsToName.put(pos, name);//STRINGS.get(name));
    }

    public BlockPosDim getNamedPosition(String name) {
//        return namedPositions.get(STRINGS.get(name));
        return namedPositions.get(name);
    }

//    public BlockPosDim getNamedPosition(Integer name) {
//        return namedPositions.get(name);
//    }

    public String getNamedPosition(BlockPos pos, Integer dim) {
        return positionsToName.get(new BlockPosDim(pos, dim));
    }

    public void readFromNBT(NBTTagCompound nbt) {
        readStates(nbt);
        readNamedPositions(nbt);
        rootActivated = nbt.getBoolean("rootActivated");
    }

    private void readStates(NBTTagCompound nbt) {
        NBTTagList statesList = nbt.getTagList("states", Constants.NBT.TAG_COMPOUND);
        for (int i = 0 ; i < statesList.tagCount() ; i++) {
            NBTTagCompound tc = (NBTTagCompound) statesList.get(i);
            states.put(STRINGS.get(tc.getString("s")), STRINGS.get(tc.getString("v")));
        }
    }

    private void readNamedPositions(NBTTagCompound nbt) {
        NBTTagList statesList = nbt.getTagList("positions", Constants.NBT.TAG_COMPOUND);
        for (int i = 0 ; i < statesList.tagCount() ; i++) {
            NBTTagCompound tc = (NBTTagCompound) statesList.get(i);
//            int name = STRINGS.get(tc.getString("s"));
            String name = tc.getString("s");
            BlockPos p = new BlockPos(tc.getInteger("x"), tc.getInteger("y"), tc.getInteger("z"));
            BlockPosDim pd = new BlockPosDim(p, tc.getInteger("dim"));
            namedPositions.put(name, pd);
            positionsToName.put(pd, name);
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        writeStates(compound);
        writeNamedPositions(compound);
        compound.setBoolean("rootActivated", rootActivated);
        return compound;
    }

    private void writeStates(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (Map.Entry<Integer, Integer> entry : states.entrySet()) {
            NBTTagCompound tc = new NBTTagCompound();
            tc.setString("s", STRINGS.get(entry.getKey()));
            tc.setString("v", STRINGS.get(entry.getValue()));
            list.appendTag(tc);
        }
        compound.setTag("states", list);
    }

    private void writeNamedPositions(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (Map.Entry<String, BlockPosDim> entry : namedPositions.entrySet()) {
            NBTTagCompound tc = new NBTTagCompound();
//            tc.setString("s", STRINGS.get(entry.getKey()));
            tc.setString("s", entry.getKey());
            BlockPos pos = entry.getValue().getPos();
            tc.setInteger("x", pos.getX());
            tc.setInteger("y", pos.getY());
            tc.setInteger("z", pos.getZ());
            tc.setInteger("dim", entry.getValue().getDimension());
            list.appendTag(tc);
        }
        compound.setTag("positions", list);
    }
}
