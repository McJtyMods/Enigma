package mcjty.enigma.code.actions;

import mcjty.enigma.code.Action;
import mcjty.enigma.code.EnigmaFunctionContext;
import mcjty.enigma.code.ExecutionException;
import org.apache.commons.lang3.StringUtils;

public class CancelAction extends Action {

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "Cancel");

    }

    @Override
    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        context.setCanceled(true);
    }
}
