package dev.saperate.elementals;

import dev.saperate.elementals.entities.WaterCubeEntityRenderer;
import dev.saperate.elementals.keys.abilities.KeyAbility1;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

import static dev.saperate.elementals.entities.water.WaterCubeEntity.WATERCUBE;
import static dev.saperate.elementals.network.ModMessages.registerS2CPackets;

public class ElementalsClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {

		registerS2CPackets();

		EntityRendererRegistry.register(WATERCUBE, WaterCubeEntityRenderer::new);

		new KeyAbility1();

	}
}