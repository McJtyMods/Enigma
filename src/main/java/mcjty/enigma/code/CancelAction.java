package mcjty.enigma.code;

import org.apache.commons.lang3.StringUtils;

public class CancelAction extends Action {

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "Cancel");

    }

    @Override
    public void execute(EnigmaFunctionContext context) {
        context.setCanceled(true);
    }
}
