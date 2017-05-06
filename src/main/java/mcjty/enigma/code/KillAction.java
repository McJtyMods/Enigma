package mcjty.enigma.code;

import mcjty.enigma.parser.Expression;
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
        String tag = "enigma:" + (m instanceof String ? (String) m : STRINGS.get((Integer) m));
        List<Entity> entities = context.getWorld().getEntities(Entity.class, input -> input.getTags().contains(tag));
        for (Entity e : entities) {
            context.getWorld().removeEntity(e);
        }
    }

}
