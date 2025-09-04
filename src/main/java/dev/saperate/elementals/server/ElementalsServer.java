package dev.saperate.elementals.server;

import dev.saperate.elementals.Elementals;
import net.fabricmc.api.DedicatedServerModInitializer;

public class ElementalsServer implements DedicatedServerModInitializer  {
    @Override
    public void onInitializeServer() {
        Elementals.GLIDER_ITEM_RENDER_PROVIDER = Object::new;
    }
}
