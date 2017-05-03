package mcjty.enigma.code;

import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import org.apache.commons.lang3.StringUtils;

public class SetVariableAction extends Action {
    private final Expression<EnigmaFunctionContext> varname;
    private final Expression<EnigmaFunctionContext> value;

    public SetVariableAction(Expression<EnigmaFunctionContext> varname, Expression<EnigmaFunctionContext> value) {
        this.varname = varname;
        this.value = value;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "SetVariable: " + varname + " to " + value);

    }

    @Override
    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        Progress progress = ProgressHolder.getProgress(context.getWorld());
        String n = ObjectTools.asStringSafe(varname.eval(context));
        Object v = value.eval(context);
        progress.addNamedVariable(n, v);
    }
}
