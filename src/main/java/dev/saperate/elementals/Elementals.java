package dev.saperate.elementals;

import dev.saperate.elementals.blocks.SoulFireCore;
import dev.saperate.elementals.blocks.WaterRapid;
import dev.saperate.elementals.commands.BendingCommand;
import dev.saperate.elementals.commands.ElementalsCommand;
import dev.saperate.elementals.commands.ElementArgumentType;
import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.NoneElement;
import dev.saperate.elementals.elements.air.AirElement;
import dev.saperate.elementals.elements.earth.EarthElement;
import dev.saperate.elementals.elements.fire.FireElement;
import dev.saperate.elementals.elements.lightning.LightningElement;
import dev.saperate.elementals.elements.water.WaterElement;
import dev.saperate.elementals.entities.ElementalEntities;
import dev.saperate.elementals.items.ElementalItems;
import dev.saperate.elementals.network.packets.GetModVersionC2SPacket;
import net.fabricmc.api.ModInitializer;


import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static dev.saperate.elementals.effects.DenseStatusEffect.DENSE_EFFECT;
import static dev.saperate.elementals.effects.DrowningStatusEffect.DROWNING_EFFECT;
import static dev.saperate.elementals.effects.SeismicSenseStatusEffect.SEISMIC_SENSE_EFFECT;
import static dev.saperate.elementals.effects.SpiritProjectionStatusEffect.SPIRIT_PROJECTION_EFFECT;
import static dev.saperate.elementals.effects.StationaryStatusEffect.STATIONARY_EFFECT;
import static dev.saperate.elementals.misc.AirBannerPattern.AIR_PATTERN;
import static dev.saperate.elementals.network.ModMessages.registerC2SPackets;

public class Elementals implements ModInitializer {

    //TODO add config
    public static final String MODID = "elementals";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    private static final Identifier WIND_SOUND_ID = new Identifier(MODID, "wind");
    private static final Identifier WIND_BURST_SOUND_ID = new Identifier(MODID, "wind_burst");
    public static SoundEvent WIND_SOUND_EVENT = SoundEvent.of(WIND_SOUND_ID);
    public static SoundEvent WIND_BURST_SOUND_EVENT = SoundEvent.of(WIND_BURST_SOUND_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initialising the cool stuff...");
        ElementalItems.register();
        ElementalEntities.register();

        SoulFireCore.registerBlock();
        WaterRapid.registerBlock();

        Registry.register(Registries.STATUS_EFFECT, new Identifier(MODID, "stationary"), STATIONARY_EFFECT);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(MODID, "dense"), DENSE_EFFECT);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(MODID, "drowning"), DROWNING_EFFECT);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(MODID, "seismic_sense"), SEISMIC_SENSE_EFFECT);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(MODID, "spirit_projection"), SPIRIT_PROJECTION_EFFECT);

        registerElements();
        registerCommands();
        registerC2SPackets();

        ServerPlayConnectionEvents.JOIN.register(Elementals::onPlayReady);

        ServerPlayerEvents.AFTER_RESPAWN.register(Elementals::onPlayerRespawn);

        Registry.register(Registries.BANNER_PATTERN, "air", AIR_PATTERN);

        Registry.register(Registries.SOUND_EVENT, WIND_SOUND_ID, WIND_SOUND_EVENT);
        Registry.register(Registries.SOUND_EVENT, WIND_BURST_SOUND_ID, WIND_BURST_SOUND_EVENT);
    }


    private void registerElements() {
        new NoneElement();
        new WaterElement();
        new FireElement();
        new EarthElement();
        new AirElement();
        new LightningElement();
    }

    private void registerCommands() {

        CommandRegistrationCallback.EVENT.register(BendingCommand::register);
        CommandRegistrationCallback.EVENT.register(ElementalsCommand::register);


        ArgumentTypeRegistry.registerArgumentType(
                new Identifier(MODID, "bending"),
                ElementArgumentType.class,
                ConstantArgumentSerializer.of(ElementArgumentType::element)
        );
    }

    public static void onPlayReady(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        Bender.getBender(handler.player).syncElements();
    }

    private static void onPlayerRespawn(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean b) {
        Bender bender = Bender.getBender(oldPlayer);

        if (bender.currAbility != null) {
            bender.currAbility.onRemove(bender);
            bender.setCurrAbility(null);
            bender.abilityData = null;
        }
        bender.player = newPlayer;

        PlayerData.get(newPlayer).chi = 100;
        bender.syncChi();
    }
}