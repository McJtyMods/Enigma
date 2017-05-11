package mcjty.enigma.code.actions;

import mcjty.enigma.code.Action;
import mcjty.enigma.code.EnigmaFunctionContext;
import mcjty.enigma.code.ExecutionException;
import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;

public class CreateItemStackAction extends Action {
    private final Expression<EnigmaFunctionContext> name;
    private final Expression<EnigmaFunctionContext> item;
    private final Expression<EnigmaFunctionContext> amount;
    private final Expression<EnigmaFunctionContext> meta;
    private final Expression<EnigmaFunctionContext> description;

    public CreateItemStackAction(Expression<EnigmaFunctionContext> name, Expression<EnigmaFunctionContext> item,
                                 Expression<EnigmaFunctionContext> amount,
                                 Expression<EnigmaFunctionContext> meta,
                                 Expression<EnigmaFunctionContext> description) {
        this.name = name;
        this.item = item;
        this.amount = amount;
        this.meta = meta;
        this.description = description;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "Create itemstack: " + name + "=" + item);
    }

    @Override
    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        Progress progress = ProgressHolder.getProgress(context.getWorld());

        String name = ObjectTools.asStringSafe(this.name.eval(context));
        String itemName = ObjectTools.asStringSafe(this.item.eval(context));
        Integer meta = ObjectTools.asIntSafe(this.meta.eval(context));
        Integer amount = ObjectTools.asIntSafe(this.amount.eval(context));
        if (amount <= 0) {
            amount = 1;
        }
        String description = this.description == null ? null : ObjectTools.asStringSafe(this.description.eval(context));
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName));
        if (item == null) {
            throw new ExecutionException("Cannot find item '" + itemName + "'!");
        }
        ItemStack stack = new ItemStack(item, amount, meta);

        if (description != null && !description.isEmpty()) {
            stack.setStackDisplayName(description);
        }

        progress.addNamedItemStack(name, stack);

        ProgressHolder.save(context.getWorld());

    }
}
