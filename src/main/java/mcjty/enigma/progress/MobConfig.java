package mcjty.enigma.progress;

import net.minecraft.item.ItemStack;

public class MobConfig {
    private final String mobId;
    private final Double hp;
    private final Double damage;
    private final ItemStack heldItem;
    private final ItemStack helmet;
    private final ItemStack chestplate;
    private final ItemStack leggings;
    private final ItemStack boots;
    private final boolean aggressive;

    public MobConfig(String mobId, Double hp, Double damage, ItemStack heldItem, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots, boolean aggressive) {
        this.mobId = mobId;
        this.hp = hp;
        this.damage = damage;
        this.heldItem = heldItem;
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
        this.aggressive = aggressive;
    }

    public String getMobId() {
        return mobId;
    }

    public Double getHp() {
        return hp;
    }

    public Double getDamage() {
        return damage;
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

    public boolean isAggressive() {
        return aggressive;
    }
}
