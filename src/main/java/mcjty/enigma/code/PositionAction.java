package mcjty.enigma.code;

import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import mcjty.enigma.varia.BlockPosDim;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;

public class PositionAction extends Action {
    private final Expression name;
    private final Expression x;
    private final Expression y;
    private final Expression z;
    private final Expression dimension;

    public PositionAction(Expression name, Expression x, Expression y, Expression z, Expression dimension) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimension = dimension;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "Position: " + name + " at " + x + "," + y + "," + z);
    }

    @Override
    public void execute(World world, EntityPlayer player) {
        Progress progress = ProgressHolder.getProgress(world);
        BlockPos p = new BlockPos(ObjectTools.asIntSafe(x.eval(world)), ObjectTools.asIntSafe(y.eval(world)), ObjectTools.asIntSafe(z.eval(world)));
        String name = ObjectTools.asStringSafe(this.name.eval(world));
        System.out.println("Set Position: " + name);
        int dim = ObjectTools.asIntSafe(dimension.eval(world));
        progress.addNamedPosition(name, new BlockPosDim(p, dim));
        ProgressHolder.save(world);
    }
}
