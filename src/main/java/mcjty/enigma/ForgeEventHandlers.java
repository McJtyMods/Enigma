package mcjty.enigma;

import mcjty.enigma.code.EnigmaFunctionContext;
import mcjty.enigma.code.RootScope;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import mcjty.enigma.varia.BlockPosDim;
import mcjty.lib.tools.ItemStackTools;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.IOException;

public class ForgeEventHandlers {

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (!event.getWorld().isRemote) {
            File dataDir = new File(((WorldServer) event.getWorld()).getChunkSaveLocation(), "enigma");
            dataDir.mkdirs();
            File file = new File(dataDir, "autostart.esc");
            if (file.exists()) {
                Enigma.logger.log(Level.INFO, "Reading script from 'autostart.esc'!");
                try {
                    RootScope.setRoot(RootScope.readRules(event.getWorld(), file));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLogIn(PlayerEvent.PlayerLoggedInEvent event) {
        EntityPlayer player = event.player;
        World world = player.getEntityWorld();
        EnigmaFunctionContext context = new EnigmaFunctionContext(world, player);
        RootScope.getRootInstance(world).forActiveScopes(context, (ctxt, scope) -> scope.onLogin(ctxt));
    }

    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (!event.getWorld().isRemote && event.getHand() == EnumHand.MAIN_HAND) {
            EntityPlayer player = event.getEntityPlayer();
            World world = player.getEntityWorld();
            ItemStack stack = player.getHeldItemMainhand();
            if (ItemStackTools.isValid(stack)) {
                EnigmaFunctionContext context = new EnigmaFunctionContext(world, player);
                RootScope.getRootInstance(world).forActiveScopes(context, (ctxt, scope) -> scope.onRightClickItem(event, ctxt, stack));
                if (context.isCanceled()) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getWorld().isRemote && event.getHand() == EnumHand.MAIN_HAND) {
            EntityPlayer player = event.getEntityPlayer();
            World world = player.getEntityWorld();
            Progress progress = ProgressHolder.getProgress(world);
            Integer position = progress.getNamedPosition(event.getPos(), world.provider.getDimension());
            if (position != null) {
                EnigmaFunctionContext context = new EnigmaFunctionContext(world, player);
                RootScope.getRootInstance(world).forActiveScopes(context, (ctxt, scope) -> scope.onRightClickPosition(event, ctxt, position));
                if (context.isCanceled()) {
                    event.setCanceled(true);
                }
            }
            IBlockState state = world.getBlockState(event.getPos());
            Integer namedBlock = progress.getNamedBlock(state);
            if (namedBlock != null) {
                EnigmaFunctionContext context = new EnigmaFunctionContext(world, player);
                context.setLocalVar("event_pos", new BlockPosDim(event.getPos(), world.provider.getDimension()));
                RootScope.getRootInstance(world).forActiveScopes(context, (ctxt, scope) -> scope.onRightClickBlock(event, ctxt, namedBlock));
                if (context.isCanceled()) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (!event.getWorld().isRemote && event.getHand() == EnumHand.MAIN_HAND) {
            EntityPlayer player = event.getEntityPlayer();
            World world = player.getEntityWorld();
            Progress progress = ProgressHolder.getProgress(world);
            Integer position = progress.getNamedPosition(event.getPos(), world.provider.getDimension());
            if (position != null) {
                EnigmaFunctionContext context = new EnigmaFunctionContext(world, player);
                RootScope.getRootInstance(world).forActiveScopes(context, (ctxt, scope) -> scope.onLeftClickPosition(event, ctxt, position));
                if (context.isCanceled()) {
                    event.setCanceled(true);
                }
            }
            IBlockState state = world.getBlockState(event.getPos());
            Integer namedBlock = progress.getNamedBlock(state);
            if (namedBlock != null) {
                EnigmaFunctionContext context = new EnigmaFunctionContext(world, player);
                context.setLocalVar("event_pos", new BlockPosDim(event.getPos(), world.provider.getDimension()));
                RootScope.getRootInstance(world).forActiveScopes(context, (ctxt, scope) -> scope.onLeftClickBlock(event, ctxt, namedBlock));
                if (context.isCanceled()) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onServerTickEvent(TickEvent.ServerTickEvent event) {
        WorldServer world = DimensionManager.getWorld(0);
        Progress progress = ProgressHolder.getProgress(world);
        if (!progress.isRootActivated()) {
            // We set scope activity to false because the root has not been activated yet
            RootScope.getRootInstance(world).setActive(false);
            progress.setRootActivated(true);
            ProgressHolder.save(world);
        }

        EnigmaFunctionContext context = new EnigmaFunctionContext(world, null);
        RootScope.getRootInstance(world).checkActivity(context);
    }
}
