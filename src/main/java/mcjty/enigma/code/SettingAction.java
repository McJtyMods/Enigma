package mcjty.enigma.code;

import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ObjectTools;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;

public class SettingAction extends Action {
    private final Expression<EnigmaFunctionContext> name;
    private final Expression<EnigmaFunctionContext> block;
    private final Expression<EnigmaFunctionContext> meta;

    public SettingAction(Expression<EnigmaFunctionContext> name, Expression<EnigmaFunctionContext> block,
                         Expression<EnigmaFunctionContext> meta) {
        this.name = name;
        this.block = block;
        this.meta = meta;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "Create blockstate: " + name + "=" + block);
    }

    @Override
    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        Progress progress = ProgressHolder.getProgress(context.getWorld());

        String name = ObjectTools.asStringSafe(this.name.eval(context));
        String blockName = ObjectTools.asStringSafe(this.block.eval(context));
        Integer meta = ObjectTools.asIntSafe(this.meta.eval(context));
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockName));
        if (block == null) {
            throw new ExecutionException("Cannot find block '" + blockName + "'!");
        }
        IBlockState state = block.getStateFromMeta(meta);
        progress.addNamedBlock(name, state);

        ProgressHolder.save(context.getWorld());

    }
}
