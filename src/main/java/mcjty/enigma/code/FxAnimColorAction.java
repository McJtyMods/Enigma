package mcjty.enigma.code;

import mcjty.enigma.fxanim.FxAnimationHandler;
import mcjty.enigma.fxanim.animations.ColorAnimation;
import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
import net.minecraft.entity.player.EntityPlayerMP;
import org.apache.commons.lang3.StringUtils;

public class FxAnimColorAction extends Action {
    private final Expression<EnigmaFunctionContext> ticks;
    private final Expression<EnigmaFunctionContext> startA;
    private final Expression<EnigmaFunctionContext> startR;
    private final Expression<EnigmaFunctionContext> startG;
    private final Expression<EnigmaFunctionContext> startB;
    private final Expression<EnigmaFunctionContext> endA;
    private final Expression<EnigmaFunctionContext> endR;
    private final Expression<EnigmaFunctionContext> endG;
    private final Expression<EnigmaFunctionContext> endB;

    public FxAnimColorAction(Expression<EnigmaFunctionContext> ticks,
                             Expression<EnigmaFunctionContext> startA,
                             Expression<EnigmaFunctionContext> startR,
                             Expression<EnigmaFunctionContext> startG,
                             Expression<EnigmaFunctionContext> startB,
                             Expression<EnigmaFunctionContext> endA,
                             Expression<EnigmaFunctionContext> endR,
                             Expression<EnigmaFunctionContext> endG,
                             Expression<EnigmaFunctionContext> endB) {
        this.ticks = ticks;
        this.startA = startA;
        this.startR = startR;
        this.startG = startG;
        this.startB = startB;
        this.endA = endA;
        this.endR = endR;
        this.endG = endG;
        this.endB = endB;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "FxAnimColor:");

    }

    @Override
    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        checkPlayer(context);

        int t = ObjectTools.asIntSafe(ticks.eval(context));
        double starta = ObjectTools.asDoubleSafe(startA.eval(context));
        double startr = ObjectTools.asDoubleSafe(startR.eval(context));
        double startg = ObjectTools.asDoubleSafe(startG.eval(context));
        double startb = ObjectTools.asDoubleSafe(startB.eval(context));
        double enda = ObjectTools.asDoubleSafe(endA.eval(context));
        double endr = ObjectTools.asDoubleSafe(endR.eval(context));
        double endg = ObjectTools.asDoubleSafe(endG.eval(context));
        double endb = ObjectTools.asDoubleSafe(endB.eval(context));

        ColorAnimation animation = new ColorAnimation(starta, startr, startg, startb, enda, endr, endg, endb, t);
        FxAnimationHandler.startAnimationServer((EntityPlayerMP)context.getPlayer(), animation);
    }

}
