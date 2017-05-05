package mcjty.enigma.code;

import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
import mcjty.enigma.progress.MobConfig;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import org.apache.commons.lang3.StringUtils;

public class CreateMobAction extends Action {
    private final Expression<EnigmaFunctionContext> name;
    private final Expression<EnigmaFunctionContext> mob;
    private final Expression<EnigmaFunctionContext> hp;
    private final Expression<EnigmaFunctionContext> item;

    public CreateMobAction(Expression<EnigmaFunctionContext> name,
                           Expression<EnigmaFunctionContext> mob,
                           Expression<EnigmaFunctionContext> hp,
                           Expression<EnigmaFunctionContext> item) {
        this.name = name;
        this.mob = mob;
        this.hp = hp;
        this.item = item;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "Create mob: " + name + "=" + mob);
    }

    @Override
    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        Progress progress = ProgressHolder.getProgress(context.getWorld());

        String name = ObjectTools.asStringSafe(this.name.eval(context));
        String mobName = ObjectTools.asStringSafe(this.mob.eval(context));
        Object hpval = this.hp.eval(context);
        Double hp = hpval == null ? null : ObjectTools.asDoubleSafe(hpval);
        Object itemval = this.item.eval(context);
        String itemName = itemval == null ? null : ObjectTools.asStringSafe(itemval);

        progress.addNamedMobConfig(name, new MobConfig(mobName, hp, itemName));

        ProgressHolder.save(context.getWorld());

    }
}
