package dev.saperate.elementals.entities.common;

import dev.saperate.elementals.data.ClientBender;
import dev.saperate.elementals.entities.models.common.DecoyPlayerModel;
import dev.saperate.elementals.entities.features.ElementalsCapeFeatureRenderer;
import dev.saperate.elementals.network.ModMessages;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.*;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;


public class DecoyPlayerEntityRenderer extends LivingEntityRenderer<DecoyPlayerEntity, PlayerEntityModel<DecoyPlayerEntity>> {
    public DecoyPlayerEntityRenderer(EntityRendererFactory.Context ctx, boolean slimArms) {
        super(ctx, new DecoyPlayerModel(ctx.getPart( EntityModelLayers.PLAYER),slimArms), 0.5f);
        this.addFeature(new ArmorFeatureRenderer<>(this,
                new ArmorEntityModel<>(ctx.getPart(EntityModelLayers.PLAYER_INNER_ARMOR)),
                new ArmorEntityModel<>(ctx.getPart(EntityModelLayers.PLAYER_OUTER_ARMOR)),
                ctx.getModelManager()));
        this.addFeature(new HeldItemFeatureRenderer<>(this, ctx.getHeldItemRenderer()));
        this.addFeature(new ElytraFeatureRenderer<>(this,ctx.getModelLoader()));
        this.addFeature(new HeadFeatureRenderer<>(this, ctx.getModelLoader(), ctx.getHeldItemRenderer()));
        this.addFeature(new ElementalsCapeFeatureRenderer(this));
        this.addFeature(new StuckArrowsFeatureRenderer<>(ctx, this));
    }

    @Override
    public void render(DecoyPlayerEntity decoy, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(decoy, f, g, matrixStack, vertexConsumerProvider, i);
        if(decoy.getFocusCamera()){
            ClientBender.get().ClientAbilityData = decoy;
        }
    }

    @Override
    public Identifier getTexture(DecoyPlayerEntity decoy) {
       ClientPlayNetworkHandler handler = MinecraftClient.getInstance().getNetworkHandler();
       if(handler == null){
           return DefaultSkinHelper.getTexture();
       }
        PlayerListEntry entry = handler.getPlayerListEntry(decoy.getOwnerUUID());
        if(entry == null){
            return DefaultSkinHelper.getTexture();
        }
        return entry.getSkinTextures().texture();
    }
}
