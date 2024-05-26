package dev.saperate.elementals.entities.common;

import dev.saperate.elementals.entities.models.common.DecoyPlayerModel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.*;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import static dev.saperate.elementals.ElementalsClient.MODEL_DECOY_PLAYER;

public class DecoyPlayerEntityRenderer extends LivingEntityRenderer<DecoyPlayerEntity, PlayerEntityModel<DecoyPlayerEntity>> {
    public DecoyPlayerEntityRenderer(EntityRendererFactory.Context ctx, float shadowRadius) {
        super(ctx, new DecoyPlayerModel(ctx.getPart(MODEL_DECOY_PLAYER),false), shadowRadius);
    }

    @Override
    public Identifier getTexture(DecoyPlayerEntity decoy) {
        return ((AbstractClientPlayerEntity)decoy.getOwner()).getSkinTextures().texture();
    }
}
