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
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import org.apache.commons.lang3.StringUtils;

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
            entity.setHealth((float)(double)mobConfig.getHp());
        }
        if (isValid(mobConfig.getHeldItem())) {
            ItemStack stack = progress.getNamedItemStack(mobConfig.getHeldItem());
            if (stack != null && ItemStackTools.isValid(stack)) {
                entity.setHeldItem(EnumHand.MAIN_HAND, stack.copy());
            } else {
                throw new ExecutionException("Could not find item '" + mobConfig.getHeldItem() + "'!");
            }
        }
        if (isValid(mobConfig.getHelmet())) {
            ItemStack stack = progress.getNamedItemStack(mobConfig.getHelmet());
            if (stack != null && ItemStackTools.isValid(stack)) {
                entity.setItemStackToSlot(EntityEquipmentSlot.HEAD, stack.copy());
            } else {
                throw new ExecutionException("Could not find item '" + mobConfig.getHelmet() + "'!");
            }
        }
        if (isValid(mobConfig.getChestplate())) {
            ItemStack stack = progress.getNamedItemStack(mobConfig.getChestplate());
            if (stack != null && ItemStackTools.isValid(stack)) {
                entity.setItemStackToSlot(EntityEquipmentSlot.CHEST, stack.copy());
            } else {
                throw new ExecutionException("Could not find item '" + mobConfig.getChestplate() + "'!");
            }
        }
        if (isValid(mobConfig.getLeggings())) {
            ItemStack stack = progress.getNamedItemStack(mobConfig.getLeggings());
            if (stack != null && ItemStackTools.isValid(stack)) {
                entity.setItemStackToSlot(EntityEquipmentSlot.LEGS, stack.copy());
            } else {
                throw new ExecutionException("Could not find item '" + mobConfig.getLeggings() + "'!");
            }
        }
        if (isValid(mobConfig.getBoots())) {
            ItemStack stack = progress.getNamedItemStack(mobConfig.getBoots());
            if (stack != null && ItemStackTools.isValid(stack)) {
                entity.setItemStackToSlot(EntityEquipmentSlot.FEET, stack.copy());
            } else {
                throw new ExecutionException("Could not find item '" + mobConfig.getBoots() + "'!");
            }
        }
        WorldTools.spawnEntity(world, entity);
    }

    private boolean isValid(ItemStack heldItem) {
        return heldItem != null && ItemStackTools.isValid(heldItem);
    }
}
