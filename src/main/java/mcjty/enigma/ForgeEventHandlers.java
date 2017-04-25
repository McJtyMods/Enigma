package mcjty.enigma;

import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ForgeEventHandlers {

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (!event.getWorld().isRemote) {
            Enigma.root.start(event.getWorld());
        }
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getWorld().isRemote) {
            EntityPlayer player = event.getEntityPlayer();
            World world = player.getEntityWorld();
            Progress progress = ProgressHolder.getProgress(world);
            Integer position = progress.getNamedPosition(event.getPos(), world.provider.getDimension());
            if (position != null) {
                Enigma.root.forActiveScopes(world, scope -> {
                    scope.onRightClickBlock(event, position);
                });
            }
        }
    }

    @SubscribeEvent
    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (!event.getWorld().isRemote) {
            EntityPlayer player = event.getEntityPlayer();
            World world = player.getEntityWorld();
            Progress progress = ProgressHolder.getProgress(world);
            Integer position = progress.getNamedPosition(event.getPos(), world.provider.getDimension());
            if (position != null) {
                Enigma.root.forActiveScopes(world, scope -> {
                    scope.onLeftClickBlock(event, position);
                });
            }
        }
    }
}
