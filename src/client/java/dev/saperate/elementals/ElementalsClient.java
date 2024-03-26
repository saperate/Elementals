package dev.saperate.elementals;

import dev.saperate.elementals.entities.WaterArcEntityRenderer;
import dev.saperate.elementals.entities.WaterCubeEntityRenderer;
import dev.saperate.elementals.keys.MouseInput;
import dev.saperate.elementals.keys.abilities.KeyAbility1;
import dev.saperate.elementals.keys.abilities.KeyAbility2;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

import static dev.saperate.elementals.entities.water.WaterCubeEntity.WATERCUBE;
import static dev.saperate.elementals.entities.water.WaterArcEntity.WATERARC;
import static dev.saperate.elementals.network.ModMessages.registerS2CPackets;

public class ElementalsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {

		registerS2CPackets();
		MouseInput.registerMouseClickEvent();

		EntityRendererRegistry.register(WATERCUBE, WaterCubeEntityRenderer::new);
		EntityRendererRegistry.register(WATERARC, WaterArcEntityRenderer::new);

		new KeyAbility1();
		new KeyAbility2();
	}
}