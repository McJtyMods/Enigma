package mcjty.enigma.code;

import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import org.apache.commons.lang3.StringUtils;

public class SetPlayerVariableAction extends Action {
    private final Expression<EnigmaFunctionContext> varname;
    private final Expression<EnigmaFunctionContext> value;

    public SetPlayerVariableAction(Expression<EnigmaFunctionContext> varname, Expression<EnigmaFunctionContext> value) {
        this.varname = varname;
        this.value = value;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "SetPlayerVariable: " + varname + " to " + value);

    }

    @Override
    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        checkPlayer(context);
        Progress progress = ProgressHolder.getProgress(context.getWorld());
        String n = ObjectTools.asStringSafe(varname.eval(context));
        Object v = value.eval(context);
        progress.getPlayerProgress(context.getPlayer().getPersistentID()).addNamedVariable(n, v);
    }
}
