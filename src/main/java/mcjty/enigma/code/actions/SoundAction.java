package mcjty.enigma.code.actions;

import mcjty.enigma.code.Action;
import mcjty.enigma.code.EnigmaFunctionContext;
import mcjty.enigma.code.ExecutionException;
import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import mcjty.enigma.varia.BlockPosDim;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.WorldServer;
import org.apache.commons.lang3.StringUtils;

public class SoundAction extends Action {
    private final Expression<EnigmaFunctionContext> position;
    private final Expression<EnigmaFunctionContext> sound;

    public SoundAction(Expression<EnigmaFunctionContext> position, Expression<EnigmaFunctionContext> sound) {
        this.position = position;
        this.sound = sound;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "Sound: " + sound + " at " + position);
    }

    @Override
    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        Progress progress = ProgressHolder.getProgress(context.getWorld());
        Object pos = position.eval(context);
        BlockPosDim namedPosition = progress.getNamedPosition(pos);
        if (namedPosition == null) {
            throw new ExecutionException("Cannot find named position '" + pos + "'!");
        }
        String soundname = ObjectTools.asStringSafe(sound.eval(context));
        SoundEvent sound = new SoundEvent(new ResourceLocation(soundname));
        WorldServer world = namedPosition.getWorld();
        world.playSound(null, namedPosition.getPos(), sound, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }
}
