package mcjty.enigma.progress;

import mcjty.enigma.Enigma;
import mcjty.enigma.code.ScopeID;
import mcjty.enigma.varia.BlockPosDim;
import mcjty.lib.tools.ItemStackTools;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.*;

import static mcjty.enigma.varia.StringRegister.STRINGS;

public class Progress {

    private final Map<Integer, Integer> states = new HashMap<>();
    private final Map<Integer, BlockPosDim> namedPositions = new HashMap<>();
    private final Map<BlockPosDim, Integer> positionsToName = new HashMap<>();
    private final Map<UUID, PlayerProgress> playerProgress = new HashMap<>();
    private final Map<Integer, ItemStack> namedItemStacks = new HashMap<>();
    private final Map<Integer, IBlockState> namedBlocks = new HashMap<>();
    private final Map<Integer, Object> namedVariables = new HashMap<>();
    private final Map<Integer, ParticleConfig> namedParticleConfigs = new HashMap<>();
    private final Set<ScopeID> initializedScopes = new HashSet<>();

    private boolean rootActivated = false;

    public void reset() {
        rootActivated = false;
        states.clear();
        namedPositions.clear();
        positionsToName.clear();
        playerProgress.clear();
        namedItemStacks.clear();
        namedBlocks.clear();
        namedVariables.clear();
        initializedScopes.clear();
    }

    public Map<Integer, Integer> getStates() {
        return states;
    }

    public void addNamedVariable(String name, Object value) {
        namedVariables.put(STRINGS.get(name), value);
    }

    public Object getNamedVariable(String name) {
        return namedVariables.get(STRINGS.get(name));
    }

    public Object getNamedVariable(Integer name) {
        return namedVariables.get(name);
    }

    public boolean isNamedVariable(String name) {
        return namedVariables.containsKey(STRINGS.get(name));
    }

    public boolean isNamedVariable(Integer name) {
        return namedVariables.containsKey(name);
    }

    public Object getNamedVariable(Object o) {
        if (o instanceof Integer) {
            return getNamedVariable((Integer) o);
        } else if (o instanceof String) {
            return getNamedVariable((String) o);
        } else {
            return null;
        }
    }

    public void addNamedParticleConfig(String name, @Nonnull ParticleConfig config) {
        namedParticleConfigs.put(STRINGS.get(name), config);
    }

    public ParticleConfig getNamedParticleConfig(String name) {
        return namedParticleConfigs.get(STRINGS.get(name));
    }

    public ParticleConfig getNamedParticleConfig(Integer name) {
        return namedParticleConfigs.get(name);
    }

    public ParticleConfig getNamedParticleConfig(Object o) {
        if (o instanceof Integer) {
            return getNamedParticleConfig((Integer) o);
        } else if (o instanceof String) {
            return getNamedParticleConfig((String) o);
        } else {
            return null;
        }
    }

    public void setScopeInitialized(Integer scope) {
        initializedScopes.add(new ScopeID(scope));
    }

    public void setScopeInitialized(String scope) {
        initializedScopes.add(new ScopeID(STRINGS.get(scope)));
    }

    public boolean isScopeInitialized(ScopeID scope) {
        return initializedScopes.contains(scope);
    }

    public void setScopeInitialized(Object o) {
        if (o instanceof Integer) {
            initializedScopes.add(new ScopeID((Integer) o));
        } else if (o instanceof ScopeID) {
            initializedScopes.add((ScopeID) o);
        } else if (o instanceof String) {
            initializedScopes.add(new ScopeID(STRINGS.get((String)o)));
        } else {
            throw new RuntimeException("Bad type for scope!");
        }
    }

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

    public Collection<BlockPosDim> getNamedPositions() {
        return namedPositions.values();
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

    public BlockPosDim getNamedPosition(Object o) {
        if (o instanceof Integer) {
            return getNamedPosition((Integer) o);
        } else if (o instanceof String) {
            return getNamedPosition((String) o);
        } else if (o instanceof BlockPosDim) {
            return (BlockPosDim) o;
        } else {
            return null;
        }
    }

    public Integer getNamedPosition(BlockPos pos, Integer dim) {
        return positionsToName.get(new BlockPosDim(pos, dim));
    }

    public void addNamedBlock(String name, IBlockState state) {
        namedBlocks.put(STRINGS.get(name), state);
    }

    public IBlockState getNamedBlock(String name) {
        return namedBlocks.get(STRINGS.get(name));
    }

    public IBlockState getNamedBlock(Integer name) {
        return namedBlocks.get(name);
    }

    public IBlockState getNamedBlock(Object name) {
        if (name instanceof Integer) {
            return namedBlocks.get(name);
        } else if (name instanceof String) {
            return namedBlocks.get(STRINGS.get((String) name));
        } else {
            return null;
        }
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

    public PlayerProgress getPlayerProgress(UUID uuid) {
        if (!playerProgress.containsKey(uuid)) {
            PlayerProgress p = new PlayerProgress();
            playerProgress.put(uuid, p);
        }
        return playerProgress.get(uuid);
    }

    public Map<UUID, PlayerProgress> getPlayerProgress() {
        return playerProgress;
    }

    public void readFromNBT(NBTTagCompound nbt) {
        readStates(nbt);
        readNamedPositions(nbt);
        readNamedItemStacks(nbt);
        readNamedBlocks(nbt);
        readPlayerProgress(nbt);
        readInitializedScopes(nbt);
        readNamedParticleConfigs(nbt);
        readNamedVariables(nbt);
        rootActivated = nbt.getBoolean("rootActivated");
    }

    private void readNamedVariables(NBTTagCompound nbt) {
        NBTTagList list = nbt.getTagList("variables", Constants.NBT.TAG_COMPOUND);
        for (int i = 0 ; i < list.tagCount() ; i++) {
            NBTTagCompound tc = (NBTTagCompound) list.get(i);
            int name = STRINGS.get(tc.getString("s"));
            Object v = null;
            if (tc.hasKey("v")) {
                v = tc.getInteger("v");
            } else if (tc.hasKey("vs")) {
                v = tc.getString("vs");
            } else if (tc.hasKey("vb")) {
                v = tc.getBoolean("vb");
            } else if (tc.hasKey("vd")) {
                v = tc.getDouble("vd");
            }
            namedVariables.put(name, v);
        }
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

    private void readNamedParticleConfigs(NBTTagCompound nbt) {
        NBTTagList list = nbt.getTagList("particles", Constants.NBT.TAG_COMPOUND);
        for (int i = 0 ; i < list.tagCount() ; i++) {
            NBTTagCompound tc = (NBTTagCompound) list.get(i);
            int name = STRINGS.get(tc.getString("s"));
            String pn = tc.getString("pn");
            int amount = tc.getInteger("a");
            double ox = tc.getDouble("ox");
            double oy = tc.getDouble("oy");
            double oz = tc.getDouble("oz");
            double speed = tc.getDouble("speed");
            namedParticleConfigs.put(name, new ParticleConfig(EnumParticleTypes.getByName(pn), amount, ox, oy, oz, speed));
        }
    }

    private void readNamedBlocks(NBTTagCompound nbt) {
        NBTTagList list = nbt.getTagList("blocks", Constants.NBT.TAG_COMPOUND);
        for (int i = 0 ; i < list.tagCount() ; i++) {
            NBTTagCompound tc = (NBTTagCompound) list.get(i);
            int name = STRINGS.get(tc.getString("s"));
            String regname = tc.getString("reg");
            Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(regname));
            if (block != null) {
                int meta = tc.getInteger("meta");
                IBlockState state = block.getStateFromMeta(meta);
                namedBlocks.put(name, state);
            } else {
                Enigma.logger.warn("Block '" + regname + "' is missing!");
            }
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

    public void readInitializedScopes(NBTTagCompound nbt) {
        NBTTagList list = nbt.getTagList("scopes", Constants.NBT.TAG_STRING);
        for (int i = 0 ; i < list.tagCount() ; i++) {
            NBTTagString tc = (NBTTagString) list.get(i);
            int scope = STRINGS.get(tc.getString());
            initializedScopes.add(new ScopeID(scope));
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setBoolean("rootActivated", rootActivated);
        writeStates(compound);
        writeNamedPositions(compound);
        writeNamedItemStacks(compound);
        writeNamedBlocks(compound);
        writePlayerProgress(compound);
        writeInitializedScopes(compound);
        writeNamedParticleConfigs(compound);
        writeNamedVariables(compound);
        return compound;
    }

    private void writeNamedVariables(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (Map.Entry<Integer, Object> entry : namedVariables.entrySet()) {
            NBTTagCompound tc = new NBTTagCompound();
            tc.setString("s", STRINGS.get(entry.getKey()));
            Object v = entry.getValue();
            if (v != null) {
                if (v instanceof Integer) {
                    tc.setInteger("v", (Integer) v);
                } else if (v instanceof String) {
                    tc.setString("vs", (String) v);
                } else if (v instanceof Boolean) {
                    tc.setBoolean("vb", (Boolean) v);
                } else if (v instanceof Double) {
                    tc.setDouble("vd", (Double) v);
                }
            }
            list.appendTag(tc);
        }
        compound.setTag("variables", list);
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

    private void writeNamedParticleConfigs(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (Map.Entry<Integer, ParticleConfig> entry : namedParticleConfigs.entrySet()) {
            NBTTagCompound tc = new NBTTagCompound();
            tc.setString("s", STRINGS.get(entry.getKey()));
            ParticleConfig pc = entry.getValue();
            tc.setString("pn", pc.getParticles().getParticleName());
            tc.setInteger("a", pc.getAmount());
            tc.setDouble("ox", pc.getOffsetX());
            tc.setDouble("oy", pc.getOffsetY());
            tc.setDouble("oz", pc.getOffsetZ());
            tc.setDouble("speed", pc.getSpeed());
            list.appendTag(tc);
        }
        compound.setTag("particles", list);
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

    private void writeNamedBlocks(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (Map.Entry<Integer, IBlockState> entry : namedBlocks.entrySet()) {
            NBTTagCompound tc = new NBTTagCompound();
            tc.setString("s", STRINGS.get(entry.getKey()));
            IBlockState state = entry.getValue();
            tc.setString("reg", state.getBlock().getRegistryName().toString());
            tc.setInteger("meta", state.getBlock().getMetaFromState(state));
            list.appendTag(tc);
        }
        compound.setTag("blocks", list);
    }

    private void writeInitializedScopes(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (ScopeID scope : initializedScopes) {
            list.appendTag(new NBTTagString(STRINGS.get(scope.getId())));
        }
        compound.setTag("scopes", list);
    }
}
