// Made with Blockbench 4.9.4
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports

package dev.saperate.elementals.entities.models.earth;

import dev.saperate.elementals.entities.earth.EarthBlockEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class ShrapnelModel extends EntityModel<EarthBlockEntity> {
	private final ModelPart bb_main;

	public ShrapnelModel(ModelPart root) {
		this.bb_main = root.getChild("bb_main");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData bb_main = modelPartData.addChild("bb_main", ModelPartBuilder.create().uv(-7, -7).cuboid(-1.0F, -2.0F, -8.0F, 2.0F, 2.0F, 9.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData cube_r1 = bb_main.addChild("cube_r1", ModelPartBuilder.create().uv(-7, -7).cuboid(-1.0F, -2.0F, -9.0F, 2.0F, 2.0F, 9.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2182F, 0.0F, 0.0F));

		ModelPartData cube_r2 = bb_main.addChild("cube_r2", ModelPartBuilder.create().uv(-7, -7).cuboid(-1.0F, -2.0F, -9.0F, 2.0F, 2.0F, 9.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

		ModelPartData cube_r3 = bb_main.addChild("cube_r3", ModelPartBuilder.create().uv(-7, -7).cuboid(-1.0F, -2.0F, -9.0F, 2.0F, 2.0F, 9.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.2618F, 0.0F));

		ModelPartData cube_r4 = bb_main.addChild("cube_r4", ModelPartBuilder.create().uv(-7, -7).cuboid(-1.0F, -2.0F, -9.0F, 2.0F, 2.0F, 9.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.2618F, 0.0F));
		return TexturedModelData.of(modelData, 16, 16);
	}
	@Override
	public void setAngles(EarthBlockEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		bb_main.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}
}