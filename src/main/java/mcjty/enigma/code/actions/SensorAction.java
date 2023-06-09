package mcjty.enigma.code.actions;

import mcjty.enigma.code.*;
import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import mcjty.enigma.varia.IPositional;
import mcjty.enigma.varia.Sensor;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;

public class SensorAction extends Action {
    @Nonnull private final Expression<EnigmaFunctionContext> name;
    @Nonnull private final Expression<EnigmaFunctionContext> area;

    public SensorAction(Expression<EnigmaFunctionContext> name, Expression<EnigmaFunctionContext> area) {
        this.name = name;
        this.area = area;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "Sensor: " + name + " at " + area);
    }

    @Override
    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        Progress progress = ProgressHolder.getProgress(context.getWorld());
        String name = ObjectTools.asStringSafe(this.name.eval(context));
        IPositional<?> a = progress.getNamedArea(area.eval(context));
        if (a == null) {
            a = progress.getNamedPosition(area.eval(context));
        } else {
            throw new ExecutionException("Cannot find named area or position '" + area + "'!");
        }
        progress.addNamedSensor(name, new Sensor(a));
        ProgressHolder.save(context.getWorld());
    }
}
