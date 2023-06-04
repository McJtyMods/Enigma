package mcjty.enigma.code;

import mcjty.enigma.compat.LostCitySupport;
import mcjty.enigma.parser.Expression;
import mcjty.enigma.parser.ExpressionContext;
import mcjty.enigma.parser.ExpressionFunction;
import mcjty.enigma.parser.ObjectTools;
import mcjty.enigma.progress.MobConfig;
import mcjty.enigma.progress.PlayerProgress;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import mcjty.enigma.setup.ModSetup;
import mcjty.enigma.varia.*;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.*;

import static mcjty.enigma.varia.StringRegister.STRINGS;

public class EnigmaExpressionContext implements ExpressionContext<EnigmaFunctionContext> {

    private final Progress progress;

    public EnigmaExpressionContext(Progress progress) {
        this.progress = progress;
    }

    public static final Map<String, ExpressionFunction<EnigmaFunctionContext>> FUNCTIONS = new HashMap<>();

    private static Random random = new Random(System.currentTimeMillis());

    static {
        FUNCTIONS.put("fmt_black", (context, o) -> String.valueOf(TextFormatting.BLACK));
        FUNCTIONS.put("fmt_darkblue", (context, o) -> String.valueOf(TextFormatting.DARK_BLUE));
        FUNCTIONS.put("fmt_darkgreen", (context, o) -> String.valueOf(TextFormatting.DARK_GREEN));
        FUNCTIONS.put("fmt_darkaqua", (context, o) -> String.valueOf(TextFormatting.DARK_AQUA));
        FUNCTIONS.put("fmt_darkred", (context, o) -> String.valueOf(TextFormatting.DARK_RED));
        FUNCTIONS.put("fmt_darkpurple", (context, o) -> String.valueOf(TextFormatting.DARK_PURPLE));
        FUNCTIONS.put("fmt_gold", (context, o) -> String.valueOf(TextFormatting.GOLD));
        FUNCTIONS.put("fmt_gray", (context, o) -> String.valueOf(TextFormatting.GRAY));
        FUNCTIONS.put("fmt_darkgray", (context, o) -> String.valueOf(TextFormatting.DARK_GRAY));
        FUNCTIONS.put("fmt_blue", (context, o) -> String.valueOf(TextFormatting.BLUE));
        FUNCTIONS.put("fmt_green", (context, o) -> String.valueOf(TextFormatting.GREEN));
        FUNCTIONS.put("fmt_aqua", (context, o) -> String.valueOf(TextFormatting.AQUA));
        FUNCTIONS.put("fmt_red", (context, o) -> String.valueOf(TextFormatting.RED));
        FUNCTIONS.put("fmt_lightpurple", (context, o) -> String.valueOf(TextFormatting.LIGHT_PURPLE));
        FUNCTIONS.put("fmt_yellow", (context, o) -> String.valueOf(TextFormatting.YELLOW));
        FUNCTIONS.put("fmt_white", (context, o) -> String.valueOf(TextFormatting.WHITE));
        FUNCTIONS.put("fmt_bold", (context, o) -> String.valueOf(TextFormatting.BOLD));
        FUNCTIONS.put("fmt_strikethrough", (context, o) -> String.valueOf(TextFormatting.STRIKETHROUGH));
        FUNCTIONS.put("fmt_underline", (context, o) -> String.valueOf(TextFormatting.UNDERLINE));
        FUNCTIONS.put("fmt_italic", (context, o) -> String.valueOf(TextFormatting.ITALIC));
        FUNCTIONS.put("fmt_reset", (context, o) -> String.valueOf(TextFormatting.RESET));

        FUNCTIONS.put("lc_valid", (context, o) -> ModSetup.lostcities && LostCitySupport.isLostCity(context.getWorld()));
        FUNCTIONS.put("lc_floor0", (context, o) -> {
            if (ModSetup.lostcities) {
                Progress progress = ProgressHolder.getProgress(context.getWorld());
                BlockPosDim namedPosition = progress.getNamedPosition(o[0]);
                return LostCitySupport.getFloor0Height(context.getWorld(), namedPosition.getPos());
            } else {
                return -1;
            }
        });

        FUNCTIONS.put("property", (context, o) -> {
            Progress progress = ProgressHolder.getProgress(context.getWorld());
            BlockPosDim namedPosition = progress.getNamedPosition(o[0]);
            if (namedPosition == null) {
                throw new ExecutionException("Cannot find position " + o[0] + "!");
            }
            IBlockState state = namedPosition.getWorld().getBlockState(namedPosition.getPos());
            String propertyName = ObjectTools.asStringSafe(o[1]);
            // Get property value
            return state.getProperties().entrySet().stream()
                    .filter(entry -> entry.getKey().getName().equals(propertyName))
                    .findFirst()
                    .map(Map.Entry::getValue)
                    .orElseThrow(() -> new ExecutionException("Cannot find property " + propertyName + " for block " + namedPosition.getPos()));
        });

        FUNCTIONS.put("pos", (context, o) -> {
            if (o.length == 1) {
                Progress progress = ProgressHolder.getProgress(context.getWorld());
                BlockPosDim namedPosition = progress.getNamedPosition(o[0]);
                if (namedPosition == null) {
                    MobConfig mobConfig = progress.getNamedMobConfig(o[0]);
                    if (mobConfig == null) {
                        throw new ExecutionException("Cannot find position or mob " + o[0] + "!");
                    }
                    String tag = "enigma:" + (o[0] instanceof String ? (String) o[0] : STRINGS.get((Integer) o[0]));
                    String t2 = ObjectTools.asStringSafe(o[0]);
                    List<Entity> entities = context.getWorld().getEntities(Entity.class, input -> (input.getTags().contains(tag) || input.getTags().contains(t2)));
                    for (Entity e : entities) {
                        return new BlockPosDim(e.getPosition(), e.dimension);
                    }
                }
                return namedPosition;
            } else if (o.length > 3) {
                return new BlockPosDim(new BlockPos(ObjectTools.asIntSafe(o[0]), ObjectTools.asIntSafe(o[1]), ObjectTools.asIntSafe(o[2])), ObjectTools.asIntSafe(o[3]));
            } else {
                int dim = 0;
                if (context.hasPlayer()) {
                    dim = context.getPlayer().getEntityWorld().provider.getDimension();
                }
                return new BlockPosDim(new BlockPos(ObjectTools.asIntSafe(o[0]), ObjectTools.asIntSafe(o[1]), ObjectTools.asIntSafe(o[2])), dim);
            }
        });
        FUNCTIONS.put("playerpos", (context, o) -> new BlockPosDim(context.getPlayer().getPosition(), context.getPlayer().getEntityWorld().provider.getDimension()));
        FUNCTIONS.put("playername", (context, o) -> context.getPlayer().getName());
        FUNCTIONS.put("getx", (context, o) -> {
            Progress progress = ProgressHolder.getProgress(context.getWorld());
            BlockPosDim namedPosition = progress.getNamedPosition(o[0]);
            if (namedPosition == null) {
                throw new ExecutionException("Cannot find position " + o[0] + "!");
            }
            return namedPosition.getPos().getX();
        });
        FUNCTIONS.put("gety", (context, o) -> {
            Progress progress = ProgressHolder.getProgress(context.getWorld());
            BlockPosDim namedPosition = progress.getNamedPosition(o[0]);
            if (namedPosition == null) {
                throw new ExecutionException("Cannot find position " + o[0] + "!");
            }
            return namedPosition.getPos().getY();
        });
        FUNCTIONS.put("getz", (context, o) -> {
            Progress progress = ProgressHolder.getProgress(context.getWorld());
            BlockPosDim namedPosition = progress.getNamedPosition(o[0]);
            if (namedPosition == null) {
                throw new ExecutionException("Cannot find position " + o[0] + "!");
            }
            return namedPosition.getPos().getZ();
        });
        FUNCTIONS.put("mincorner", (context, o) -> {
            Progress progress = ProgressHolder.getProgress(context.getWorld());
            Area namedArea = progress.getNamedArea(o[0]);
            if (namedArea == null) {
                throw new ExecutionException("Cannot find area " + o[0] + "!");
            }
            return new BlockPosDim(namedArea.getPos1(), namedArea.getDimension());
        });
        FUNCTIONS.put("maxcorner", (context, o) -> {
            Progress progress = ProgressHolder.getProgress(context.getWorld());
            Area namedArea = progress.getNamedArea(o[0]);
            if (namedArea == null) {
                throw new ExecutionException("Cannot find area " + o[0] + "!");
            }
            return new BlockPosDim(namedArea.getPos2(), namedArea.getDimension());
        });
        FUNCTIONS.put("getdim", (context, o) -> {
            IPositional positional = AreaTools.getPositional(ProgressHolder.getProgress(context.getWorld()), o[0]);
            return positional.getDimension();
        });
        FUNCTIONS.put("up", (context, o) -> {
            IPositional positional = AreaTools.getPositional(ProgressHolder.getProgress(context.getWorld()), o[0]);
            return positional.up(ObjectTools.asIntSafe(o[1]));
        });
        FUNCTIONS.put("down", (context, o) -> {
            IPositional positional = AreaTools.getPositional(ProgressHolder.getProgress(context.getWorld()), o[0]);
            return positional.down(ObjectTools.asIntSafe(o[1]));
        });
        FUNCTIONS.put("south", (context, o) -> {
            IPositional positional = AreaTools.getPositional(ProgressHolder.getProgress(context.getWorld()), o[0]);
            return positional.south(ObjectTools.asIntSafe(o[1]));
        });
        FUNCTIONS.put("north", (context, o) -> {
            IPositional positional = AreaTools.getPositional(ProgressHolder.getProgress(context.getWorld()), o[0]);
            return positional.north(ObjectTools.asIntSafe(o[1]));
        });
        FUNCTIONS.put("west", (context, o) -> {
            IPositional positional = AreaTools.getPositional(ProgressHolder.getProgress(context.getWorld()), o[0]);
            return positional.west(ObjectTools.asIntSafe(o[1]));
        });
        FUNCTIONS.put("east", (context, o) -> {
            IPositional positional = AreaTools.getPositional(ProgressHolder.getProgress(context.getWorld()), o[0]);
            return positional.east(ObjectTools.asIntSafe(o[1]));
        });
        FUNCTIONS.put("lookat", (context, o) -> {
            double maxdist = ObjectTools.asDoubleSafe(o[0]);
            EntityPlayer player = context.getPlayer();
            if (player == null) {
                throw new ExecutionException("Player is needed for 'lookat' function!");
            }
            World world = player.getEntityWorld();
            int dimension = world.provider.getDimension();
            Vec3d offset = player.getLookVec().normalize().scale(maxdist);
            Vec3d pp = new Vec3d(player.posX, player.posY, player.posZ);
            RayTraceResult result = world.rayTraceBlocks(pp, pp.add(offset));
            if (result == null) {
                return null;
            }
            return new BlockPosDim(result.getBlockPos(), dimension);
        });
        FUNCTIONS.put("posat", (context, o) -> {
            double dist = ObjectTools.asDoubleSafe(o[0]);
            EntityPlayer player = context.getPlayer();
            if (player == null) {
                throw new ExecutionException("Player is needed for 'posat' function!");
            }
            Vec3d offset = player.getLookVec().normalize().scale(dist);
            return new BlockPosDim(player.getPosition().add(offset.x, offset.y, offset.z), player.getEntityWorld().provider.getDimension());
        });
        FUNCTIONS.put("yaw", (context, o) -> {
            EntityPlayer player = context.getPlayer();
            if (player == null) {
                throw new ExecutionException("Player is needed for 'yaw' function!");
            }
            if (o.length == 0) {
                return (double) player.rotationYaw;
            } else {
                Progress progress = ProgressHolder.getProgress(context.getWorld());
                BlockPosDim namedPosition = progress.getNamedPosition(o[0]);
                if (namedPosition == null) {
                    throw new ExecutionException("Cannot find position " + o[0] + "!");
                }
                BlockPos p = namedPosition.getPos();
                double d0 = p.getX() + .5 - player.posX;
                double d1 = p.getY() + .5 - player.posY - player.getEyeHeight();
                double d2 = p.getZ() + .5 - player.posZ;

                double d3 = Math.sqrt(d0 * d0 + d2 * d2);
                return (Math.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
            }
        });
        FUNCTIONS.put("pitch", (context, o) -> {
            EntityPlayer player = context.getPlayer();
            if (player == null) {
                throw new ExecutionException("Player is needed for 'pitch' function!");
            }
            if (o.length == 0) {
                return (double) player.rotationPitch;
            } else {
                Progress progress = ProgressHolder.getProgress(context.getWorld());
                BlockPosDim namedPosition = progress.getNamedPosition(o[0]);
                if (namedPosition == null) {
                    throw new ExecutionException("Cannot find position " + o[0] + "!");
                }
                BlockPos p = namedPosition.getPos();
                double d0 = p.getX() + .5 - player.posX;
                double d1 = p.getY() + .5 - player.posY - player.getEyeHeight();
                double d2 = p.getZ() + .5 - player.posZ;

                double d3 = Math.sqrt(d0 * d0 + d2 * d2);
                return (-(Math.atan2(d1, d3) * (180D / Math.PI)));
            }
        });

        FUNCTIONS.put("istr", (context, o) -> {
            if (o[0] instanceof Integer) {
                return STRINGS.get((Integer) o[0]);
            } else {
                return ObjectTools.asStringSafe(o[0]);
            }
        });
        FUNCTIONS.put("intern", (context, o) -> {
            if (o[0] instanceof String) {
                return STRINGS.get((String) o[0]);
            } else {
                return o[0];
            }
        });
        FUNCTIONS.put("max", (context, o) -> {
            if (o[0] instanceof Integer) {
                int m = ObjectTools.asIntSafe(o[0]);
                for (int i = 1 ; i < o.length ; i++) {
                    m = Math.max(m, ObjectTools.asIntSafe(o[i]));
                }
                return m;
            } else if (o[0] instanceof Double) {
                double m = ObjectTools.asDoubleSafe(o[0]);
                for (int i = 1 ; i < o.length ; i++) {
                    m = Math.max(m, ObjectTools.asDoubleSafe(o[i]));
                }
                return m;
            } else if (o[0] instanceof String) {
                String m = ObjectTools.asStringSafe(o[0]);
                for (int i = 1 ; i < o.length ; i++) {
                    String s = ObjectTools.asStringSafe(o[1]);
                    m = m.compareTo(s) < 0 ? s : m;
                }
                return m;
            } else if (o[0] instanceof Boolean) {
                boolean m = ObjectTools.asBoolSafe(o[0]);
                for (int i = 1 ; i < o.length ; i++) {
                    m = m || ObjectTools.asBoolSafe(o[i]);
                }
                return m;
            } else {
                return o[0];
            }
        });
        FUNCTIONS.put("min", (context, o) -> {
            if (o[0] instanceof Integer) {
                int m = ObjectTools.asIntSafe(o[0]);
                for (int i = 1 ; i < o.length ; i++) {
                    m = Math.min(m, ObjectTools.asIntSafe(o[i]));
                }
                return m;
            } else if (o[0] instanceof Double) {
                double m = ObjectTools.asDoubleSafe(o[0]);
                for (int i = 1 ; i < o.length ; i++) {
                    m = Math.min(m, ObjectTools.asDoubleSafe(o[i]));
                }
                return m;
            } else if (o[0] instanceof String) {
                String m = ObjectTools.asStringSafe(o[0]);
                for (int i = 1 ; i < o.length ; i++) {
                    String s = ObjectTools.asStringSafe(o[1]);
                    m = m.compareTo(s) < 0 ? m : s;
                }
                return m;
            } else if (o[0] instanceof Boolean) {
                boolean m = ObjectTools.asBoolSafe(o[0]);
                for (int i = 1 ; i < o.length ; i++) {
                    m = m && ObjectTools.asBoolSafe(o[i]);
                }
                return m;
            } else {
                return o[0];
            }
        });
        FUNCTIONS.put("str", (context, o) -> ObjectTools.asStringSafe(o[0]));
        FUNCTIONS.put("int", (context, o) -> ObjectTools.asIntSafe(o[0]));
        FUNCTIONS.put("double", (context, o) -> ObjectTools.asDoubleSafe(o[0]));
        FUNCTIONS.put("boolean", (context, o) -> ObjectTools.asBoolSafe(o[0]));

        FUNCTIONS.put("random", (context, o) -> random.nextDouble());

        FUNCTIONS.put("state", (context, o) -> {
            Progress progress = ProgressHolder.getProgress(context.getWorld());
            return progress.getState(o[0]);
        });
        FUNCTIONS.put("pstate", (context, o) -> {
            Progress progress = ProgressHolder.getProgress(context.getWorld());
            PlayerProgress playerProgress = progress.getPlayerProgress(context.getPlayer().getPersistentID());
            return playerProgress.getState(o[0]);
        });
        FUNCTIONS.put("blockname", (context, o) -> {
            Progress progress = ProgressHolder.getProgress(context.getWorld());
            BlockPosDim namedPosition = progress.getNamedPosition(o[0]);
            if (namedPosition == null) {
                throw new ExecutionException("Cannot find position " + o[0] + "!");
            }
            IBlockState state = DimensionManager.getWorld(namedPosition.getDimension()).getBlockState(namedPosition.getPos());
            if (state == null) {
                return "";  // Not a named block
            }
            return STRINGS.get(progress.getNamedBlock(state));
        });
        FUNCTIONS.put("substring", (context, o) -> {
            String s = ObjectTools.asStringSafe(o[0]);
            int idx1 = ObjectTools.asIntSafe(o[1]);
            if (o.length > 2) {
                int idx2 = ObjectTools.asIntSafe(o[2]);
                return s.substring(idx1, idx2);
            } else {
                return s.substring(idx1);
            }
        });
        FUNCTIONS.put("isblock", (context, o) -> {
            Progress progress = ProgressHolder.getProgress(context.getWorld());
            BlockPosDim namedPosition = progress.getNamedPosition(o[0]);
            if (namedPosition == null) {
                throw new ExecutionException("Cannot find position " + o[0] + "!");
            }
            IBlockState state = progress.getNamedBlock(o[1]);
            if (state == null) {
                throw new ExecutionException("Cannot find block state " + o[1] + "!");
            }
            return DimensionManager.getWorld(namedPosition.getDimension()).getBlockState(namedPosition.getPos()).equals(state);
        });
        FUNCTIONS.put("itemstack", (context, o) -> {
            if (o[0] instanceof String) {
                int amount = 1;
                int meta = 0;
                if (o.length > 1) {
                    amount = ObjectTools.asIntSafe(o[1]);
                    if (o.length > 2) {
                        meta = ObjectTools.asIntSafe(o[2]);
                    }
                }
                String n = ObjectTools.asStringSafe(o[0]);
                Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(n));
                if (item != null) {
                    return new ItemStack(item, amount, meta);
                }
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(n));
                if (block != null) {
                    return new ItemStack(block, amount, meta);
                }
                throw new ExecutionException("Cannot find item or block '" + n + "'!");
            } else {
                Progress progress = ProgressHolder.getProgress(context.getWorld());
                return progress.getNamedItemStack(o[0]);
            }
        });
        FUNCTIONS.put("inv_count", (context, o) -> {
            // Count the number of items in the inventory
            Progress progress = ProgressHolder.getProgress(context.getWorld());
            BlockPosDim namedPosition = progress.getNamedPosition(o[0]);
            TileEntity te = namedPosition.getWorld().getTileEntity(namedPosition.getPos());
            if (te == null) {
                return -1;
            }
            if (te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
                IItemHandler handler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                if (handler != null) {
                    int cnt = 0;
                    for (int i = 0 ; i < handler.getSlots() ; i++) {
                        ItemStack stack = handler.getStackInSlot(i);
                        cnt += stack.getCount();
                    }
                    return cnt;
                }
            }
            return -1;
        });
        FUNCTIONS.put("inv_getcount", (context, o) -> {
            // Check the stack size of the item in the specific inventory (at position) at the specific slot
            Progress progress = ProgressHolder.getProgress(context.getWorld());
            BlockPosDim namedPosition = progress.getNamedPosition(o[0]);
            int slot = ObjectTools.asIntSafe(o[1]);
            TileEntity te = namedPosition.getWorld().getTileEntity(namedPosition.getPos());
            if (te == null) {
                return -1;
            }
            if (te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
                IItemHandler handler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                if (handler != null) {
                    if (slot >= handler.getSlots() || slot < 0) {
                        throw new ExecutionException("Slot " + slot + " is out of bounds!");
                    }
                    ItemStack stack = handler.getStackInSlot(slot);
                    if (stack.isEmpty()) {
                        return 0;
                    } else {
                        return stack.getCount();
                    }
                }
            }
            return -1;
        });
        FUNCTIONS.put("inv_get", (context, o) -> {
            // Check the stack of the item in the specific inventory (at position) at the specific slot
            Progress progress = ProgressHolder.getProgress(context.getWorld());
            BlockPosDim namedPosition = progress.getNamedPosition(o[0]);
            int slot = ObjectTools.asIntSafe(o[1]);
            TileEntity te = namedPosition.getWorld().getTileEntity(namedPosition.getPos());
            if (te == null) {
                return ItemStack.EMPTY;
            }
            if (te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
                IItemHandler handler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                if (handler != null) {
                    if (slot >= handler.getSlots() || slot < 0) {
                        throw new ExecutionException("Slot " + slot + " is out of bounds!");
                    }
                    return handler.getStackInSlot(slot);
                }
            }
            return ItemStack.EMPTY;
        });
        FUNCTIONS.put("hasitem", (context, o) -> {
            Progress progress = ProgressHolder.getProgress(context.getWorld());
            ItemStack stack = progress.getNamedItemStack(o[0]);
            if (!stack.isEmpty()) {
                List<ItemStack> items = Collections.unmodifiableList(context.getPlayer().inventory.mainInventory);
                for (ItemStack item : items) {
                    if (!item.isEmpty()) {
                        if (InventoryHelper.stackEqualExact(item, stack)) {
                            return true;
                        }
                    }
                }
            } else {
                throw new ExecutionException("Cannot find item " + o[0] + "!");
            }
            return false;
        });
        FUNCTIONS.put("hasitemmain", (context, o) -> {
            Progress progress = ProgressHolder.getProgress(context.getWorld());
            ItemStack stack = progress.getNamedItemStack(o[0]);
            if (!stack.isEmpty()) {
                if (InventoryHelper.stackEqualExact(stack, context.getPlayer().getHeldItem(EnumHand.MAIN_HAND))) {
                    return true;
                }
            } else {
                throw new ExecutionException("Cannot find item " + o[0] + "!");
            }
            return false;
        });
        FUNCTIONS.put("hasitemoff", (context, o) -> {
            Progress progress = ProgressHolder.getProgress(context.getWorld());
            ItemStack stack = progress.getNamedItemStack(o[0]);
            if (!stack.isEmpty()) {
                if (InventoryHelper.stackEqualExact(stack, context.getPlayer().getHeldItem(EnumHand.OFF_HAND))) {
                    return true;
                }
            } else {
                throw new ExecutionException("Cannot find item " + o[0] + "!");
            }
            return false;
        });
        FUNCTIONS.put("distance", (context, o) -> {
            Progress progress = ProgressHolder.getProgress(context.getWorld());
            BlockPosDim pos1 = progress.getNamedPosition(o[0]);
            if (pos1 == null) {
                throw new ExecutionException("Cannot find position " + o[0] + "!");
            }
            BlockPos pos2;
            if (o.length > 1) {
                BlockPosDim p2 = progress.getNamedPosition(o[1]);
                if (p2 == null) {
                    throw new ExecutionException("Cannot find position " + o[1] + "!");
                }
                if (pos1.getDimension() != p2.getDimension()) {
                    return -1;
                }
                pos2 = p2.getPos();
            } else {
                EntityPlayer player = context.getPlayer();
                if (player.getEntityWorld().provider.getDimension() != pos1.getDimension()) {
                    return -1;
                }
                pos2 = player.getPosition();
            }
            return BlockPosDim.distance(pos1.getPos(), pos2);
        });

    }

    @Nullable
    @Override
    public Expression<EnigmaFunctionContext> getVariable(String var) {
        int i = STRINGS.get(var);
        return context -> {
            if (context.isLocalVar(i)) {
                return context.getLocalVar(i);
            } else if (context.hasPlayer() && progress.getPlayerProgress(context.getPlayer().getPersistentID()).isNamedVariable(i)) {
                return progress.getPlayerProgress(context.getPlayer().getPersistentID()).getNamedVariable(i);
            } else if (progress.isNamedVariable(i)) {
                return progress.getNamedVariable(i);
            } else {
                return var;
            }
        };
    }

    @Nullable
    @Override
    public ExpressionFunction<EnigmaFunctionContext> getFunction(String name) {
        return FUNCTIONS.get(name);
    }

    @Override
    public boolean isFunction(String name) {
        return FUNCTIONS.containsKey(name);
    }
}
