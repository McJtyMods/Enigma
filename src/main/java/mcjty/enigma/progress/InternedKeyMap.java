package mcjty.enigma.progress;

import java.util.HashMap;

import static mcjty.enigma.varia.StringRegister.STRINGS;

public class InternedKeyMap<T> extends HashMap<Integer, T> {

    public void put(String name, T value) {
        put(STRINGS.get(name), value);
    }

    public T get(String name) {
        return get(STRINGS.get(name));
    }

    public boolean containsKey(String name) {
        return containsKey(STRINGS.get(name));
    }

    public T getChecked(Object o) {
        if (o instanceof Integer) {
            return get(o);
        } else if (o instanceof String) {
            return get((String) o);
        } else {
            return null;
        }
    }


}
