package mcjty.enigma.progress;

import mcjty.enigma.code.ScopeID;
import mcjty.enigma.progress.serializers.StateSerializer;
import mcjty.enigma.progress.serializers.VariableSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;

import java.util.HashSet;
import java.util.Set;

import static mcjty.enigma.varia.StringRegister.STRINGS;

public class PlayerProgress {

    private static final NBTData<Integer, Object> VARIABLE_SERIALIZER = new VariableSerializer();
    private static final NBTData<Integer, Integer> STATE_SERIALIZER = new StateSerializer();

    private final InternedKeyMap<Integer> states = new InternedKeyMap<>();
    private final InternedKeyMap<Object> namedVariables = new InternedKeyMap<>();
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
        namedVariables.put(name, value);
    }

    public Object getNamedVariable(Integer name) {
        return namedVariables.get(name);
    }

    public boolean isNamedVariable(Integer name) {
        return namedVariables.containsKey(name);
    }

    public Object getNamedVariable(Object o) {
        return namedVariables.getChecked(o);
    }

    public InternedKeyMap<Integer> getStates() {
        return states;
    }

    public void setState(String state, String value) {
        states.put(state, STRINGS.get(value));
    }

    public Integer getState(Object o) {
        return states.getChecked(o);
    }

    public void readFromNBT(NBTTagCompound nbt) {
        NBTDataSerializer.deserialize(nbt, "states", states, STATE_SERIALIZER);
        NBTDataSerializer.deserialize(nbt, "variables", namedVariables, VARIABLE_SERIALIZER);
        readInitializedScopes(nbt);
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
        NBTDataSerializer.serialize(compound, "states", states, STATE_SERIALIZER);
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
