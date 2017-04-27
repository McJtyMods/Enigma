package mcjty.enigma.code;

import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
import mcjty.enigma.progress.PlayerProgress;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import org.apache.commons.lang3.StringUtils;

public class SetPlayerStateAction extends Action {
    private final Expression<EnigmaFunctionContext> name;
    private final Expression<EnigmaFunctionContext> value;

    public SetPlayerStateAction(Expression<EnigmaFunctionContext> name, Expression<EnigmaFunctionContext>  value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "Set Player State: " + name + "=" + value);
    }

    @Override
    public void execute(EnigmaFunctionContext context) {
        checkPlayer(context);

        Progress progress = ProgressHolder.getProgress(context.getWorld());
        System.out.println("Setting player state " + name + " to " + value);

        PlayerProgress playerProgress = progress.getPlayerProgress(context.getPlayer());
        playerProgress.setState(ObjectTools.asStringSafe(name.eval(context)), ObjectTools.asStringSafe(value.eval(context)));

        ProgressHolder.save(context.getWorld());
    }
}
