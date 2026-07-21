// Modeled after WaterBladeModel: a cross of thin flat planes so the blade
// reads as a solid 3D sliver of compressed air from any viewing angle.
package dev.saperate.elementals.client.entities.models.air;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class AirBladeModel extends EntityModel<AirBladeEntity> {
    private final ModelPart bb_main;

    public AirBladeModel(ModelPart root) {
        this.bb_main = root.getChild("bb_main");
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();

        //long, thin horizontal blade pointing forward (+Z), like a flat sliver of cut air
        PartDefinition bb_main = modelPartData.addOrReplaceChild("bb_main",
                CubeListBuilder.create().texOffs(0, 10).addBox(-1.0F, -0.5F, -7.0F, 2.0F, 0.5F, 14.0F, new CubeDeformation(0.0F)),
                PartPose.rotation(0.0F, 0.0F, 0.0F));

        //cross the plane on the vertical axis so the blade still reads as a shape when seen edge-on
        bb_main.addOrReplaceChild("cube_r1",
                CubeListBuilder.create().texOffs(0, 5).addBox(-1.0F, -0.5F, -7.0F, 2.0F, 0.5F, 14.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

        //a slimmer diagonal cross to fill in the silhouette further
        bb_main.addOrReplaceChild("cube_r2",
                CubeListBuilder.create().texOffs(0, 0).addBox(-0.75F, -0.35F, -7.0F, 1.5F, 0.35F, 14.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

        bb_main.addOrReplaceChild("cube_r3",
                CubeListBuilder.create().texOffs(0, 15).addBox(-0.75F, -0.35F, -7.0F, 1.5F, 0.35F, 14.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

        return LayerDefinition.create(modelData, 32, 32);
    }

    @Override
    public void setupAnim(AirBladeEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void renderToBuffer(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        bb_main.render(matrices, vertices, light, overlay, color);
    }
}