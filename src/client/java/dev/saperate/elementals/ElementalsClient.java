package dev.saperate.elementals;

import dev.saperate.elementals.entities.earth.EarthBlockEntityRenderer;
import dev.saperate.elementals.entities.fire.FireArcEntityRenderer;
import dev.saperate.elementals.entities.fire.FireBallEntityRenderer;
import dev.saperate.elementals.entities.fire.FireBlockEntityRenderer;
import dev.saperate.elementals.entities.fire.FireShieldEntityRenderer;
import dev.saperate.elementals.entities.models.water.WaterBladeModel;
import dev.saperate.elementals.entities.water.*;
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
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

import static dev.saperate.elementals.Elementals.MODID;
import static dev.saperate.elementals.entities.earth.EarthBlockEntity.EARTHBLOCK;
import static dev.saperate.elementals.entities.fire.FireArcEntity.FIREARC;
import static dev.saperate.elementals.entities.fire.FireBallEntity.FIREBALL;
import static dev.saperate.elementals.entities.fire.FireBlockEntity.FIREBLOCK;
import static dev.saperate.elementals.entities.fire.FireShieldEntity.FIRESHIELD;
import static dev.saperate.elementals.entities.water.WaterArmEntity.WATERARM;
import static dev.saperate.elementals.entities.water.WaterBladeEntity.WATERBLADE;
import static dev.saperate.elementals.entities.water.WaterBulletEntity.WATERBULLET;
import static dev.saperate.elementals.entities.water.WaterCubeEntity.WATERCUBE;
import static dev.saperate.elementals.entities.water.WaterArcEntity.WATERARC;
import static dev.saperate.elementals.entities.water.WaterHealingEntity.WATERHEALING;
import static dev.saperate.elementals.entities.water.WaterHelmetEntity.WATERHELMET;
import static dev.saperate.elementals.entities.water.WaterJetEntity.WATERJET;
import static dev.saperate.elementals.entities.water.WaterShieldEntity.WATERSHIELD;
import static dev.saperate.elementals.entities.water.WaterTowerEntity.WATERTOWER;
import static dev.saperate.elementals.network.ModMessages.SYNC_CURR_ABILITY_PACKET_ID;
import static dev.saperate.elementals.network.ModMessages.SYNC_ELEMENT_PACKET_ID;

public class ElementalsClient implements ClientModInitializer {

	public static final EntityModelLayer MODEL_WATER_BLADE_LAYER = (new EntityModelLayer(new Identifier(MODID, "water_blade"),"bb_main"));

	@Override
	public void onInitializeClient() {
		registerS2CPackets();

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

		EntityRendererRegistry.register(FIREBLOCK, FireBlockEntityRenderer::new);
		EntityRendererRegistry.register(FIREARC, FireArcEntityRenderer::new);
		EntityRendererRegistry.register(FIREBALL, FireBallEntityRenderer::new);
		EntityRendererRegistry.register(FIRESHIELD, FireShieldEntityRenderer::new);

		EntityRendererRegistry.register(EARTHBLOCK, EarthBlockEntityRenderer::new);

		new KeyAbility1();
		new KeyAbility2();
		new KeyAbility3();
		new KeyAbility4();


		EntityModelLayerRegistry.registerModelLayer(MODEL_WATER_BLADE_LAYER, WaterBladeModel::getTexturedModelData);

	}

	public void registerS2CPackets(){
		ClientPlayNetworking.registerGlobalReceiver(SYNC_CURR_ABILITY_PACKET_ID, SyncCurrAbilityS2CPacket::receive);
		ClientPlayNetworking.registerGlobalReceiver(SYNC_ELEMENT_PACKET_ID, SyncBendingElementS2CPacket::receive);
	}


}