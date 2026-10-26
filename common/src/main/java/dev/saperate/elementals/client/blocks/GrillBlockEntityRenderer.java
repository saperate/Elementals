package dev.saperate.elementals.client.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.saperate.elementals.blocks.GrillBlock;
import dev.saperate.elementals.blocks.blockEntities.GrillBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

public class GrillBlockEntityRenderer implements BlockEntityRenderer<GrillBlockEntity> {

    public GrillBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super();
    }
    
    @Override
    public void render(GrillBlockEntity blockEntity, float v, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();

        // We this instead of toYRot() because west and east have to be reversed to be consistent
        Direction dir = GrillBlock.getDirection(blockEntity);
        if(dir.getAxis() == Direction.Axis.X){
            dir = dir.getOpposite();
        }
        
        poseStack.translate(0.5f, 0 ,0.5f);
        poseStack.mulPose(Axis.YP.rotation((float) (Math.toRadians(dir.toYRot()))));
        poseStack.translate(-0.5f, 0, -0.5f);
        
        if(blockEntity.hasCampfire()) {
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(blockEntity.getCampfire(), poseStack, bufferSource, packedLight, packedOverlay);
        }
        
        if(blockEntity.hasContainedStack()){
            poseStack.translate(0, 12f/16f, 0);
            ItemStack stack = blockEntity.getContainedStack();
            
            if(stack.getItem() instanceof BlockItem blockItem){
                Minecraft.getInstance().getBlockRenderer().renderSingleBlock(blockItem.getBlock().defaultBlockState(), poseStack, bufferSource, packedLight, packedOverlay);
            }else{
                //Not rendering for now, later though, it will place the item on a 90* rotation on top of the grill
            }
            
        }
        
        poseStack.popPose();
    }
}
