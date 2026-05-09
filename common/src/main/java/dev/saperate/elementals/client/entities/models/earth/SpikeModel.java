

package dev.saperate.elementals.client.entities.models.earth;

import dev.saperate.elementals.entities.earth.EarthBlockEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.PoseStack;

public class SpikeModel extends EntityModel<EarthBlockEntity> {
	private final ModelPart bb_main;
	public SpikeModel(ModelPart root) {
		this.bb_main = root.getChild("bb_main");
	}
	public static LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		PartDefinition bb_main = modelPartData.addOrReplaceChild("bb_main", CubeListBuilder.create(), PartPose.rotation(0.0F, 24.0F, 0.0F));

		PartDefinition cube_r1 = bb_main.addOrReplaceChild("cube_r1", CubeListBuilder.create().uv(-7, -7).addBox(-2.0F, -3.0F, -9.0F, 4.0F, 4.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.of(0.0F, -8.0F, 1.0F, 1.8326F, 0.0F, 0.0F));

		PartDefinition cube_r2 = bb_main.addOrReplaceChild("cube_r2", CubeListBuilder.create().uv(-7, -7).addBox(-2.0F, -3.0F, -9.0F, 4.0F, 4.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.of(0.0F, -8.0F, 1.0F, 1.309F, 0.0F, 0.0F));

		PartDefinition cube_r3 = bb_main.addOrReplaceChild("cube_r3", CubeListBuilder.create().uv(-7, -7).addBox(-2.0F, -3.0F, -9.0F, 4.0F, 4.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.of(0.0F, -8.0F, 1.0F, 1.5708F, 0.0F, -0.2618F));

		PartDefinition cube_r4 = bb_main.addOrReplaceChild("cube_r4", CubeListBuilder.create().uv(-7, -7).addBox(-2.0F, -3.0F, -9.0F, 4.0F, 4.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.of(0.0F, -8.0F, 1.0F, 1.5708F, 0.0F, 0.2618F));

		PartDefinition cube_r5 = bb_main.addOrReplaceChild("cube_r5", CubeListBuilder.create().uv(-7, -7).addBox(-2.0F, -3.0F, -8.0F, 4.0F, 4.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.of(0.0F, -8.0F, 1.0F, 1.5708F, 0.0F, 0.0F));
		return LayerDefinition.of(modelData, 16, 16);
	}
	@Override
	public void setAngles(EarthBlockEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}

	@Override
	public void render(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
		bb_main.render(matrices, vertices, light, overlay);
	}
}