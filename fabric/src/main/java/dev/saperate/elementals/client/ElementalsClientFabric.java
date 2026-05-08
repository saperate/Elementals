package dev.saperate.elementals.client;

import net.fabricmc.api.ClientModInitializer;

public class ElementalsClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ElementalsClient.init();
    }
}
