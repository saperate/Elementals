package dev.saperate.elementals.client.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.saperate.elementals.blocks.GrillBlock;
import dev.saperate.elementals.blocks.blockEntities.GrillBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class GrillBlockEntityRenderer implements BlockEntityRenderer<GrillBlockEntity> {

    public GrillBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super();
    }
    
    @Override
    public void render(GrillBlockEntity blockEntity, float v, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        //poseStack.mulPose(GrillBlock.getDirection(blockEntity).getRotation());
        
        if(blockEntity.hasCampfire()) {
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(blockEntity.getCampfire(), poseStack, bufferSource, packedLight, packedOverlay);
        }
        
        if(blockEntity.hasContainedBlock()){
            poseStack.translate(0, 12f/16f, 0);
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(blockEntity.getContainedBlock(), poseStack, bufferSource, packedLight, packedOverlay);
        }
        
        poseStack.popPose();
    }
}
