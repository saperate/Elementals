// Modeled after WaterBladeModel: a cross of thin flat planes so the blade
// reads as a solid 3D sliver of compressed air from any viewing angle.
package dev.saperate.elementals.client.entities.models.air;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.saperate.elementals.entities.air.AirBladeEntity;
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

        //horizontal cut: a long, thin flat plane pointing forward (+Z), like a flat sliver of cut air
        PartDefinition bb_main = modelPartData.addOrReplaceChild("bb_main",
                CubeListBuilder.create().texOffs(0, 10).addBox(-1.0F, -0.5F, -7.0F, 2.0F, 0.5F, 14.0F, new CubeDeformation(0.0F)),
                PartPose.rotation(0.0F, 0.0F, 0.0F));

        //vertical cut: same plane rotated 90 degrees, so the blade reads as a "+" cross
        //when seen edge-on, instead of relying on diagonal fillers
        bb_main.addOrReplaceChild("cube_r1",
                CubeListBuilder.create().texOffs(0, 5).addBox(-1.0F, -0.5F, -7.0F, 2.0F, 0.5F, 14.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

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