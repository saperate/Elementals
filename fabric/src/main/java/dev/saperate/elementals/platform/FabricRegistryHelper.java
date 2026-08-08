package dev.saperate.elementals.platform;

import dev.saperate.elementals.Constants;
import dev.saperate.elementals.Elementals;
import dev.saperate.elementals.blocks.ElementalsBlocks;
import dev.saperate.elementals.client.particle.MetalShardParticle;
import dev.saperate.elementals.commands.BendingCommand;
import dev.saperate.elementals.commands.ElementArgumentType;
import dev.saperate.elementals.commands.ElementalsCommand;
import dev.saperate.elementals.items.ElementalsItems;
import dev.saperate.elementals.items.WaterPouchItem;
import dev.saperate.elementals.platform.services.IRegistryHelper;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static dev.saperate.elementals.Elementals.LIGHTNING_PARTICLE_TYPE;
import static dev.saperate.elementals.Elementals.METAL_SHARD_PARTICLE_TYPE;
import static dev.saperate.elementals.items.ElementalsItems.*;

public class FabricRegistryHelper implements IRegistryHelper {

    @Override
    public CreativeModeTab createItemTab() {
        return FabricItemGroup.builder()
                .icon(() -> new ItemStack(SCROLL_ITEM.get()))
                .title(Component.literal("Elementals"))
                .displayItems((context,entries) -> {
                    entries.accept(SCROLL_ITEM.get());
                    entries.accept(FIRE_SCROLL_ITEM.get());
                    entries.accept(WATER_SCROLL_ITEM.get());
                    entries.accept(EARTH_SCROLL_ITEM.get());
                    entries.accept(AIR_SCROLL_ITEM.get());
                    entries.accept(LIGHTNING_SCROLL_ITEM.get());
                    entries.accept(BLOOD_SCROLL_ITEM.get());
                    entries.accept(METAL_SCROLL_ITEM.get());
                    entries.accept(DIRT_BOTTLE_ITEM.get());
                    entries.accept(LIGHTNING_BOTTLE_ITEM.get());
                    entries.accept(BOOMERANG_ITEM.get());
                    entries.accept(WATER_POUCH_ITEM.get());
                    entries.accept(GLIDER_ITEM.get());
//                    entries.accept(UNCOOKED_PLAIN_PIE_ITEM.get());
//                    entries.accept(COOKED_PLAIN_PIE_ITEM.get());
//                    entries.accept(UNCOOKED_MOONPEACH_PIE_ITEM.get());
//                    entries.accept(COOKED_MOONPEACH_PIE_ITEM.get());
//                    entries.accept(UNCOOKED_STARBERRY_PIE_ITEM.get());
//                    entries.accept(COOKED_STARBERRY_PIE_ITEM.get());
//                    entries.accept(UNCOOKED_SWEETBERRY_PIE_ITEM.get());
//                    entries.accept(COOKED_SWEETBERRY_PIE_ITEM.get());
                }).build();
    }

    @Override
    public void registerCommands() {
        CommandRegistrationCallback.EVENT.register(BendingCommand::register);
        CommandRegistrationCallback.EVENT.register(ElementalsCommand::register);

        ArgumentTypeRegistry.registerArgumentType(
                ResourceLocation.fromNamespaceAndPath(Constants.MODID, "bending"),
                ElementArgumentType.class,
                SingletonArgumentInfo.contextFree(ElementArgumentType::element));
    }

    @Override
    public void registerLootTables() {
        LootTableEvents.MODIFY.register(((key, tableBuilder, source, registries) -> {
            if(source.isBuiltin() && (BuiltInLootTables.DESERT_PYRAMID_ARCHAEOLOGY.equals(key))){
                tableBuilder.modifyPools((builder)->{
                    builder.with(LootItem.lootTableItem(ElementalsItems.LIGHTNING_SCROLL_ITEM.get()).build());
                });
            }else if(source.isBuiltin() && (BuiltInLootTables.FISHING_TREASURE.equals(key))){
                tableBuilder.modifyPools((builder)->{
                    builder.with(LootItem.lootTableItem(ElementalsItems.BLOOD_SCROLL_ITEM.get()).build());
                });
            }
        }));
    }

    @Override
    public void registerClientParticles() {
        ParticleFactoryRegistry.getInstance().register(LIGHTNING_PARTICLE_TYPE, FlameParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(METAL_SHARD_PARTICLE_TYPE, MetalShardParticle.Provider::new);
    }

    @Override
    public void registerClientColorProviders() {
        ColorProviderRegistry.ITEM.register(
                (stack, tintIndex) -> 
                        tintIndex == 0 ? ((WaterPouchItem) stack.getItem()).getColor(stack) : 0xFFFFFFFF,
                ElementalsItems.WATER_POUCH_ITEM.get()
        );

        ColorProviderRegistry.BLOCK.register(
                (state, view, pos, tintIndex) -> 
                        0x4253ed, 
                ElementalsBlocks.MOON_PEACH_LEAVES.get()
        );
    }

    @Override
    public KeyMapping registerClientKeyBinding(KeyMapping keyMapping) {
        return KeyBindingHelper.registerKeyBinding(keyMapping);
    }

    @Override
    public <T extends Entity> void registerClientEntityRenderer(Supplier<EntityType<T>> type, EntityRendererProvider<T> provider) {
        EntityRendererRegistry.register(type.get(), provider);
    }
    
    @Override
    public <T extends GameRules.Value<T>> GameRules.Key<T> registerGameRule(String name, GameRules.Category category, GameRules.Type<T> defaultValue) {
        return GameRuleRegistry.register(name, category, defaultValue);
    }

    public GameRules.Type<GameRules.BooleanValue> createGameruleIntegerType(boolean defaultValue){
        return GameRuleFactory.createBooleanRule(defaultValue);
    }

    public GameRules.Type<GameRules.IntegerValue> createGameruleIntegerType(int defaultValue){
        return GameRuleFactory.createIntRule(defaultValue);
    }

    @Override
    public void registerClientModelLayer(ModelLayerLocation modelMetalLanceLayer, TexturedModelDataProvider provider) {
        EntityModelLayerRegistry.registerModelLayer(modelMetalLanceLayer, provider::create);
    }

    @Override
    public @NotNull MobEffectHolder registerEffect(String name, MobEffect effect) {
        Holder<MobEffect> holder = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,
                ResourceLocation.fromNamespaceAndPath(Constants.MODID, name), effect);
        return () -> holder;
    }

    @Override
    public <U extends SimpleCriterionTrigger.SimpleInstance, T extends SimpleCriterionTrigger<U>> TriggerHolder<U, T> registerCriterion(String name, T criterion) {
        T trigger = Registry.register(
                BuiltInRegistries.TRIGGER_TYPES,
                ResourceLocation.fromNamespaceAndPath(Constants.MODID, name), 
                criterion);
        return () -> trigger;
    }

    @Override
    public Supplier<Holder<ArmorMaterial>> registerArmorMaterial(String id, ArmorMaterial material) {
        Holder<ArmorMaterial> holder = Registry.registerForHolder(BuiltInRegistries.ARMOR_MATERIAL,
                ResourceLocation.fromNamespaceAndPath(Constants.MODID, id),
                material);
        return () -> holder;
    }

    @Override
    public <T extends Item> Supplier<T>  registerItem(String name, Supplier<T> item){
        T holder = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Constants.MODID,name), item.get());
        return () -> holder;
    }

    @Override
    public <T extends Item, U extends DispenseItemBehavior> void registerDispenserBehavior(Supplier<T> item, Supplier<U> behavior) {
        DispenserBlock.registerBehavior(item.get(), behavior.get());
    }

    @Override
    public void registerCreativeTab(String name, CreativeModeTab creativeModeTab) {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, 
                ResourceLocation.fromNamespaceAndPath(Constants.MODID,name) , 
                creativeModeTab
        );
    }

    @Override
    public Supplier<Block> registerBlock(String name, Supplier<Block> block) {
        Block holder = Registry.register(BuiltInRegistries.BLOCK,
                ResourceLocation.fromNamespaceAndPath(Constants.MODID, name), block.get());
        return () -> holder;
    }

    @Override
    public <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntityType(String name, Supplier<Block> block, BlockEntityTypeFactory<T> factory) {
        BlockEntityType<T> holder = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, 
                ResourceLocation.fromNamespaceAndPath(Constants.MODID, name),
                BlockEntityType.Builder.of(factory::create, block.get()).build());
        return () -> holder;
    }

    @Override
    public <T extends Entity> Supplier<EntityType<T>> registerEntity(String name, EntityType.EntityFactory<T> factory, float width, float height) {
        EntityType<T> entityType = Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(Constants.MODID, name),
                EntityType.Builder.of(factory, MobCategory.MISC)
                        .noSummon()
                        .sized(width, height).build(name));
        return () -> entityType;
    }

    @Override
    public <T extends LivingEntity> void registerDefaultEntityAttribute(Supplier<EntityType<T>> entity, Supplier<AttributeSupplier.Builder> attributeSupplier) {
        FabricDefaultAttributeRegistry.register(entity.get(), attributeSupplier.get());
    }

    @Override
    public void registerSoundEvent(ResourceLocation id, SoundEvent event) {
        Registry.register(BuiltInRegistries.SOUND_EVENT, id, event);
    }

    @Override
    public void registerParticleType(String name, SimpleParticleType type) {
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, 
                ResourceLocation.fromNamespaceAndPath(Constants.MODID, name), type);
    }

    @Override
    public void registerClientOverlay(ResourceLocation id, LayeredDraw.Layer layer) {
        HudRenderCallback.EVENT.register(layer::render);
    }
}
