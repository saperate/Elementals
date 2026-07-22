// Crescent-shaped wind slash: instead of flat crossed planes, the blade is built from
// several thin segments arranged along a curved arc - fat in the middle, tapering to
// sharp points at both tips, like a scythe-shaped cut of compressed air.
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

        //empty root part - the actual crescent shape is built entirely from the
        //procedural arc segments attached below, so nothing is added here directly
        PartDefinition bb_main = modelPartData.addOrReplaceChild("bb_main",
                CubeListBuilder.create(), PartPose.ZERO);

        int segments = 9;              // more segments = smoother curve
        float radius = 11.0F;          // how wide/curved the crescent is
        float halfAngle = (float) Math.toRadians(55.0); // total sweep of the arc (~110 degrees)
        float maxWidth = 3.2F;         // thickness of the blade at its widest (center) point
        float planeThickness = 0.5F;   // flat plane depth, just enough to catch light from an angle

        for (int i = 0; i < segments; i++) {
            //t goes from -1 (one tip) to 0 (center) to 1 (other tip)
            float t = (i / (float) (segments - 1)) * 2f - 1f;
            float angle = t * halfAngle;

            //taper the width so the crescent bulges in the middle and comes to a sharp
            //point at both horns, instead of being a uniform-width strip
            float widthFactor = (float) Math.pow(1.0 - Math.abs(t), 0.6);
            float width = Math.max(0.35F, maxWidth * widthFactor);

            //walk along a circular arc in the X-Z plane, so the whole shape bows forward
            //like a scythe blade, with the tips trailing behind the bulging center
            float angleStep = (2 * halfAngle) / (segments - 1);
            float segLength = (2 * radius * (float) Math.sin(angleStep / 2)) * 1.3F;
            float x = radius * (float) Math.sin(angle);
            float z = radius * (float) Math.cos(angle) - radius;

            bb_main.addOrReplaceChild("crescent_" + i,
                    CubeListBuilder.create().texOffs(0, 0)
                            .addBox(-width / 2, -planeThickness / 2, -segLength / 2, width, planeThickness, segLength, new CubeDeformation(0.0F)),
                    PartPose.offsetAndRotation(x, 0, z, 0, angle, 0));
        }

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