package mcjty.enigma.overlays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class OverlayRenderer {

    public static void tickMessages() {
        TimedMessages.tick();
    }

    public static void renderOverlays() {
        int y = 30;
        for (TimedMessage message : TimedMessages.getMessages()) {
            RenderHelper.renderText(Minecraft.getMinecraft(), 30, y, message.getMessage(), 1.5);
            y += 10;
        }
    }

    public static boolean useOverlay = false;
    public static float overlayAlpha = 0.0f;
    public static float overlayRed = 0.0f;
    public static float overlayGreen = 0.0f;
    public static float overlayBlue = 0.0f;

    public static void setOverlayColor(float alpha, float red, float green, float blue) {
        if (Math.abs(alpha) < 0.001) {
            useOverlay = false;
        }
        useOverlay = true;
        overlayAlpha = alpha;
        overlayRed = red;
        overlayGreen = green;
        overlayBlue = blue;
    }

    public static void renderOverlayColor() {
        if (!useOverlay) {
            return;
        }
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        int width = res.getScaledWidth();
        int height = res.getScaledHeight();
        renderOverlayColor(width, height, overlayAlpha, overlayRed, overlayGreen, overlayBlue);
    }

    public static void renderOverlayColor(int width, int height, float alpha, float red, float green, float blue) {
        GlStateManager.disableDepth();
        GlStateManager.disableAlpha();
        drawRect(0, 0, width, height, alpha, red, green, blue);
        GlStateManager.enableAlpha();
        GlStateManager.enableDepth();
    }

    public static void drawRect(int left, int top, int right, int bottom, float alpha, float red, float green, float blue) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(red, green, blue, alpha);
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION);
        vertexbuffer.pos(left, bottom, 0.0D).endVertex();
        vertexbuffer.pos(right, bottom, 0.0D).endVertex();
        vertexbuffer.pos(right, top, 0.0D).endVertex();
        vertexbuffer.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

}
