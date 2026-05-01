package dev.saperate.elementals;

import net.fabricmc.api.ModInitializer;

public class ElementalsFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        Constants.LOG.info("Hello Fabric world!");
        Elementals.init();
    }
}
