package mcjty.enigma.code;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class Scope {

    private final List<ActionBlock> onStartBlocks = new ArrayList<>();
    private final List<Pair<ActionBlock, Integer>> onDelay = new ArrayList<>();
    private final List<Scope> nestedScopes = new ArrayList<>();

    private String stateName;
    private String stateValue;

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getStateValue() {
        return stateValue;
    }

    public void setStateValue(String stateValue) {
        this.stateValue = stateValue;
    }

    public void addOnStart(ActionBlock actionBlock) {
        onStartBlocks.add(actionBlock);
    }

    public void addOnDelay(ActionBlock actionBlock, String delayPar) {
        onDelay.add(Pair.of(actionBlock, Integer.parseInt(delayPar)));
    }

    public void addScope(Scope scope) {
        nestedScopes.add(scope);
    }

    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "Scope " + stateName + "=" + stateValue + ":");
        for (ActionBlock block : onStartBlocks) {
            System.out.println(StringUtils.repeat(' ', indent+4) + "On Start:");
            block.dump(indent+4);
        }
        for (Pair<ActionBlock, Integer> pair : onDelay) {
            System.out.println(StringUtils.repeat(' ', indent+4) + "On Delay (" + pair.getValue() + "):");
            pair.getKey().dump(indent+4);
        }
        for (Scope scope : nestedScopes) {
            scope.dump(indent+4);
        }
    }
}
