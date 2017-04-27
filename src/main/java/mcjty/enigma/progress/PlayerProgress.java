package mcjty.enigma.progress;

import java.util.HashMap;
import java.util.Map;

import static mcjty.enigma.varia.StringRegister.STRINGS;

public class PlayerProgress {

    private final Map<Integer, Integer> states = new HashMap<>();

    public void setState(String state, String value) {
        states.put(STRINGS.get(state), STRINGS.get(value));
    }

    public Integer getState(String state) {
        return states.get(STRINGS.get(state));
    }

    public Integer getState(Integer state) {
        return states.get(state);
    }


}
