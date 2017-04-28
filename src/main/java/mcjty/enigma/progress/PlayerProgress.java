package mcjty.enigma.progress;

import mcjty.enigma.code.ScopeID;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static mcjty.enigma.varia.StringRegister.STRINGS;

public class PlayerProgress {

    private final Map<Integer, Integer> states = new HashMap<>();
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
        readInitializedScopes(nbt);
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
        writeInitializedScopes(compound);
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

    private void writeInitializedScopes(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (ScopeID scope : initializedScopes) {
            list.appendTag(new NBTTagString(STRINGS.get(scope.getId())));
        }
        compound.setTag("scopes", list);
    }
}
