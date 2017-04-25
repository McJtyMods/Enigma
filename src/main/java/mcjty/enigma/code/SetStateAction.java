package mcjty.enigma.code;

import mcjty.enigma.Enigma;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static mcjty.enigma.varia.StringRegister.STRINGS;

public class SetStateAction extends Action {
    private final String name;
    private final String value;

    public SetStateAction(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "Set State: " + name + "=" + value);
    }

    @Override
    public void execute(World world, EntityPlayer player) {
        Progress progress = ProgressHolder.getProgress(world);
        System.out.println("Setting state " + name + " to " + value);

        int nameI = STRINGS.get(name);
        int valueI = STRINGS.get(value);

        List<Scope> toactivate = new ArrayList<>();
        Enigma.root.forActiveScopes(world, scope -> {
            for (Scope child : scope.getNestedScopes()) {
                if (!child.isActive(world)) {
                    if (child.getStateName().equals(nameI)) {
                        if (child.getStateValue().equals(valueI)) {
                            toactivate.add(child);
                        }
                    }
                }
            }
        });

        progress.setState(name, value);
        for (Scope scope : toactivate) {
            scope.start(world);
        }

        ProgressHolder.save(world);
    }
}
