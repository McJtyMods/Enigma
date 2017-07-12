package mcjty.enigma.items;

import mcjty.enigma.Enigma;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Coin extends Item {

    public Coin() {
        setUnlocalizedName(Enigma.MODID + "_" + "coin");
        setRegistryName("coin");
        setCreativeTab(Enigma.tabEnigma);
        setMaxStackSize(64);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelResourceLocation models[] = new ModelResourceLocation[4];
        for (int i = 0 ; i < 2 ; i++) {
            ResourceLocation registryName = getRegistryName();
            models[i] = new ModelResourceLocation(registryName, "coin" + i);
            ModelBakery.registerItemVariants(this, models[i]);
        }

        ModelLoader.setCustomMeshDefinition(this, new ItemMeshDefinition() {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                return models[stack.getItemDamage()];
            }
        });
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack) {
        return super.getUnlocalizedName(itemStack) + itemStack.getItemDamage();
    }


    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            for (int i = 0; i < 2; i++) {
                items.add(new ItemStack(this, 1, i));
            }
        }
    }

}
