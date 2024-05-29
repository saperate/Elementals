package dev.saperate.elementals;

import com.google.common.collect.ImmutableMap;
import dev.saperate.elementals.entities.air.*;
import dev.saperate.elementals.entities.common.DecoyPlayerEntity;
import dev.saperate.elementals.entities.water.*;
import dev.saperate.elementals.entities.common.DecoyPlayerEntityRenderer;
import dev.saperate.elementals.entities.earth.EarthBlockEntityRenderer;
import dev.saperate.elementals.entities.fire.FireArcEntityRenderer;
import dev.saperate.elementals.entities.fire.FireBallEntityRenderer;
import dev.saperate.elementals.entities.fire.FireBlockEntityRenderer;
import dev.saperate.elementals.entities.fire.FireShieldEntityRenderer;
import dev.saperate.elementals.entities.models.common.DecoyPlayerModel;
import dev.saperate.elementals.entities.models.water.WaterBladeModel;
import dev.saperate.elementals.keys.abilities.KeyAbility1;
import dev.saperate.elementals.keys.abilities.KeyAbility2;
import dev.saperate.elementals.keys.abilities.KeyAbility3;
import dev.saperate.elementals.keys.abilities.KeyAbility4;
import dev.saperate.elementals.packets.SyncBendingElementS2CPacket;
import dev.saperate.elementals.packets.SyncCurrAbilityS2CPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Map;

import static dev.saperate.elementals.Elementals.MODID;
import static dev.saperate.elementals.entities.ElementalEntities.*;
import static dev.saperate.elementals.network.ModMessages.*;

public class ElementalsClient implements ClientModInitializer {
	public static final EntityModelLayer MODEL_DECOY_PLAYER = new EntityModelLayer(new Identifier(MODID, "decoy_player"),"main");
	public static final EntityModelLayer MODEL_WATER_BLADE_LAYER = (new EntityModelLayer(new Identifier(MODID, "water_blade"),"bb_main"));

	@Override
	public void onInitializeClient() {
		registerS2CPackets();
		registerEntityRenderers();

		new KeyAbility1();
		new KeyAbility2();
		new KeyAbility3();
		new KeyAbility4();


		EntityModelLayerRegistry.registerModelLayer(MODEL_WATER_BLADE_LAYER, WaterBladeModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(MODEL_DECOY_PLAYER, DecoyPlayerModel::getTexturedModelData);


	}

	public void registerS2CPackets(){
		ClientPlayNetworking.registerGlobalReceiver(SYNC_CURR_ABILITY_PACKET_ID, SyncCurrAbilityS2CPacket::receive);
		ClientPlayNetworking.registerGlobalReceiver(SYNC_ELEMENT_PACKET_ID, SyncBendingElementS2CPacket::receive);
	}

	public void registerEntityRenderers(){
		//WATER
		EntityRendererRegistry.register(WATERCUBE, WaterCubeEntityRenderer::new);
		EntityRendererRegistry.register(WATERHELMET, WaterHelmetEntityRenderer::new);
		EntityRendererRegistry.register(WATERSHIELD, WaterShieldEntityRenderer::new);
		EntityRendererRegistry.register(WATERARC, WaterArcEntityRenderer::new);
		EntityRendererRegistry.register(WATERJET, WaterJetEntityRenderer::new);
		EntityRendererRegistry.register(WATERARM, WaterArmEntityRenderer::new);
		EntityRendererRegistry.register(WATERBLADE, WaterBladeEntityRenderer::new);
		EntityRendererRegistry.register(WATERBULLET, WaterBulletEntityRenderer::new);
		EntityRendererRegistry.register(WATERHEALING, WaterHealingEntityRenderer::new);
		EntityRendererRegistry.register(WATERTOWER, WaterTowerEntityRenderer::new);

		//FIRE
		EntityRendererRegistry.register(FIREBLOCK, FireBlockEntityRenderer::new);
		EntityRendererRegistry.register(FIREARC, FireArcEntityRenderer::new);
		EntityRendererRegistry.register(FIREBALL, FireBallEntityRenderer::new);
		EntityRendererRegistry.register(FIRESHIELD, FireShieldEntityRenderer::new);

		//EARTH
		EntityRendererRegistry.register(EARTHBLOCK, EarthBlockEntityRenderer::new);

		//AIR
		EntityRendererRegistry.register(AIRSHIELD, AirShieldEntityRenderer::new);
		EntityRendererRegistry.register(AIRTORNADO, AirTornadoEntityRenderer::new);
		EntityRendererRegistry.register(AIRSTREAM, AirStreamEntityRenderer::new);
		EntityRendererRegistry.register(AIRBALL, AirBallEntityRenderer::new);
		EntityRendererRegistry.register(AIRBULLET, AirBulletEntityRenderer::new);
		EntityRendererRegistry.register(AIRSCOOTER, AirScooterEntityRenderer::new);

		//COMMON
		EntityRendererRegistry.register(DECOYPLAYER, (context) -> new DecoyPlayerEntityRenderer(context, false));
	}





}