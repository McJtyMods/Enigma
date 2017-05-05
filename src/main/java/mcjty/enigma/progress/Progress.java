package mcjty.enigma.progress;

import mcjty.enigma.code.ScopeID;
import mcjty.enigma.progress.serializers.*;
import mcjty.enigma.varia.BlockPosDim;
import mcjty.lib.tools.ItemStackTools;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.*;

import static mcjty.enigma.varia.StringRegister.STRINGS;

public class Progress {

    private static final NBTData<Integer, Object> VARIABLE_SERIALIZER = new VariableSerializer();
    private static final NBTData<UUID, PlayerProgress> PLAYER_SERIALIZER = new PlayerSerializer();
    private static final NBTData<Integer, Integer> STATE_SERIALIZER = new StateSerializer();
    private static final NBTData<Integer, BlockPosDim> POSITION_SERIALIZER = new PositionSerializer();
    private static final NBTData<Integer, ParticleConfig> PARTICLE_SERIALIZER = new ParticleSerializer();
    private static final NBTData<Integer, IBlockState> NAMEDBLOCK_SERIALIZER = new NamedBlockSerializer();
    private static final NBTData<Integer, ItemStack> ITEMSTACK_SERIALIZER = new ItemStackSerializer();

    private final Map<Integer, Integer> states = new HashMap<>();
    private final Map<Integer, BlockPosDim> namedPositions = new HashMap<>();
    private final Map<BlockPosDim, Integer> positionsToName = new HashMap<>();
    private final Map<UUID, PlayerProgress> playerProgress = new HashMap<>();
    private final Map<Integer, ItemStack> namedItemStacks = new HashMap<>();
    private final Map<Integer, IBlockState> namedBlocks = new HashMap<>();
    private final Map<Pair<String,Integer>, Integer> blocksToName = new HashMap<>();
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
        blocksToName.clear();
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
        blocksToName.put(Pair.of(state.getBlock().getRegistryName().toString(), state.getBlock().getMetaFromState(state)), STRINGS.get(name));
    }

    public Integer getNamedBlock(IBlockState state) {
        return blocksToName.get(Pair.of(state.getBlock().getRegistryName().toString(), state.getBlock().getMetaFromState(state)));
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
        NBTDataSerializer.deserialize(nbt, "states", states, STATE_SERIALIZER);
        NBTDataSerializer.deserialize(nbt, "positions", namedPositions, POSITION_SERIALIZER);
        NBTDataSerializer.deserialize(nbt, "particles", namedParticleConfigs, PARTICLE_SERIALIZER);
        NBTDataSerializer.deserialize(nbt, "itemstacks", namedItemStacks, ITEMSTACK_SERIALIZER);
        NBTDataSerializer.deserialize(nbt, "blocks", namedBlocks, NAMEDBLOCK_SERIALIZER);
        NBTDataSerializer.deserialize(nbt, "players", playerProgress, PLAYER_SERIALIZER);
        NBTDataSerializer.deserialize(nbt, "variables", namedVariables, VARIABLE_SERIALIZER);
        readInitializedScopes(nbt);
        rootActivated = nbt.getBoolean("rootActivated");

        // Reverse maps
        for (Map.Entry<Integer, BlockPosDim> entry : namedPositions.entrySet()) {
            positionsToName.put(entry.getValue(), entry.getKey());
        }
        for (Map.Entry<Integer, IBlockState> entry : namedBlocks.entrySet()) {
            IBlockState state = entry.getValue();
            blocksToName.put(Pair.of(state.getBlock().getRegistryName().toString(), state.getBlock().getMetaFromState(state)), entry.getKey());
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
        NBTDataSerializer.serialize(compound, "states", states, STATE_SERIALIZER);
        NBTDataSerializer.serialize(compound, "positions", namedPositions, POSITION_SERIALIZER);
        NBTDataSerializer.serialize(compound, "itemstacks", namedItemStacks, ITEMSTACK_SERIALIZER);
        NBTDataSerializer.serialize(compound, "blocks", namedBlocks, NAMEDBLOCK_SERIALIZER);
        NBTDataSerializer.serialize(compound, "players", playerProgress, PLAYER_SERIALIZER);
        NBTDataSerializer.serialize(compound, "particles", namedParticleConfigs, PARTICLE_SERIALIZER);
        NBTDataSerializer.serialize(compound, "variables", namedVariables, VARIABLE_SERIALIZER);
        writeInitializedScopes(compound);
        return compound;
    }

    private void writeInitializedScopes(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (ScopeID scope : initializedScopes) {
            list.appendTag(new NBTTagString(STRINGS.get(scope.getId())));
        }
        compound.setTag("scopes", list);
    }

}
