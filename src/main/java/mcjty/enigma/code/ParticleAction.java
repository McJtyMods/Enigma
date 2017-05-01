package mcjty.enigma.code;

import mcjty.enigma.parser.Expression;
import mcjty.enigma.progress.ParticleConfig;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import mcjty.enigma.varia.BlockPosDim;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.DimensionManager;
import org.apache.commons.lang3.StringUtils;

public class ParticleAction extends Action {
    private final Expression<EnigmaFunctionContext> position;
    private final Expression<EnigmaFunctionContext> particle;

    public ParticleAction(Expression<EnigmaFunctionContext> position, Expression<EnigmaFunctionContext> particle) {
        this.position = position;
        this.particle = particle;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "Particle: " + particle + " at " + position);
    }

    @Override
    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        Progress progress = ProgressHolder.getProgress(context.getWorld());
        Object pos = position.eval(context);
        BlockPosDim namedPosition = progress.getNamedPosition(pos);
        if (namedPosition == null) {
            throw new ExecutionException("Cannot find named position '" + pos + "'!");
        }
        Object particlename = particle.eval(context);
        ParticleConfig config = progress.getNamedParticleConfig(particlename);
        if (config == null) {
            throw new ExecutionException("Cannot find particle '" + particlename + "'!");
        }
        BlockPos p = namedPosition.getPos();
        DimensionManager.getWorld(namedPosition.getDimension()).spawnParticle(config.getParticles(), p.getX()+.5, p.getY()+.5, p.getZ()+.5,
                config.getAmount(), config.getOffsetX(), config.getOffsetY(), config.getOffsetZ(), config.getSpeed());
    }
}
