package dev.saperate.elementals.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;

public class ElementalsClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ElementalsClient.init();
    }
}
