package mcjty.enigma.items;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModItems {

    public static Key key;
    public static Coin coin;

    public static void init() {
        key = new Key();
        coin = new Coin();
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        key.initModel();
        coin.initModel();
    }
}
