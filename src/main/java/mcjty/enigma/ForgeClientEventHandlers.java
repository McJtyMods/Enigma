package mcjty.enigma;

import mcjty.enigma.code.EnigmaFunctionContext;
import mcjty.enigma.code.RootScope;
import mcjty.enigma.fxanim.FxAnimationHandler;
import mcjty.enigma.overlays.OverlayRenderer;
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
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.IOException;

public class ForgeClientEventHandlers {

    @SubscribeEvent
    public void onClientTickEvent(TickEvent.ClientTickEvent event) {
        FxAnimationHandler.tick();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void renderGameOverlayEvent(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.TEXT) {
            OverlayRenderer.renderOverlays();
        } else if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            OverlayRenderer.renderOverlayColor();
        }
    }

    public static boolean cancelChat = false;

    @SubscribeEvent
    public void onClientChatReceived(ClientChatReceivedEvent event) {
        if (cancelChat) {
            event.setCanceled(true);
        }
    }
}
