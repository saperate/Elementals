package dev.saperate.elementals;

import dev.saperate.elementals.blocks.SoulFireCore;
import dev.saperate.elementals.commands.BendingCommand;
import dev.saperate.elementals.commands.ElementArgumentType;
import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.data.StateDataSaverAndLoader;
import dev.saperate.elementals.elements.NoneElement;
import dev.saperate.elementals.elements.fire.FireElement;
import dev.saperate.elementals.elements.water.WaterElement;
import net.fabricmc.api.ModInitializer;


import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static dev.saperate.elementals.network.ModMessages.registerC2SPackets;

public class Elementals implements ModInitializer {
	public static final String MODID = "elementals";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	@Override
	public void onInitialize() {

		SoulFireCore.registerBlock();

		registerElements();
		registerCommands();
		registerC2SPackets();
		CommandRegistrationCallback.EVENT.register(BendingCommand::register);

		ServerPlayConnectionEvents.JOIN.register(Elementals::onPlayReady);

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

	public static void onPlayReady(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server){
		ServerPlayerEntity player = handler.player;
		Bender bender = Bender.getBender(player);

		PlayerData playerState = StateDataSaverAndLoader.getPlayerState(player);
		bender.setElement(playerState.element,true);
		bender.boundAbilities = playerState.boundAbilities;
	}

}