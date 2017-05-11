package mcjty.enigma.code.actions;

import mcjty.enigma.code.Action;
import mcjty.enigma.code.EnigmaFunctionContext;
import mcjty.enigma.code.ExecutionException;
import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
import net.minecraft.entity.Entity;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static mcjty.enigma.varia.StringRegister.STRINGS;

public class KillAction extends Action {
    private final Expression<EnigmaFunctionContext> mobconfig;

    public KillAction(Expression<EnigmaFunctionContext> mobconfig) {
        this.mobconfig = mobconfig;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "Kill: " + mobconfig);

    }

    @Override
    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        Object m = mobconfig.eval(context);
        String t2 = ObjectTools.asStringSafe(m);
        String tag = "enigma:" + (m instanceof String ? (String) m : STRINGS.get((Integer) m));
        List<Entity> entities = context.getWorld().getEntities(Entity.class, input -> (input.getTags().contains(tag) || input.getTags().contains(t2)));
        for (Entity e : entities) {
            context.getWorld().removeEntity(e);
        }
    }

}
