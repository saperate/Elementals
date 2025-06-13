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
	private final ModelPart bb_main;
	public MetalLanceModel(ModelPart root) {
		this.CoreTransition = root.getChild("CoreTransition");
		this.SpearHead = root.getChild("SpearHead");
		this.bb_main = root.getChild("bb_main");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData CoreTransition = modelPartData.addChild("CoreTransition", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 1.0F));

		ModelPartData cube_r1 = CoreTransition.addChild("cube_r1", ModelPartBuilder.create().uv(0, 31).cuboid(-1.0F, -2.0F, 2.0F, 1.0F, 2.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(1.0F, 0.0F, -1.0F, 0.0F, -0.0436F, 0.0F));

		ModelPartData cube_r2 = CoreTransition.addChild("cube_r2", ModelPartBuilder.create().uv(25, 27).cuboid(-1.0F, -2.0F, 2.0F, 1.0F, 2.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -1.0F, 0.0F, 0.0436F, 0.0F));

		ModelPartData SpearHead = modelPartData.addChild("SpearHead", ModelPartBuilder.create().uv(0, 19).cuboid(-1.0F, -0.5F, -5.5F, 2.0F, 1.0F, 10.0F, new Dilation(0.0F))
				.uv(25, 19).cuboid(-2.0F, -0.499F, -4.5F, 4.0F, 1.001F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 23.0F, 12.5F, 0.0F, 0.0F, 1.5708F));

		ModelPartData bb_main = modelPartData.addChild("bb_main", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -3.0F, -9.75F, 2.0F, 4.0F, 14.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public void setAngles(MetalLanceEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		CoreTransition.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		SpearHead.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		bb_main.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}
}