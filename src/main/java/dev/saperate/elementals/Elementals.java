package dev.saperate.elementals;

import com.mojang.brigadier.CommandDispatcher;
import dev.saperate.elementals.advancements.HasElementCriterion;
import dev.saperate.elementals.advancements.UsedAbilityCriterion;
import dev.saperate.elementals.blocks.LitAir;
import dev.saperate.elementals.blocks.SoulFireCore;
import dev.saperate.elementals.blocks.WaterRapid;
import dev.saperate.elementals.blocks.blockEntities.LitAirBlockEntity;
import dev.saperate.elementals.commands.BendingCommand;
import dev.saperate.elementals.commands.ElementalsCommand;
import dev.saperate.elementals.commands.ElementArgumentType;
import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.data.StateDataSaverAndLoader;
import dev.saperate.elementals.elements.NoneElement;
import dev.saperate.elementals.elements.air.AirElement;
import dev.saperate.elementals.elements.blood.BloodElement;
import dev.saperate.elementals.elements.earth.EarthElement;
import dev.saperate.elementals.elements.fire.FireElement;
import dev.saperate.elementals.elements.lightning.LightningElement;
import dev.saperate.elementals.elements.water.WaterElement;
import dev.saperate.elementals.enchantments.VolumeEnchantment;
import dev.saperate.elementals.entities.ElementalEntities;
import dev.saperate.elementals.items.ElementalItems;
import net.fabricmc.api.ModInitializer;


import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.SetContentsLootFunction;
import net.minecraft.loot.function.SetLootTableLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.SummonCommand;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static dev.saperate.elementals.blocks.LitAir.LIT_AIR;
import static dev.saperate.elementals.effects.BurnoutStatusEffect.BURNOUT_EFFECT;
import static dev.saperate.elementals.effects.DenseStatusEffect.DENSE_EFFECT;
import static dev.saperate.elementals.effects.DrowningStatusEffect.DROWNING_EFFECT;
import static dev.saperate.elementals.effects.OverchargedStatusEffect.OVERCHARGED_EFFECT;
import static dev.saperate.elementals.effects.SeismicSenseStatusEffect.SEISMIC_SENSE_EFFECT;
import static dev.saperate.elementals.effects.ShockedStatusEffect.SHOCKED_EFFECT;
import static dev.saperate.elementals.effects.SpiritProjectionStatusEffect.SPIRIT_PROJECTION_EFFECT;
import static dev.saperate.elementals.effects.StaticAuraStatusEffect.STATIC_AURA_EFFECT;
import static dev.saperate.elementals.effects.StationaryStatusEffect.STATIONARY_EFFECT;
import static dev.saperate.elementals.effects.StunnedStatusEffect.STUNNED_EFFECT;
import static dev.saperate.elementals.items.ElementalItems.*;
import static dev.saperate.elementals.misc.AirBannerPattern.AIR_PATTERN;
import static dev.saperate.elementals.network.ModMessages.registerC2SPackets;

public class Elementals implements ModInitializer {
    public static Enchantment VOLUME_ENCHANTMENT = new VolumeEnchantment();

    //TODO add config
    public static final String MODID = "elementals";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static HasElementCriterion HAS_ELEMENT = Criteria.register(new HasElementCriterion());
    public static UsedAbilityCriterion USED_ABILITY = Criteria.register(new UsedAbilityCriterion());
    private static final Identifier WIND_SOUND_ID = new Identifier(MODID, "wind");
    private static final Identifier WIND_BURST_SOUND_ID = new Identifier(MODID, "wind_burst");
    public static SoundEvent WIND_SOUND_EVENT = SoundEvent.of(WIND_SOUND_ID);
    public static SoundEvent WIND_BURST_SOUND_EVENT = SoundEvent.of(WIND_BURST_SOUND_ID);
    public static final DefaultParticleType LIGHTNING_PARTICLE_TYPE = FabricParticleTypes.simple();

    public static final GameRules.Key<GameRules.BooleanRule> BENDING_GRIEFING =
            GameRuleRegistry.register("bendingGriefing", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));

    public static final BlockEntityType<LitAirBlockEntity> LIT_AIR_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier(MODID, "lit_air_block_entity"),
            BlockEntityType.Builder.create(LitAirBlockEntity::new, LIT_AIR).build(null)
    );

    @Override
    public void onInitialize() {
        LOGGER.info("Initialising the cool stuff...");
        ElementalItems.register();
        DispenserBlock.registerBehavior(BOOMERANG_ITEM,BOOMERANG_ITEM);
        DispenserBlock.registerBehavior(DIRT_BOTTLE_ITEM,DIRT_BOTTLE_ITEM);

        Registry.register(Registries.ENCHANTMENT, new Identifier(MODID, "volume"), VOLUME_ENCHANTMENT);

        ElementalEntities.register();

        SoulFireCore.registerBlock();
        WaterRapid.registerBlock();
        LitAir.registerBlock();

        Registry.register(Registries.STATUS_EFFECT, new Identifier(MODID, "stationary"), STATIONARY_EFFECT);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(MODID, "dense"), DENSE_EFFECT);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(MODID, "drowning"), DROWNING_EFFECT);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(MODID, "seismic_sense"), SEISMIC_SENSE_EFFECT);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(MODID, "spirit_projection"), SPIRIT_PROJECTION_EFFECT);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(MODID, "shocked"), SHOCKED_EFFECT);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(MODID, "stunned"), STUNNED_EFFECT);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(MODID, "static_aura"), STATIC_AURA_EFFECT);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(MODID, "overcharged"), OVERCHARGED_EFFECT);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(MODID, "burnout"), BURNOUT_EFFECT);

        registerElements();
        registerCommands();
        registerC2SPackets();

        ServerPlayConnectionEvents.JOIN.register(Elementals::onPlayReady);
        ServerPlayConnectionEvents.DISCONNECT.register(Elementals::onPlayerDisconnect);
        ServerLifecycleEvents.SERVER_STOPPING.register(Elementals::onPlayEnd);
        ServerPlayerEvents.AFTER_RESPAWN.register(Elementals::onPlayerRespawn);

        Registry.register(Registries.BANNER_PATTERN, "air", AIR_PATTERN);

        Registry.register(Registries.SOUND_EVENT, WIND_SOUND_ID, WIND_SOUND_EVENT);
        Registry.register(Registries.SOUND_EVENT, WIND_BURST_SOUND_ID, WIND_BURST_SOUND_EVENT);

        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(MODID, "lightning"), LIGHTNING_PARTICLE_TYPE);

        LootTableEvents.REPLACE.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if (id.equals(LootTables.DESERT_PYRAMID_ARCHAEOLOGY)) {
                List<LootPoolEntry> entries = new ArrayList<>(Arrays.asList(tableBuilder.pools[0].entries));
                entries.add(ItemEntry.builder(LIGHTNING_SCROLL_ITEM).build());

                LootPool.Builder pool = LootPool.builder().with(entries);
                return LootTable.builder().pool(pool).build();
            }if(id.equals(LootTables.FISHING_TREASURE_GAMEPLAY)){
                List<LootPoolEntry> entries = new ArrayList<>(Arrays.asList(tableBuilder.pools[0].entries));
                entries.add(ItemEntry.builder(BLOOD_SCROLL_ITEM).build());

                LootPool.Builder pool = LootPool.builder().with(entries);
                return LootTable.builder().pool(pool).build();
            }

            return null;
        });

    }



    private void registerElements() {
        new NoneElement();
        new WaterElement();
        new FireElement();
        new EarthElement();
        new AirElement();
        new LightningElement();
        new BloodElement();
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

    private static void onPlayEnd(MinecraftServer minecraftServer) {
        Bender.benders.clear();
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

    private static void onPlayerDisconnect(ServerPlayNetworkHandler handler, MinecraftServer server) {
        Bender.benders.remove(handler.player.getUuid());
    }

}