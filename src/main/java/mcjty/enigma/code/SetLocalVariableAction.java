package mcjty.enigma.code;

import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
import org.apache.commons.lang3.StringUtils;

public class SetLocalVariableAction extends Action {
    private final Expression<EnigmaFunctionContext> varname;
    private final Expression<EnigmaFunctionContext> value;

    public SetLocalVariableAction(Expression<EnigmaFunctionContext> varname, Expression<EnigmaFunctionContext> value) {
        this.varname = varname;
        this.value = value;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "SetLocalVariable: " + varname + " to " + value);

    }

    @Override
    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        String n = ObjectTools.asStringSafe(varname.eval(context));
        Object v = value.eval(context);
        context.setLocalVar(n, v);
    }
}
