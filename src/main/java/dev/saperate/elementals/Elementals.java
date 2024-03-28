package dev.saperate.elementals;

import dev.saperate.elementals.commands.BendingCommand;
import dev.saperate.elementals.commands.ElementArgumentType;
import dev.saperate.elementals.elements.NoneElement;
import dev.saperate.elementals.elements.fire.FireElement;
import dev.saperate.elementals.elements.water.WaterElement;
import dev.saperate.elementals.entities.fire.FireBlockEntity;
import net.fabricmc.api.ModInitializer;


import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.impl.tag.convention.TagRegistration;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static dev.saperate.elementals.entities.fire.FireBlockEntity.FIREBLOCK;
import static dev.saperate.elementals.network.ModMessages.registerC2SPackets;

public class Elementals implements ModInitializer {
	public static final String MODID = "elementals";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	@Override
	public void onInitialize() {


		registerElements();
		registerCommands();
		registerC2SPackets();
		CommandRegistrationCallback.EVENT.register(BendingCommand::register);

		LOGGER.info("Hello from elementals mod!");
	}


	private void registerElements(){
		new NoneElement();
		new WaterElement();
		new FireElement();
	}

	private void registerCommands(){
		ArgumentTypeRegistry.registerArgumentType(
				new Identifier(MODID,"bending"),
				ElementArgumentType.class,
				ConstantArgumentSerializer.of(ElementArgumentType::element)
		);
	}
}