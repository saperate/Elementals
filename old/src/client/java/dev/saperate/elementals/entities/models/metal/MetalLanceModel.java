// Made with Blockbench 4.11.2
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports

package dev.saperate.elementals.entities.models.metal;

import dev.saperate.elementals.entities.metal.MetalLanceEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class MetalLanceModel extends EntityModel<MetalLanceEntity> {
	private final ModelPart CoreTransition;
	private final ModelPart SpearHead;
	public MetalLanceModel(ModelPart root) {
		this.CoreTransition = root.getChild("CoreTransition");
		this.SpearHead = root.getChild("SpearHead");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData CoreTransition = modelPartData.addChild("CoreTransition", ModelPartBuilder.create().uv(0, 0).cuboid(-0.5F, -2.0F, -15.0F, 1.0F, 2.0F, 29.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 1.0F));

		ModelPartData SpearHead = modelPartData.addChild("SpearHead", ModelPartBuilder.create(), ModelTransform.of(0.0F, 23.0F, 12.5F, 0.0F, 0.0F, 1.5708F));

		ModelPartData cube_r1 = SpearHead.addChild("cube_r1", ModelPartBuilder.create().uv(14, 31).cuboid(1.0F, -1.01F, -1.0F, 1.0F, 1.0F, 6.0F, new Dilation(0.01F)), ModelTransform.of(0.1F, 0.51F, -2.5F, 0.0F, -0.2094F, 0.0F));

		ModelPartData cube_r2 = SpearHead.addChild("cube_r2", ModelPartBuilder.create().uv(0, 31).cuboid(-2.0F, -1.01F, -1.0F, 1.0F, 1.0F, 6.0F, new Dilation(0.01F)), ModelTransform.of(-0.1F, 0.51F, -2.5F, 0.0F, 0.2094F, 0.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}
	@Override
	public void setAngles(MetalLanceEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
		CoreTransition.render(matrices, vertices, light, overlay, color);
		SpearHead.render(matrices, vertices, light, overlay, color);
	}
}