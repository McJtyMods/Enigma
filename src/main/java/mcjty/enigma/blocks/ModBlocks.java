package mcjty.enigma.blocks;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModBlocks {

    public static MimicBlock mimic;

    public static void init() {
        mimic = new MimicBlock();
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        mimic.initModel();
    }
}
