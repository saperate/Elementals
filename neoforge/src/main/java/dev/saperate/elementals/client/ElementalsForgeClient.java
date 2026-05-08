package dev.saperate.elementals.client;

import dev.saperate.elementals.Constants;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(value = Constants.MODID, dist = Dist.CLIENT)
public class ElementalsForgeClient {
    public ElementalsForgeClient(IEventBus modBus) {
        ElementalsClient.init();
    }
}
