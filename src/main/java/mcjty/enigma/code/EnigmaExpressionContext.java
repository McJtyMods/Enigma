package mcjty.enigma.code;

import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ExpressionContext;
import mcjty.enigma.parser.ExpressionFunction;
import mcjty.enigma.parser.ObjectTools;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import static mcjty.enigma.varia.StringRegister.STRINGS;

public class EnigmaExpressionContext implements ExpressionContext<EnigmaFunctionContext> {

    private static final Map<String, ExpressionFunction<EnigmaFunctionContext>> FUNCTIONS = new HashMap<>();

    static {
        FUNCTIONS.put("state", (context, o) -> {
            Progress progress = ProgressHolder.getProgress(context.getWorld());
            Integer valueI = progress.getState(ObjectTools.asStringSafe(o));
            return STRINGS.get(valueI);
        });
    }

    @Nullable
    @Override
    public Expression getVariable(String var) {
        return null;
    }

    @Override
    public boolean isVariable(String var) {
        return false;
    }

    @Nullable
    @Override
    public ExpressionFunction<EnigmaFunctionContext> getFunction(String name) {
        return FUNCTIONS.get(name);
    }

    @Override
    public boolean isFunction(String name) {
        return FUNCTIONS.containsKey(name);
    }
}
