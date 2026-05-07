package dev.saperate.elementals;

import dev.saperate.elementals.advancements.HasElementCriterion;
import dev.saperate.elementals.advancements.UsedAbilityCriterion;
import dev.saperate.elementals.blocks.ElementalsBlocks;
import dev.saperate.elementals.data.ElementalConfig;
import dev.saperate.elementals.effects.ElementalsStatusEffects;
import dev.saperate.elementals.enchantments.ElementalsEnchantments;
import dev.saperate.elementals.items.ElementalsItems;
import dev.saperate.elementals.items.ElementalsDynamicRecipes;
import dev.saperate.elementals.misc.BlockRestoreManager;
import dev.saperate.elementals.misc.ElementalsSounds;
import dev.saperate.elementals.misc.IItemRenderProvider;
import dev.saperate.elementals.mixin.GameRulesAccessor;
import dev.saperate.elementals.mixin.GameRulesBooleanRuleAccessor;
import dev.saperate.elementals.platform.Services;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Elementals {
    public static final String MODID = "elementals";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static HasElementCriterion HAS_ELEMENT = CriteriaTriggers.register(HasElementCriterion.getName(),new HasElementCriterion());
    public static UsedAbilityCriterion USED_ABILITY = CriteriaTriggers.register(UsedAbilityCriterion.getName(),new UsedAbilityCriterion());
    public static final SimpleParticleType LIGHTNING_PARTICLE_TYPE = FabricParticleTypes.simple();
    public static final SimpleParticleType METAL_SHARD_PARTICLE_TYPE = FabricParticleTypes.simple();
    public static IItemRenderProvider GLIDER_ITEM_RENDER_PROVIDER;
    public static IItemRenderProvider METAL_ARMOR_RENDER_PROVIDER;
    public static GameRules.Key<GameRules.BooleanValue> BENDING_GRIEFING = GameRulesAccessor.callRegister(
            "bendingGriefing",
            GameRules.Category.MISC,
            GameRulesBooleanRuleAccessor.invokeCreate(true, 
                    (server, rule) -> {})
    ); 

    public static void init() {
        LOGGER.info("Initialising the cool stuff...");
        ElementalConfig.get().loadConfig();
        
        ElementalsStatusEffects.registerEffects();
        ElementalsItems.registerItems();
        ElementalsBlocks.registerBlocks();
        ElementalsEnchantments.registerEnchantments();
        ElementalEntities.register();
        ElementalsSounds.register();
        
        //Inner registration
        initElements();
        initNetworking();

        Services.REGISTRY.modifyLootTables();
        Services.REGISTRY.registerGameRules();
        
        ServerPlayConnectionEvents.JOIN.register(Elementals::onPlayReady);
        ServerPlayConnectionEvents.DISCONNECT.register(Elementals::onPlayerDisconnect);
        ServerLifecycleEvents.SERVER_STOPPING.register(Elementals::onPlayEnd);
        ServerTickEvents.END_SERVER_TICK.register(Elementals::onTickEnd);
        ServerPlayerEvents.AFTER_RESPAWN.register(Elementals::onPlayerRespawn);
        ServerLifecycleEvents.SERVER_STOPPING.register(Elementals::onServerStopping);


        Registry.register(BuiltInRegistries.PARTICLE_TYPE, ResourceLocation.fromNamespaceAndPath(MODID, "lightning"), LIGHTNING_PARTICLE_TYPE);
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, ResourceLocation.fromNamespaceAndPath(MODID, "metal_shard"), METAL_SHARD_PARTICLE_TYPE);
        
        
        ElementalsDynamicRecipes.register();
    }


    private static void initElements() {
        new NoneElement();
        new WaterElement();
        new FireElement();
        new EarthElement();
        new AirElement();
        new LightningElement();
        new BloodElement();
        new MetalElement();
    }
    
    
    private static void onTickEnd(MinecraftServer server) {
        BlockRestoreManager.tick(server);
    }
    private static void onServerStopping(MinecraftServer server) {

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

        BlockRestoreManager.reset();
    }
}