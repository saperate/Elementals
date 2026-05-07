package dev.saperate.elementals;

import dev.saperate.elementals.advancements.HasElementCriterion;
import dev.saperate.elementals.advancements.UsedAbilityCriterion;
import dev.saperate.elementals.blocks.ElementalsBlocks;
import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.ElementalConfig;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.effects.ElementalsStatusEffects;
import dev.saperate.elementals.enchantments.ElementalsEnchantments;
import dev.saperate.elementals.items.ElementalsItems;
import dev.saperate.elementals.items.ElementalsDynamicRecipes;
import dev.saperate.elementals.misc.BlockRestoreManager;
import dev.saperate.elementals.misc.ElementalsSounds;
import dev.saperate.elementals.misc.IItemRenderProvider;
import dev.saperate.elementals.mixin.CriteriaTriggersAccessor;
import dev.saperate.elementals.mixin.GameRulesAccessor;
import dev.saperate.elementals.mixin.GameRulesBooleanRuleAccessor;
import dev.saperate.elementals.mixin.SimpleParticleTypeAccessor;
import dev.saperate.elementals.network.ElementalsNetworking;
import dev.saperate.elementals.platform.Services;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Elementals {
    public static final String MODID = "elementals";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static HasElementCriterion HAS_ELEMENT = CriteriaTriggersAccessor.callRegister(HasElementCriterion.getName(), new HasElementCriterion());
    public static UsedAbilityCriterion USED_ABILITY = CriteriaTriggersAccessor.callRegister(UsedAbilityCriterion.getName(), new UsedAbilityCriterion());
    public static final SimpleParticleType LIGHTNING_PARTICLE_TYPE = SimpleParticleTypeAccessor.callConstructor(false);
    public static final SimpleParticleType METAL_SHARD_PARTICLE_TYPE = SimpleParticleTypeAccessor.callConstructor(false);
    public static IItemRenderProvider GLIDER_ITEM_RENDER_PROVIDER = () -> null;
    public static IItemRenderProvider METAL_ARMOR_RENDER_PROVIDER = () -> null;
    public static GameRules.Key<GameRules.BooleanValue> BENDING_GRIEFING = GameRulesAccessor.callRegister(
            "bendingGriefing",
            GameRules.Category.MISC,
            GameRulesBooleanRuleAccessor.invokeCreate(true,
                    (server, rule) -> {
                    })
    );

    public static void init() {
        LOGGER.info("Initialising the cool stuff...");
        ElementalConfig.get().loadConfig();

        ElementalsStatusEffects.register();
        ElementalsItems.register();
        ElementalsBlocks.register();
        ElementalsEnchantments.register();
        ElementalEntities.register();
        ElementalsSounds.register();
        ElementalsNetworking.register();
        Services.REGISTRY.registerLootTables();
        Services.REGISTRY.registerCommands();
        registerElements();

        Services.EVENTS.onPlayerJoin(Elementals::onPlayerJoin);
        Services.EVENTS.onPlayerDisconnect(Elementals::onPlayerDisconnect);
        Services.EVENTS.onPlayerRespawn(Elementals::onPlayerRespawn);
        Services.EVENTS.onServerClose(Elementals::onServerStop);
        Services.EVENTS.onServerTick(Elementals::onServerTick);

        Registry.register(BuiltInRegistries.PARTICLE_TYPE, ResourceLocation.fromNamespaceAndPath(MODID, "lightning"), LIGHTNING_PARTICLE_TYPE);
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, ResourceLocation.fromNamespaceAndPath(MODID, "metal_shard"), METAL_SHARD_PARTICLE_TYPE);


        ElementalsDynamicRecipes.register();
    }


    private static void registerElements() {
        new NoneElement();
        new WaterElement();
        new FireElement();
        new EarthElement();
        new AirElement();
        new LightningElement();
        new BloodElement();
        new MetalElement();
    }

    public static void onPlayerJoin(ServerPlayer player) {
        Bender.getBender(player).syncElements();
    }

    private static void onPlayerDisconnect(ServerPlayer player) {
        Bender.benders.remove(player.getUUID());
    }

    private static void onPlayerRespawn(ServerPlayer newPlayer) {
        Bender bender = Bender.getBender(newPlayer);

        if (bender.currAbility != null) {
            bender.currAbility.onRemove(bender);
            bender.setCurrAbility(null);
            bender.abilityData = null;
        }
        bender.player = newPlayer;

        PlayerData.get(newPlayer).chi = 100;
        bender.syncChi();
    }

    private static void onServerStop(MinecraftServer minecraftServer) {
        Bender.benders.clear();
        BlockRestoreManager.reset();
    }

    private static void onServerTick(MinecraftServer server) {
        BlockRestoreManager.tick(server);
    }
    
}