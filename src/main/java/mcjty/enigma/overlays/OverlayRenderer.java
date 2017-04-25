package mcjty.enigma.overlays;

import net.minecraft.client.Minecraft;

public class OverlayRenderer {

    public static void renderOverlays() {
        TimedMessages.tick();
        int y = 30;
        for (TimedMessage message : TimedMessages.getMessages()) {
            RenderHelper.renderText(Minecraft.getMinecraft(), 30, y, message.getMessage());
            y += 10;
        }
    }
}
