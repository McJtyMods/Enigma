package mcjty.enigma.code;

import mcjty.enigma.parser.Expression;
import mcjty.enigma.progress.MobConfig;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import mcjty.enigma.varia.BlockPosDim;
import mcjty.lib.tools.EntityTools;
import mcjty.lib.tools.ItemStackTools;
import mcjty.lib.tools.WorldTools;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Consumer;

import static mcjty.enigma.varia.StringRegister.STRINGS;

public class SpawnAction extends Action {
    private final Expression<EnigmaFunctionContext> position;
    private final Expression<EnigmaFunctionContext> mob;

    public SpawnAction(Expression<EnigmaFunctionContext> position, Expression<EnigmaFunctionContext> mob) {
        this.position = position;
        this.mob = mob;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "Spawn: " + mob + " at " + position);
    }

    @Override
    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        Progress progress = ProgressHolder.getProgress(context.getWorld());
        Object pos = position.eval(context);
        BlockPosDim namedPosition = progress.getNamedPosition(pos);
        if (namedPosition == null) {
            throw new ExecutionException("Cannot find named position '" + pos + "'!");
        }
        Object m = mob.eval(context);
        MobConfig mobConfig = progress.getNamedMobConfig(m);
        if (mobConfig == null) {
            throw new ExecutionException("Cannot find named mob '" + m + "'!");
        }

        WorldServer world = DimensionManager.getWorld(namedPosition.getDimension());
        if (world == null) {
            world = world.getMinecraftServer().worldServerForDimension(namedPosition.getDimension());
        }
        EntityLiving entity = EntityTools.createEntity(world, mobConfig.getMobId());
        if (entity == null) {
            throw new ExecutionException("Could not spawn entity with id '" + mobConfig.getMobId() + "'!");
        }
        BlockPos p = namedPosition.getPos();
        entity.setLocationAndAngles(p.getX() + 0.5D, p.getY(), p.getZ() + 0.5D, 0.0F, 0.0F);
        if (mobConfig.getHp() != null) {
            IAttributeInstance entityAttribute = entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
            float hp = (float) (double) mobConfig.getHp();
            entityAttribute.setBaseValue(hp);
            entity.setHealth(hp);
        }
        if (mobConfig.getDamage() != null) {
            IAttributeInstance entityAttribute = entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
            float damage = (float) (double) mobConfig.getDamage();
            entityAttribute.setBaseValue(damage);
        }
        equip(mobConfig.getHeldItem(), progress, stack -> entity.setHeldItem(EnumHand.MAIN_HAND, stack));
        equip(mobConfig.getHelmet(), progress, stack -> entity.setItemStackToSlot(EntityEquipmentSlot.HEAD, stack));
        equip(mobConfig.getChestplate(), progress, stack -> entity.setItemStackToSlot(EntityEquipmentSlot.CHEST, stack));
        equip(mobConfig.getLeggings(), progress, stack -> entity.setItemStackToSlot(EntityEquipmentSlot.LEGS, stack));
        equip(mobConfig.getBoots(), progress, stack -> entity.setItemStackToSlot(EntityEquipmentSlot.FEET, stack));
        if (mobConfig.isAggressive() && context.hasPlayer()) {
            entity.setAttackTarget(context.getPlayer());
            entity.setLastAttacker(context.getPlayer());
        }

        String tag = "enigma:" + (m instanceof String ? (String) m : STRINGS.get((Integer) m));
        entity.addTag(tag);
        if (mobConfig.getTag() != null) {
            entity.addTag(mobConfig.getTag());
        }
        WorldTools.spawnEntity(world, entity);
    }

    private boolean isValid(ItemStack heldItem) {
        return heldItem != null && ItemStackTools.isValid(heldItem);
    }


    private void equip(ItemStack item, Progress progress, Consumer<ItemStack> consumer) throws ExecutionException {
        if (isValid(item)) {
            ItemStack stack = progress.getNamedItemStack(item);
            if (stack != null && ItemStackTools.isValid(stack)) {
                consumer.accept(stack.copy());
            } else {
                throw new ExecutionException("Could not find item '" + item.getDisplayName() + "'!");
            }
        }
    }
}
