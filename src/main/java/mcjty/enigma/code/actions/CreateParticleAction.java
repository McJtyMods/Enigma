package mcjty.enigma.code.actions;

import mcjty.enigma.code.Action;
import mcjty.enigma.code.EnigmaFunctionContext;
import mcjty.enigma.code.ExecutionException;
import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
import mcjty.enigma.progress.ParticleConfig;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import net.minecraft.util.EnumParticleTypes;
import org.apache.commons.lang3.StringUtils;

public class CreateParticleAction extends Action {
    private final Expression<EnigmaFunctionContext> name;
    private final Expression<EnigmaFunctionContext> partsys;
    private final Expression<EnigmaFunctionContext> amount;
    private final Expression<EnigmaFunctionContext> offsetX;
    private final Expression<EnigmaFunctionContext> offsetY;
    private final Expression<EnigmaFunctionContext> offsetZ;
    private final Expression<EnigmaFunctionContext> speed;

    public CreateParticleAction(Expression<EnigmaFunctionContext> name,
                                Expression<EnigmaFunctionContext> partsys,
                                Expression<EnigmaFunctionContext> amount,
                                Expression<EnigmaFunctionContext> offsetX,
                                Expression<EnigmaFunctionContext> offsetY,
                                Expression<EnigmaFunctionContext> offsetZ,
                                Expression<EnigmaFunctionContext> speed) {
        this.name = name;
        this.partsys = partsys;
        this.amount = amount;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.speed = speed;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "Create particles: " + name + "=" + partsys);
    }

    @Override
    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        Progress progress = ProgressHolder.getProgress(context.getWorld());

        String name = ObjectTools.asStringSafe(this.name.eval(context));
        String partSysName = ObjectTools.asStringSafe(this.partsys.eval(context));
        EnumParticleTypes particles = EnumParticleTypes.getByName(partSysName);
        if (particles == null) {
            throw new ExecutionException("Cannot find particle system '" + partSysName + "'!");
        }
        int amount = ObjectTools.asIntSafe(this.amount.eval(context));
        if (amount <= 0) {
            amount = 1;
        }
        double offsetX = ObjectTools.asDoubleSafe(this.offsetX.eval(context));
        double offsetY = ObjectTools.asDoubleSafe(this.offsetY.eval(context));
        double offsetZ = ObjectTools.asDoubleSafe(this.offsetZ.eval(context));
        double speed = ObjectTools.asDoubleSafe(this.speed.eval(context));

        ParticleConfig config = new ParticleConfig(particles, amount, offsetX, offsetY, offsetZ, speed);
        progress.addNamedParticleConfig(name, config);

        ProgressHolder.save(context.getWorld());

    }
}
