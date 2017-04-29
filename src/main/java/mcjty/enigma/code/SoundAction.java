package mcjty.enigma.code;

import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import mcjty.enigma.varia.BlockPosDim;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
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
    public void execute(EnigmaFunctionContext context) {
        Progress progress = ProgressHolder.getProgress(context.getWorld());
        BlockPosDim namedPosition = progress.getNamedPosition(position.eval(context));
        // @todo error checking
        String soundname = ObjectTools.asStringSafe(sound.eval(context));
        SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(soundname));
        // @todo error checking
        context.getWorld().playSound(null, namedPosition.getPos(), sound, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }
}
