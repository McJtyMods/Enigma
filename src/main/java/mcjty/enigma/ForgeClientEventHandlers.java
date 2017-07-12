package mcjty.enigma;

import mcjty.enigma.fxanim.FxAnimationHandler;
import mcjty.enigma.overlays.OverlayRenderer;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ForgeClientEventHandlers {

    @SubscribeEvent
    public void onClientTickEvent(TickEvent.ClientTickEvent event) {
        FxAnimationHandler.tick();
        OverlayRenderer.tickMessages();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void renderGameOverlayEvent(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.TEXT) {
            OverlayRenderer.renderOverlays();
        } else if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            OverlayRenderer.renderOverlayColor();
        }
    }

    public void renderX(RenderGameOverlayEvent.Post event) {

    }

    public static boolean cancelChat = false;

    @SubscribeEvent
    public void onClientChatReceived(ClientChatReceivedEvent event) {
        if (cancelChat) {
            event.setCanceled(true);
        }
    }
}
