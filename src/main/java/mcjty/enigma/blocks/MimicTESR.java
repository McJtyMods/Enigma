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
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;

import java.util.Collections;

public class MimicTESR extends TileEntitySpecialRenderer<MimicTE> {

    private static final BlockRenderLayer[] LAYERS = BlockRenderLayer.values();

    private final FakeMimicWorld fakeWorld = new FakeMimicWorld();

    @Override
    public void renderTileEntityAt(MimicTE te, double x, double y, double z, float partialTicks, int destroyStage) {

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

        IBlockState mimicState = Blocks.DIAMOND_BLOCK.getDefaultState();
        fakeWorld.setState(mimicState, Collections.singleton(te.getPos()));

        for (BlockRenderLayer layer : LAYERS) {
            if (mimicState.getBlock().canRenderInLayer(mimicState, layer)) {
                ForgeHooksClient.setRenderLayer(layer);
                if (layer == BlockRenderLayer.TRANSLUCENT) {
                    GlStateManager.enableBlend();
                }

                for (BlockPos pos : fakeWorld.getPositions()) {
                    tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
                    tessellator.getBuffer().setTranslation(x - te.getPos().getX(), y - te.getPos().getY(), z - te.getPos().getZ());
//                    tessellator.getBuffer().setTranslation(x, y, z);
                    renderBlock(dispatcher, mimicState, pos, fakeWorld, tessellator.getBuffer());
                    tessellator.draw();
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
