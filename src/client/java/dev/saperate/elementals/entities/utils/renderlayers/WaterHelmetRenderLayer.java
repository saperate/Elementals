package dev.saperate.elementals.entities.utils.renderlayers;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public class WaterHelmetRenderLayer extends RenderLayer {

    public static RenderLayer getRenderLayer() {
        boolean affectsOutline = true;

        MultiPhaseParameters multiPhaseParameters = MultiPhaseParameters.builder()
                .program(ENTITY_TRANSLUCENT_PROGRAM)
                .texture(MIPMAP_BLOCK_ATLAS_TEXTURE)
                .cull(DISABLE_CULLING)
                .lightmap(ENABLE_LIGHTMAP)
                .overlay(ENABLE_OVERLAY_COLOR)
                .writeMaskState(COLOR_MASK)
                .build( affectsOutline);

        return RenderLayer.of("entity_waterhelmet", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
                VertexFormat.DrawMode.QUADS, 786432, false, true, multiPhaseParameters);
    }

    private WaterHelmetRenderLayer(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode,
                                          int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction,
                                          Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }

}
