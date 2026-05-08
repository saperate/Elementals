package dev.saperate.elementals.client;

import dev.saperate.elementals.Constants;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Constants.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ElementalsForgeClient {
    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        ElementalsClient.init();
    }
}
