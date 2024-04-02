package dev.saperate.elementals;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import dev.saperate.elementals.entities.*;
import dev.saperate.elementals.keys.MouseInput;
import dev.saperate.elementals.keys.abilities.KeyAbility1;
import dev.saperate.elementals.keys.abilities.KeyAbility2;
import dev.saperate.elementals.packets.SyncBendingElementS2CPacket;
import dev.saperate.elementals.packets.SyncCurrAbilityS2CPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.entity.EntityType;

import static dev.saperate.elementals.entities.fire.FireArcEntity.FIREARC;
import static dev.saperate.elementals.entities.fire.FireBallEntity.FIREBALL;
import static dev.saperate.elementals.entities.fire.FireBlockEntity.FIREBLOCK;
import static dev.saperate.elementals.entities.water.WaterCubeEntity.WATERCUBE;
import static dev.saperate.elementals.entities.water.WaterArcEntity.WATERARC;
import static dev.saperate.elementals.network.ModMessages.SYNC_CURR_ABILITY_PACKET_ID;
import static dev.saperate.elementals.network.ModMessages.SYNC_ELEMENT_PACKET_ID;

public class ElementalsClient implements ClientModInitializer {


	@Override
	public void onInitializeClient() {


		registerS2CPackets();
		MouseInput.registerMouseClickEvent();

		EntityRendererRegistry.register(WATERCUBE, WaterCubeEntityRenderer::new);
		EntityRendererRegistry.register(WATERARC, WaterArcEntityRenderer::new);
		EntityRendererRegistry.register(FIREBLOCK, FireBlockEntityRenderer::new);
		EntityRendererRegistry.register(FIREARC, FireArcEntityRenderer::new);
		EntityRendererRegistry.register(FIREBALL, FireBallEntityRenderer::new);
		new KeyAbility1();
		new KeyAbility2();
	}

	public void registerS2CPackets(){
		ClientPlayNetworking.registerGlobalReceiver(SYNC_CURR_ABILITY_PACKET_ID, SyncCurrAbilityS2CPacket::receive);
		ClientPlayNetworking.registerGlobalReceiver(SYNC_ELEMENT_PACKET_ID, SyncBendingElementS2CPacket::receive);
	}
}