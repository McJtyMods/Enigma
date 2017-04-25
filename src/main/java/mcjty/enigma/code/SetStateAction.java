package mcjty.enigma.code;

import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import net.minecraft.entity.player.EntityPlayer;
import org.apache.commons.lang3.StringUtils;

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
    public void execute(EntityPlayer player) {
        Progress progress = ProgressHolder.getProgress(player.getEntityWorld());
        progress.setState(name, value);
        System.out.println("Setting state " + name + " to " + value);
        ProgressHolder.save(player.getEntityWorld());
    }
}
