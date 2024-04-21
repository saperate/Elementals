package dev.saperate.elementals;

import dev.saperate.elementals.entities.*;
import dev.saperate.elementals.keys.abilities.KeyAbility1;
import dev.saperate.elementals.keys.abilities.KeyAbility2;
import dev.saperate.elementals.keys.abilities.KeyAbility3;
import dev.saperate.elementals.packets.SyncBendingElementS2CPacket;
import dev.saperate.elementals.packets.SyncCurrAbilityS2CPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

import static dev.saperate.elementals.entities.fire.FireArcEntity.FIREARC;
import static dev.saperate.elementals.entities.fire.FireBallEntity.FIREBALL;
import static dev.saperate.elementals.entities.fire.FireBlockEntity.FIREBLOCK;
import static dev.saperate.elementals.entities.fire.FireShieldEntity.FIRESHIELD;
import static dev.saperate.elementals.entities.water.WaterCubeEntity.WATERCUBE;
import static dev.saperate.elementals.entities.water.WaterArcEntity.WATERARC;
import static dev.saperate.elementals.entities.water.WaterHelmetEntity.WATERHELMET;
import static dev.saperate.elementals.entities.water.WaterShieldEntity.WATERSHIELD;
import static dev.saperate.elementals.network.ModMessages.SYNC_CURR_ABILITY_PACKET_ID;
import static dev.saperate.elementals.network.ModMessages.SYNC_ELEMENT_PACKET_ID;

public class ElementalsClient implements ClientModInitializer {


	@Override
	public void onInitializeClient() {
		registerS2CPackets();

		EntityRendererRegistry.register(WATERCUBE, WaterCubeEntityRenderer::new);
		EntityRendererRegistry.register(WATERHELMET, WaterHelmetEntityRenderer::new);
		EntityRendererRegistry.register(WATERSHIELD, WaterShieldEntityRenderer::new);
		EntityRendererRegistry.register(WATERARC, WaterArcEntityRenderer::new);

		EntityRendererRegistry.register(FIREBLOCK, FireBlockEntityRenderer::new);
		EntityRendererRegistry.register(FIREARC, FireArcEntityRenderer::new);
		EntityRendererRegistry.register(FIREBALL, FireBallEntityRenderer::new);
		EntityRendererRegistry.register(FIRESHIELD, FireShieldEntityRenderer::new);
		new KeyAbility1();
		new KeyAbility2();
		new KeyAbility3();



	}

	public void registerS2CPackets(){
		ClientPlayNetworking.registerGlobalReceiver(SYNC_CURR_ABILITY_PACKET_ID, SyncCurrAbilityS2CPacket::receive);
		ClientPlayNetworking.registerGlobalReceiver(SYNC_ELEMENT_PACKET_ID, SyncBendingElementS2CPacket::receive);
	}


}