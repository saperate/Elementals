// Made with Blockbench 4.11.2
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports

package dev.saperate.elementals.client.entities.models.metal;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.saperate.elementals.entities.metal.MetalLanceEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class MetalLanceModel extends EntityModel<MetalLanceEntity> {
	private final ModelPart CoreTransition;
	private final ModelPart SpearHead;
	public MetalLanceModel(ModelPart root) {
		this.CoreTransition = root.getChild("CoreTransition");
		this.SpearHead = root.getChild("SpearHead");
	}
	public static LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		PartDefinition CoreTransition = modelPartData.addOrReplaceChild("CoreTransition", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -2.0F, -15.0F, 1.0F, 2.0F, 29.0F, new CubeDeformation(0.0F)), PartPose.rotation(0.0F, 24.0F, 1.0F));

		PartDefinition SpearHead = modelPartData.addOrReplaceChild("SpearHead", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 23.0F, 12.5F, 0.0F, 0.0F, 1.5708F));

		PartDefinition cube_r1 = SpearHead.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(14, 31).addBox(1.0F, -1.01F, -1.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.1F, 0.51F, -2.5F, 0.0F, -0.2094F, 0.0F));

		PartDefinition cube_r2 = SpearHead.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 31).addBox(-2.0F, -1.01F, -1.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(-0.1F, 0.51F, -2.5F, 0.0F, 0.2094F, 0.0F));
		return LayerDefinition.create(modelData, 64, 64);
	}

	@Override
	public void setupAnim(MetalLanceEntity metalLanceEntity, float v, float v1, float v2, float v3, float v4) {
		
	}

	@Override
	public void renderToBuffer(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
		CoreTransition.render(matrices, vertices, light, overlay, color);
		SpearHead.render(matrices, vertices, light, overlay, color);
	}
}