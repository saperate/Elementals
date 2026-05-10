package dev.saperate.elementals;

import net.fabricmc.api.ModInitializer;

public class ElementalsFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        Elementals.init();
    }
}
