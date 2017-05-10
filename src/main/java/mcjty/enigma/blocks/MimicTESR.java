package mcjty.enigma.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import java.util.Collections;

public class MimicTESR extends TileEntitySpecialRenderer<MimicTE> {

    private static final BlockRenderLayer[] LAYERS = BlockRenderLayer.values();

    private final FakeMimicWorld fakeWorld = new FakeMimicWorld();

    @Override
    public void renderTileEntityAt(MimicTE te, double x, double y, double z, float partialTicks, int destroyStage) {
        IBlockState mimicState = te.getToMimic();
        if (mimicState == null) {
            return;
        }

        GlStateManager.pushMatrix();

        RenderHelper.disableStandardItemLighting();
        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
        } else {
            GlStateManager.shadeModel(GL11.GL_FLAT);
        }

        Tessellator tessellator = Tessellator.getInstance();
        BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        BlockRenderLayer origLayer = MinecraftForgeClient.getRenderLayer();

        fakeWorld.setState(mimicState, Collections.singleton(te.getPos()));

        for (BlockRenderLayer layer : LAYERS) {
            if (mimicState.getBlock().canRenderInLayer(mimicState, layer)) {
                ForgeHooksClient.setRenderLayer(layer);
                if (layer == BlockRenderLayer.TRANSLUCENT) {
                    GlStateManager.enableBlend();
                }
                if (te.isBlendColor()) {
                    GlStateManager.enableBlend();
                    GlStateManager.blendFunc(GL11.GL_CONSTANT_COLOR, GL11.GL_ONE_MINUS_CONSTANT_COLOR);
                    GL14.glBlendColor((float) te.getRed(), (float) te.getGreen(), (float) te.getBlue(), 0.0f);
                }

                for (BlockPos pos : fakeWorld.getPositions()) {
                    tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
                    tessellator.getBuffer().setTranslation(x - te.getPos().getX() + te.getDx(), y - te.getPos().getY() + te.getDy(), z - te.getPos().getZ() + te.getDz());
                    renderBlock(dispatcher, mimicState, pos, fakeWorld, tessellator.getBuffer());
                    tessellator.draw();
                }

                if (te.isBlendColor()) {
                    GlStateManager.disableBlend();
                    GL14.glBlendColor(1, 1, 1, 1);
                    GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                }
                if (layer == BlockRenderLayer.TRANSLUCENT) {
                    GlStateManager.disableBlend();
                }
            }
        }

        ForgeHooksClient.setRenderLayer(origLayer);
        tessellator.getBuffer().setTranslation(0, 0, 0);

        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
    }



    private static boolean renderBlock(BlockRendererDispatcher dispatcher, IBlockState state, BlockPos pos, IBlockAccess blockAccess, VertexBuffer worldRendererIn) {
        try {
            EnumBlockRenderType enumblockrendertype = state.getRenderType();

            if (enumblockrendertype == EnumBlockRenderType.INVISIBLE) {
                return false;
            } else {
                if (blockAccess.getWorldType() != WorldType.DEBUG_WORLD) {
                    try {
                        state = state.getActualState(blockAccess, pos);
                    } catch (Exception var8) {
                    }
                }

                switch (enumblockrendertype) {
                    case MODEL:
                        IBakedModel model = dispatcher.getModelForState(state);
                        state = state.getBlock().getExtendedState(state, blockAccess, pos);
                        return dispatcher.getBlockModelRenderer().renderModel(blockAccess, model, state, pos, worldRendererIn, false);
                    case ENTITYBLOCK_ANIMATED:
                        return false;
                    default:
                        return false;
                }
            }
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Tesselating block in world");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being tesselated");
            CrashReportCategory.addBlockInfo(crashreportcategory, pos, state.getBlock(), state.getBlock().getMetaFromState(state));
            throw new ReportedException(crashreport);
        }
    }


}
