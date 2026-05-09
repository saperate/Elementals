

package dev.saperate.elementals.client.entities.models.earth;

import dev.saperate.elementals.entities.earth.EarthBlockEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.PoseStack;

public class ShrapnelModel extends EntityModel<EarthBlockEntity> {
	private final ModelPart bb_main;

	public ShrapnelModel(ModelPart root) {
		this.bb_main = root.getChild("bb_main");
	}
	public static LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		PartDefinition bb_main = modelPartData.addOrReplaceChild("bb_main", CubeListBuilder.create().uv(-7, -7).addBox(-1.0F, -2.0F, -8.0F, 2.0F, 2.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.rotation(0.0F, 24.0F, 0.0F));

		bb_main.addOrReplaceChild("cube_r1", CubeListBuilder.create().uv(-7, -7).addBox(-1.0F, -2.0F, -9.0F, 2.0F, 2.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.of(0.0F, 0.0F, 0.0F, 0.2182F, 0.0F, 0.0F));

		bb_main.addOrReplaceChild("cube_r2", CubeListBuilder.create().uv(-7, -7).addBox(-1.0F, -2.0F, -9.0F, 2.0F, 2.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.of(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

		bb_main.addOrReplaceChild("cube_r3", CubeListBuilder.create().uv(-7, -7).addBox(-1.0F, -2.0F, -9.0F, 2.0F, 2.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.of(0.0F, 0.0F, 0.0F, 0.0F, -0.2618F, 0.0F));

		bb_main.addOrReplaceChild("cube_r4", CubeListBuilder.create().uv(-7, -7).addBox(-1.0F, -2.0F, -9.0F, 2.0F, 2.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.of(0.0F, 0.0F, 0.0F, 0.0F, 0.2618F, 0.0F));
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