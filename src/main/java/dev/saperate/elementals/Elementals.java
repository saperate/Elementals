package dev.saperate.elementals;

import dev.saperate.elementals.elements.NoneElement;
import dev.saperate.elementals.elements.water.WaterElement;
import net.fabricmc.api.ModInitializer;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static dev.saperate.elementals.network.ModMessages.registerC2SPackets;

public class Elementals implements ModInitializer {
	public static final String MODID = "elementals";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	@Override
	public void onInitialize() {
		registerElements();
		registerC2SPackets();

		LOGGER.info("Hello from elementals mod!");
	}


	private void registerElements(){
		new NoneElement();
		new WaterElement();
	}
}