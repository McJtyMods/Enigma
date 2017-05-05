package mcjty.enigma.progress;

import net.minecraft.item.ItemStack;

public class MobConfig {
    private final String mobId;
    private final Double hp;
    private final ItemStack heldItem;
    private final ItemStack helmet;
    private final ItemStack chestplate;
    private final ItemStack leggings;
    private final ItemStack boots;

    public MobConfig(String mobId, Double hp, ItemStack heldItem, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots) {
        this.mobId = mobId;
        this.hp = hp;
        this.heldItem = heldItem;
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
    }

    public String getMobId() {
        return mobId;
    }

    public Double getHp() {
        return hp;
    }

    public ItemStack getHeldItem() {
        return heldItem;
    }

    public ItemStack getHelmet() {
        return helmet;
    }

    public ItemStack getChestplate() {
        return chestplate;
    }

    public ItemStack getLeggings() {
        return leggings;
    }

    public ItemStack getBoots() {
        return boots;
    }
}
