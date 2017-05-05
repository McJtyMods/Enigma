package mcjty.enigma.code;

import mcjty.enigma.fxanim.FxAnimationHandler;
import mcjty.enigma.fxanim.animations.RotateAnimation;
import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
import net.minecraft.entity.player.EntityPlayerMP;
import org.apache.commons.lang3.StringUtils;

public class FxAnimRotateAction extends Action {
    private final Expression<EnigmaFunctionContext> ticks;
    private final Expression<EnigmaFunctionContext> startYaw;
    private final Expression<EnigmaFunctionContext> endYaw;
    private final Expression<EnigmaFunctionContext> startPitch;
    private final Expression<EnigmaFunctionContext> endPitch;

    public FxAnimRotateAction(Expression<EnigmaFunctionContext> ticks,
                              Expression<EnigmaFunctionContext> startYaw,
                              Expression<EnigmaFunctionContext> startPitch,
                              Expression<EnigmaFunctionContext> endYaw,
                              Expression<EnigmaFunctionContext> endPitch) {
        this.ticks = ticks;
        this.startYaw = startYaw;
        this.endYaw = endYaw;
        this.startPitch = startPitch;
        this.endPitch = endPitch;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "FxAnimRotate:");

    }

    @Override
    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        checkPlayer(context);

        int t = ObjectTools.asIntSafe(ticks.eval(context));
        double syaw = ObjectTools.asDoubleSafe(startYaw.eval(context));
        double eyaw = ObjectTools.asDoubleSafe(endYaw.eval(context));
        double spitch = ObjectTools.asDoubleSafe(startPitch.eval(context));
        double epitch = ObjectTools.asDoubleSafe(endPitch.eval(context));

        RotateAnimation animation = new RotateAnimation(syaw, spitch, eyaw, epitch, t);
        FxAnimationHandler.startAnimationServer((EntityPlayerMP)context.getPlayer(), animation);
    }

}
