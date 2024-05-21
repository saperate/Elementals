package dev.saperate.elementals;

import dev.saperate.elementals.blocks.SoulFireCore;
import dev.saperate.elementals.blocks.WaterRapid;
import dev.saperate.elementals.commands.BendingCommand;
import dev.saperate.elementals.commands.ElementArgumentType;
import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.data.StateDataSaverAndLoader;
import dev.saperate.elementals.elements.NoneElement;
import dev.saperate.elementals.elements.air.AirElement;
import dev.saperate.elementals.elements.earth.EarthElement;
import dev.saperate.elementals.elements.fire.FireElement;
import dev.saperate.elementals.elements.water.WaterElement;
import dev.saperate.elementals.entities.ElementalEntities;
import dev.saperate.elementals.items.ElementalItems;
import net.fabricmc.api.ModInitializer;


import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static dev.saperate.elementals.effects.DenseStatusEffect.DENSE_EFFECT;
import static dev.saperate.elementals.effects.DrowningStatusEffect.DROWNING_EFFECT;
import static dev.saperate.elementals.effects.SeismicSenseStatusEffect.SEISMIC_SENSE_EFFECT;
import static dev.saperate.elementals.effects.StationaryStatusEffect.STATIONARY_EFFECT;
import static dev.saperate.elementals.misc.AirBannerPattern.AIR_PATTERN;
import static dev.saperate.elementals.network.ModMessages.registerC2SPackets;

public class Elementals implements ModInitializer {
    public static final String MODID = "elementals";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);


    @Override
    public void onInitialize() {
        LOGGER.info("Starting registration of the cool stuff...");
        ElementalItems.register();
        ElementalEntities.register();

        SoulFireCore.registerBlock();
        WaterRapid.registerBlock();

        Registry.register(Registries.STATUS_EFFECT, new Identifier(MODID, "stationary"), STATIONARY_EFFECT.addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160890", 0, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
        Registry.register(Registries.STATUS_EFFECT, new Identifier(MODID, "dense"), DENSE_EFFECT);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(MODID, "drowning"), DROWNING_EFFECT);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(MODID, "seismic_sense"), SEISMIC_SENSE_EFFECT);

        registerElements();
        registerCommands();
        registerC2SPackets();

        CommandRegistrationCallback.EVENT.register(BendingCommand::register);
        ServerPlayConnectionEvents.JOIN.register(Elementals::onPlayReady);
        ServerPlayerEvents.AFTER_RESPAWN.register(Elementals::onPlayerRespawn);

        Registry.register(Registries.BANNER_PATTERN, "air", AIR_PATTERN);
        LOGGER.info("Everything successfully registered, have fun!");


        //TODO remove before release
        LOGGER.error("/!\\ IMPORTANT /!\\\n" +
                "\tDO NOT RELEASE, USE AT YOUR OWN RISK AS IT CAN CONTAIN BUGS AND CAN DESTROY YOUR WORLD \n" +
                "\tIf this message is in a public release, contact saperate as it shouldn't be there.\n" +
                "\t05/11/2024\n" +
                "\t-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
    }


    private void registerElements() {
        new NoneElement();
        new WaterElement();
        new FireElement();
        new EarthElement();
        new AirElement();
    }

    private void registerCommands() {
        ArgumentTypeRegistry.registerArgumentType(
                new Identifier(MODID, "bending"),
                ElementArgumentType.class,
                ConstantArgumentSerializer.of(ElementArgumentType::element)
        );
    }

    public static void onPlayReady(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        ServerPlayerEntity player = handler.player;
        Bender bender = Bender.getBender(player);

        PlayerData playerState = StateDataSaverAndLoader.getPlayerState(player);
        bender.setElement(playerState.element, true);
        bender.boundAbilities = playerState.boundAbilities;
    }

    private static void onPlayerRespawn(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean b) {
        Bender bender = Bender.getBender(oldPlayer);

        if (bender.currAbility != null) {
            bender.currAbility.onRemove(bender);
            bender.setCurrAbility(null);
            bender.abilityData = null;
        }
        bender.player = newPlayer;
    }
}