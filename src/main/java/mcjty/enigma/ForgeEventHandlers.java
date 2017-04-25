package mcjty.enigma;

import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ForgeEventHandlers {

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getWorld().isRemote) {
            EntityPlayer player = event.getEntityPlayer();
            Progress progress = ProgressHolder.getProgress(player.getEntityWorld());
            Integer position = progress.getNamedPosition(event.getPos(), player.getEntityWorld().provider.getDimension());
            if (position != null) {
                Enigma.root.forActiveScopes(player, scope -> {
                    scope.onRightClickBlock(event, position);
                });
            }
        }
    }

    @SubscribeEvent
    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (!event.getWorld().isRemote) {
            EntityPlayer player = event.getEntityPlayer();
            Progress progress = ProgressHolder.getProgress(player.getEntityWorld());
            Integer position = progress.getNamedPosition(event.getPos(), player.getEntityWorld().provider.getDimension());
            if (position != null) {
                Enigma.root.forActiveScopes(player, scope -> {
                    scope.onLeftClickBlock(event, position);
                });
            }
        }
    }
}
