package mcjty.enigma.code;

import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static mcjty.enigma.varia.StringRegister.STRINGS;

public class Scope {

    private final List<ActionBlock> onStart = new ArrayList<>();
    private final List<Pair<ActionBlock, Integer>> onDelay = new ArrayList<>();
    private final List<Pair<ActionBlock, Integer>> onRightClickBlock = new ArrayList<>();
    private final List<Pair<ActionBlock, Integer>> onLeftClickBlock = new ArrayList<>();
    private final List<Scope> nestedScopes = new ArrayList<>();

    private Integer stateName;
    private Integer stateValue;

    public void forActiveScopes(EntityPlayer player, Consumer<Scope> consumer) {
        Progress progress = ProgressHolder.getProgress(player.getEntityWorld());
        for (Scope scope : nestedScopes) {
            Integer state = progress.getState(scope.getStateName());
            if (scope.getStateValue().equals(state)) {
                consumer.accept(scope);
            }
        }
    }

    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event, @Nonnull Integer position) {
        EntityPlayer player = event.getEntityPlayer();
        Progress progress = ProgressHolder.getProgress(player.getEntityWorld());
        for (Pair<ActionBlock, Integer> pair : onRightClickBlock) {
            if (position.equals(pair.getValue())) {
                pair.getKey().execute(player);
            }
        }
    }

    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event, @Nonnull Integer position) {
        EntityPlayer player = event.getEntityPlayer();
        Progress progress = ProgressHolder.getProgress(player.getEntityWorld());
        for (Pair<ActionBlock, Integer> pair : onLeftClickBlock) {
            if (position.equals(pair.getValue())) {
                pair.getKey().execute(player);
            }
        }
    }

    public Integer getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = STRINGS.get(stateName);
    }

    public Integer getStateValue() {
        return stateValue;
    }

    public void setStateValue(String stateValue) {
        this.stateValue = STRINGS.get(stateValue);
    }

    public void addOnStart(ActionBlock actionBlock) {
        onStart.add(actionBlock);
    }

    public void addOnDelay(ActionBlock actionBlock, String delayPar) {
        onDelay.add(Pair.of(actionBlock, Integer.parseInt(delayPar)));
    }

    public void addOnRightClickBlock(ActionBlock actionBlock, String position) {
        onRightClickBlock.add(Pair.of(actionBlock, STRINGS.get(position)));
    }

    public void addOnLeftClickBlock(ActionBlock actionBlock, String position) {
        onLeftClickBlock.add(Pair.of(actionBlock, STRINGS.get(position)));
    }

    public void addScope(Scope scope) {
        nestedScopes.add(scope);
    }

    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "Scope " + STRINGS.get(stateName) + "=" + STRINGS.get(stateValue) + ":");
        for (ActionBlock block : onStart) {
            System.out.println(StringUtils.repeat(' ', indent+4) + "On Start:");
            block.dump(indent+4);
        }
        for (Pair<ActionBlock, Integer> pair : onDelay) {
            System.out.println(StringUtils.repeat(' ', indent+4) + "On Delay (" + pair.getValue() + "):");
            pair.getKey().dump(indent+4);
        }
        for (Pair<ActionBlock, Integer> pair : onRightClickBlock) {
            System.out.println(StringUtils.repeat(' ', indent+4) + "On Right Click Block (" + STRINGS.get(pair.getValue()) + "):");
            pair.getKey().dump(indent+4);
        }
        for (Pair<ActionBlock, Integer> pair : onLeftClickBlock) {
            System.out.println(StringUtils.repeat(' ', indent+4) + "On Left Click Block (" + STRINGS.get(pair.getValue()) + "):");
            pair.getKey().dump(indent+4);
        }
        for (Scope scope : nestedScopes) {
            scope.dump(indent+4);
        }
    }
}
