package dev.saperate.elementals.client.entities.models.common;

import dev.saperate.elementals.entities.common.DecoyPlayerEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class DecoyPlayerModel extends PlayerModel<DecoyPlayerEntity> {

    private static boolean slim;
    public DecoyPlayerModel(ModelPart root, boolean thinArms) {
        super(root, thinArms);
        slim = thinArms;
    }

    public static LayerDefinition getTexturedModelData() {
        var dilation = new CubeDeformation(0, 0, 0);
        MeshDefinition modelData = HumanoidModel.createMesh(dilation, 0.0F);
        PartDefinition modelPartData = modelData.getRoot();
        modelPartData.addOrReplaceChild("ear", CubeListBuilder.create().texOffs(24, 0).addBox(-3.0F, -6.0F, -1.0F, 6.0F, 6.0F, 1.0F, dilation), PartPose.ZERO);
        modelPartData.addOrReplaceChild("cloak", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, 0.0F, -1.0F, 10.0F, 16.0F, 1.0F, dilation, 1.0F, 0.5F), PartPose.rotation(0.0F, 0.0F, 0.0F));
        float f = 0.25F;
        if (slim) {
            modelPartData.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, dilation), PartPose.rotation(5.0F, 2.5F, 0.0F));
            modelPartData.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, dilation), PartPose.rotation(-5.0F, 2.5F, 0.0F));
            modelPartData.addOrReplaceChild("left_sleeve", CubeListBuilder.create().texOffs(48, 48).addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, dilation.extend(0.25F)), PartPose.rotation(5.0F, 2.5F, 0.0F));
            modelPartData.addOrReplaceChild("right_sleeve", CubeListBuilder.create().texOffs(40, 32).addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, dilation.extend(0.25F)), PartPose.rotation(-5.0F, 2.5F, 0.0F));
        } else {
            modelPartData.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation), PartPose.rotation(5.0F, 2.0F, 0.0F));
            modelPartData.addOrReplaceChild("left_sleeve", CubeListBuilder.create().texOffs(48, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation.extend(0.25F)), PartPose.rotation(5.0F, 2.0F, 0.0F));
            modelPartData.addOrReplaceChild("right_sleeve", CubeListBuilder.create().texOffs(40, 32).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation.extend(0.25F)), PartPose.rotation(-5.0F, 2.0F, 0.0F));
        }
        modelPartData.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation), PartPose.rotation(1.9F, 12.0F, 0.0F));
        modelPartData.addOrReplaceChild("left_pants", CubeListBuilder.create().texOffs(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation.extend(0.25F)), PartPose.rotation(1.9F, 12.0F, 0.0F));
        modelPartData.addOrReplaceChild("right_pants", CubeListBuilder.create().texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation.extend(0.25F)), PartPose.rotation(-1.9F, 12.0F, 0.0F));
        modelPartData.addOrReplaceChild("jacket", CubeListBuilder.create().texOffs(16, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, dilation.extend(0.25F)), PartPose.ZERO);
        return LayerDefinition.create(modelData, 64, 64);
    }
}
