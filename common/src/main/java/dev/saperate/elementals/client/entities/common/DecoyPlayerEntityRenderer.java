package dev.saperate.elementals.client.entities.common;

import dev.saperate.elementals.client.data.ClientBender;
import dev.saperate.elementals.client.entities.features.ElementalsCapeFeatureRenderer;
import dev.saperate.elementals.client.entities.models.common.DecoyPlayerModel;
import dev.saperate.elementals.entities.common.DecoyPlayerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;


public class DecoyPlayerEntityRenderer extends LivingEntityRenderer<DecoyPlayerEntity, PlayerModel<DecoyPlayerEntity>> {
    public DecoyPlayerEntityRenderer(EntityRendererProvider.Context ctx, boolean slimArms) {
        super(ctx, new DecoyPlayerModel(ctx.bakeLayer(ModelLayers.PLAYER),slimArms), 0.5f);
        this.addLayer(new HumanoidArmorLayer<>(this,
                new HumanoidArmorModel<>(ctx.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
                new HumanoidArmorModel<>(ctx.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
                ctx.getModelManager()));
        this.addLayer(new ItemInHandLayer<>(this, ctx.getItemInHandRenderer()));
        this.addLayer(new ElytraLayer<>(this,ctx.getModelSet()));
        this.addLayer(new CustomHeadLayer<>(this, ctx.getModelSet(), ctx.getItemInHandRenderer()));
        this.addLayer(new ElementalsCapeFeatureRenderer(this));
        this.addLayer(new ArrowLayer<>(ctx, this));
    }

    @Override
    public void render(DecoyPlayerEntity decoy, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i) {
        super.render(decoy, f, g, matrixStack, vertexConsumerProvider, i);
        if(decoy.getOwner().equals(ClientBender.get().player) && decoy.getFocusCamera()){
            ClientBender.get().ClientAbilityData = decoy;
        }
    }

    @Override
    public ResourceLocation getTextureLocation(DecoyPlayerEntity decoy) {
       ClientPacketListener handler = Minecraft.getInstance().getConnection();
       if(handler == null){
           return DefaultPlayerSkin.getDefaultTexture();
       }
        PlayerInfo entry = handler.getPlayerInfo(decoy.getOwnerUUID());
        if(entry == null){
            return DefaultPlayerSkin.getDefaultTexture();
        }
        return entry.getSkin().texture();
    }
}
