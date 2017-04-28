package mcjty.enigma.progress;

import mcjty.enigma.parser.ObjectTools;
import mcjty.enigma.varia.BlockPosDim;
import mcjty.lib.tools.ItemStackTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static mcjty.enigma.varia.StringRegister.STRINGS;

public class Progress {

    private final Map<Integer, Integer> states = new HashMap<>();
    private final Map<Integer, BlockPosDim> namedPositions = new HashMap<>();
    private final Map<BlockPosDim, Integer> positionsToName = new HashMap<>();
    private final Map<UUID, PlayerProgress> playerProgress = new HashMap<>();
    private final Map<Integer, ItemStack> namedItemStacks = new HashMap<>();

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

    public Integer getState(Object o) {
        if (o instanceof Integer) {
            return states.get(o);
        } else if (o instanceof String) {
            return states.get(STRINGS.get((String)o));
        } else {
            return null;
        }
    }

    public boolean isRootActivated() {
        return rootActivated;
    }

    public void setRootActivated(boolean rootActivated) {
        this.rootActivated = rootActivated;
    }

    public void addNamedPosition(String name, @Nonnull BlockPosDim pos) {
        namedPositions.put(STRINGS.get(name), pos);
        positionsToName.put(pos, STRINGS.get(name));
    }

    public BlockPosDim getNamedPosition(String name) {
        return namedPositions.get(STRINGS.get(name));
    }

    public BlockPosDim getNamedPosition(Integer name) {
        return namedPositions.get(name);
    }

    public Integer getNamedPosition(BlockPos pos, Integer dim) {
        return positionsToName.get(new BlockPosDim(pos, dim));
    }

    public void addNamedItemStack(String name, ItemStack stack) {
        namedItemStacks.put(STRINGS.get(name), stack);
    }

    public ItemStack getNamedItemStack(String name) {
        return namedItemStacks.get(STRINGS.get(name));
    }

    public ItemStack getNamedItemStack(Integer name) {
        return namedItemStacks.get(name);
    }

    public ItemStack getNamedItemStack(Object name) {
        if (name instanceof Integer) {
            return namedItemStacks.get(name);
        } else if (name instanceof String) {
            return namedItemStacks.get(STRINGS.get((String) name));
        } else {
            return ItemStackTools.getEmptyStack();
        }
    }

    public PlayerProgress getPlayerProgress(EntityPlayer player) {
        if (!playerProgress.containsKey(player.getPersistentID())) {
            PlayerProgress p = new PlayerProgress();
            playerProgress.put(player.getPersistentID(), p);
        }
        return playerProgress.get(player.getPersistentID());
    }

    public void readFromNBT(NBTTagCompound nbt) {
        readStates(nbt);
        readNamedPositions(nbt);
        readNamedItemStacks(nbt);
        readPlayerProgress(nbt);
        rootActivated = nbt.getBoolean("rootActivated");
    }

    private void readPlayerProgress(NBTTagCompound nbt) {
        NBTTagList list = nbt.getTagList("players", Constants.NBT.TAG_COMPOUND);
        for (int i = 0 ; i < list.tagCount() ; i++) {
            NBTTagCompound tc = (NBTTagCompound) list.get(i);
            UUID uuid = tc.getUniqueId("uuid");
            PlayerProgress p = new PlayerProgress();
            p.readFromNBT(tc);
            playerProgress.put(uuid, p);
        }
    }

    private void readStates(NBTTagCompound nbt) {
        NBTTagList statesList = nbt.getTagList("states", Constants.NBT.TAG_COMPOUND);
        for (int i = 0 ; i < statesList.tagCount() ; i++) {
            NBTTagCompound tc = (NBTTagCompound) statesList.get(i);
            states.put(STRINGS.get(tc.getString("s")), STRINGS.get(tc.getString("v")));
        }
    }

    private void readNamedPositions(NBTTagCompound nbt) {
        NBTTagList list = nbt.getTagList("positions", Constants.NBT.TAG_COMPOUND);
        for (int i = 0 ; i < list.tagCount() ; i++) {
            NBTTagCompound tc = (NBTTagCompound) list.get(i);
            int name = STRINGS.get(tc.getString("s"));
            BlockPos p = new BlockPos(tc.getInteger("x"), tc.getInteger("y"), tc.getInteger("z"));
            BlockPosDim pd = new BlockPosDim(p, tc.getInteger("dim"));
            namedPositions.put(name, pd);
            positionsToName.put(pd, name);
        }
    }

    private void readNamedItemStacks(NBTTagCompound nbt) {
        NBTTagList list = nbt.getTagList("itemstacks", Constants.NBT.TAG_COMPOUND);
        for (int i = 0 ; i < list.tagCount() ; i++) {
            NBTTagCompound tc = (NBTTagCompound) list.get(i);
            int name = STRINGS.get(tc.getString("s"));
            ItemStack stack = ItemStackTools.getEmptyStack();
            if (tc.hasKey("item")) {
                stack = ItemStackTools.loadFromNBT(tc.getCompoundTag("item"));
            }
            namedItemStacks.put(name, stack);
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setBoolean("rootActivated", rootActivated);
        writeStates(compound);
        writeNamedPositions(compound);
        writeNamedItemStacks(compound);
        writePlayerProgress(compound);
        return compound;
    }

    private void writePlayerProgress(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (Map.Entry<UUID, PlayerProgress> entry : playerProgress.entrySet()) {
            NBTTagCompound tc = new NBTTagCompound();
            tc.setUniqueId("uuid", entry.getKey());
            entry.getValue().writeToNBT(tc);
            list.appendTag(tc);
        }
        compound.setTag("players", list);
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
        for (Map.Entry<Integer, BlockPosDim> entry : namedPositions.entrySet()) {
            NBTTagCompound tc = new NBTTagCompound();
            tc.setString("s", STRINGS.get(entry.getKey()));
            BlockPos pos = entry.getValue().getPos();
            tc.setInteger("x", pos.getX());
            tc.setInteger("y", pos.getY());
            tc.setInteger("z", pos.getZ());
            tc.setInteger("dim", entry.getValue().getDimension());
            list.appendTag(tc);
        }
        compound.setTag("positions", list);
    }

    private void writeNamedItemStacks(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (Map.Entry<Integer, ItemStack> entry : namedItemStacks.entrySet()) {
            NBTTagCompound tc = new NBTTagCompound();
            tc.setString("s", STRINGS.get(entry.getKey()));
            if (ItemStackTools.isValid(entry.getValue())) {
                NBTTagCompound itemtag = new NBTTagCompound();
                entry.getValue().writeToNBT(itemtag);
                tc.setTag("item", itemtag);
            }
            list.appendTag(tc);
        }
        compound.setTag("itemstacks", list);
    }
}
