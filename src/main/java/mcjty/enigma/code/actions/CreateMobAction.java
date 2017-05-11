package mcjty.enigma.code.actions;

import mcjty.enigma.code.Action;
import mcjty.enigma.code.EnigmaFunctionContext;
import mcjty.enigma.code.ExecutionException;
import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
import mcjty.enigma.progress.MobConfig;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import mcjty.lib.tools.ItemStackTools;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.StringUtils;

public class CreateMobAction extends Action {
    private final Expression<EnigmaFunctionContext> name;
    private final Expression<EnigmaFunctionContext> mob;
    private final Expression<EnigmaFunctionContext> tag;
    private final Expression<EnigmaFunctionContext> hp;
    private final Expression<EnigmaFunctionContext> damage;
    private final Expression<EnigmaFunctionContext> item;
    private final Expression<EnigmaFunctionContext> helmet;
    private final Expression<EnigmaFunctionContext> chestplate;
    private final Expression<EnigmaFunctionContext> leggings;
    private final Expression<EnigmaFunctionContext> boots;
    private final Expression<EnigmaFunctionContext> aggressive;

    public CreateMobAction(Expression<EnigmaFunctionContext> name,
                           Expression<EnigmaFunctionContext> mob,
                           Expression<EnigmaFunctionContext> tag,
                           Expression<EnigmaFunctionContext> hp,
                           Expression<EnigmaFunctionContext> damage,
                           Expression<EnigmaFunctionContext> item,
                           Expression<EnigmaFunctionContext> helmet,
                           Expression<EnigmaFunctionContext> chestplate,
                           Expression<EnigmaFunctionContext> leggings,
                           Expression<EnigmaFunctionContext> boots,
                           Expression<EnigmaFunctionContext> aggressive) {
        this.name = name;
        this.mob = mob;
        this.tag = tag;
        this.hp = hp;
        this.damage = damage;
        this.item = item;
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
        this.aggressive = aggressive;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "Create mob: " + name + "=" + mob);
    }

    @Override
    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        Progress progress = ProgressHolder.getProgress(context.getWorld());

        String name = ObjectTools.asStringSafe(this.name.eval(context));
        String tag = this.tag == null ? null : ObjectTools.asStringSafe(this.tag.eval(context));
        String mobName = ObjectTools.asStringSafe(this.mob.eval(context));
        Double hp = this.hp == null ? null : ObjectTools.asDoubleSafe(this.hp.eval(context));
        Double damage = this.damage == null ? null : ObjectTools.asDoubleSafe(this.damage.eval(context));
        boolean aggro = ObjectTools.asBoolSafe(aggressive);

        progress.addNamedMobConfig(name, new MobConfig(mobName, tag,
                hp, damage,
                getItem(context, item),
                getItem(context, helmet),
                getItem(context, chestplate),
                getItem(context, leggings),
                getItem(context, boots),
                aggro));

        ProgressHolder.save(context.getWorld());

    }

    private ItemStack getItem(EnigmaFunctionContext context, Expression<EnigmaFunctionContext> i) {
        Object itemval = i == null ? ItemStackTools.getEmptyStack() : i.eval(context);
        Progress progress = ProgressHolder.getProgress(context.getWorld());
        return progress.getNamedItemStack(itemval);
    }
}
