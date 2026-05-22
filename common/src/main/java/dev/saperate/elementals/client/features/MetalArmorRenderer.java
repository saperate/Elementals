package dev.saperate.elementals.client.features;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.saperate.elementals.Constants;
import dev.saperate.elementals.Elementals;
import dev.saperate.elementals.items.MetalArmorItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.specialty.DyeableGeoArmorRenderer;
import software.bernie.geckolib.util.Color;

public class MetalArmorRenderer extends DyeableGeoArmorRenderer<MetalArmorItem> {
    public MetalArmorRenderer() {
        super(new DefaultedItemGeoModel<>(ResourceLocation.fromNamespaceAndPath(Constants.MODID, "armor/metal_armor")));
    }

    @Override
    public void preRender(PoseStack poseStack, MetalArmorItem animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
        //For some reason the metal armor legs have the bones set to hidden, so we use this to fix it
        setAllVisible(true);
    }

    @Override
    protected boolean isBoneDyeable(GeoBone geoBone) {
        return true;
    }
    
    @Override
    protected @NotNull Color getColorForBone(GeoBone geoBone) {
        return new Color(0xFF000000 | MetalArmorItem.getColor(currentStack));
    }
    
}
