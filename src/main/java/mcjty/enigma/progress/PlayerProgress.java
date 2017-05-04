package mcjty.enigma.progress;

import mcjty.enigma.code.ScopeID;
import mcjty.enigma.varia.BlockPosDim;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static mcjty.enigma.varia.StringRegister.STRINGS;

public class PlayerProgress {

    private final Map<Integer, Integer> states = new HashMap<>();
    private final Map<Integer, Object> namedVariables = new HashMap<>();
    private final Set<ScopeID> initializedScopes = new HashSet<>();

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

    public Map<Integer, Integer> getStates() {
        return states;
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

    public void readFromNBT(NBTTagCompound nbt) {
        readStates(nbt);
        readNamedVariables(nbt);
        readInitializedScopes(nbt);
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
            } else if (tc.hasKey("vpx")) {
                v = new BlockPosDim(new BlockPos(tc.getInteger("vpx"), tc.getInteger("vpy"), tc.getInteger("vzz")),
                        tc.getInteger("vpd"));
            }
            namedVariables.put(name, v);
        }
    }

    private void readStates(NBTTagCompound nbt) {
        NBTTagList statesList = nbt.getTagList("states", Constants.NBT.TAG_COMPOUND);
        for (int i = 0 ; i < statesList.tagCount() ; i++) {
            NBTTagCompound tc = (NBTTagCompound) statesList.get(i);
            states.put(STRINGS.get(tc.getString("s")), STRINGS.get(tc.getString("v")));
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
        writeStates(compound);
        writeNamedVariables(compound);
        writeInitializedScopes(compound);
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
                } else if (v instanceof BlockPosDim) {
                    BlockPosDim p = (BlockPosDim) v;
                    tc.setInteger("vpx", p.getPos().getX());
                    tc.setInteger("vpy", p.getPos().getY());
                    tc.setInteger("vpz", p.getPos().getZ());
                    tc.setInteger("vpd", p.getDimension());
                }
            }
            list.appendTag(tc);
        }
        compound.setTag("variables", list);
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

    private void writeInitializedScopes(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (ScopeID scope : initializedScopes) {
            list.appendTag(new NBTTagString(STRINGS.get(scope.getId())));
        }
        compound.setTag("scopes", list);
    }
}
