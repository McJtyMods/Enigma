package mcjty.enigma.code;

import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import mcjty.enigma.varia.BlockPosDim;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.StringUtils;

public class PositionAction extends Action {
    private final String name;
    private final BlockPos pos;
    private final int dimension;

    public PositionAction(String name, String x, String y, String z, String dimension) {
        this.name = name;
        this.pos = new BlockPos(Integer.parseInt(x), Integer.parseInt(y), Integer.parseInt(z));
        this.dimension = Integer.parseInt(dimension);
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "Position: " + name + " at " + pos.getX() + "," + pos.getY() + "," + pos.getZ());
    }

    @Override
    public void execute(EntityPlayer player) {
        Progress progress = ProgressHolder.getProgress(player.getEntityWorld());
        progress.addNamedPosition(name, new BlockPosDim(pos, dimension));
        ProgressHolder.save(player.getEntityWorld());
    }
}
