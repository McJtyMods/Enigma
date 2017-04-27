package mcjty.enigma;

import mcjty.enigma.code.EnigmaFunctionContext;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ForgeEventHandlers {

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (!event.getWorld().isRemote) {
            Enigma.root.init(new EnigmaFunctionContext(event.getWorld(), null));
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
                Enigma.root.forActiveScopes(context, scope -> scope.onRightClickBlock(event, context, position));
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
                Enigma.root.forActiveScopes(context, scope -> scope.onLeftClickBlock(event, context, position));
            }
        }
    }

    @SubscribeEvent
    public void onServerTickEvent(TickEvent.ServerTickEvent event) {
        WorldServer world = DimensionManager.getWorld(0);
        Enigma.root.init(new EnigmaFunctionContext(world, null));
        Progress progress = ProgressHolder.getProgress(world);
        if (!progress.isRootActivated()) {
            Enigma.root.setInactiveForRoot();
            progress.setRootActivated(true);
            ProgressHolder.save(world);
        }

        EnigmaFunctionContext context = new EnigmaFunctionContext(world, null);
        Enigma.root.checkActivity(context);
    }
}
