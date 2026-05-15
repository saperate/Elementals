package dev.saperate.elementals.platform;

import dev.saperate.elementals.Constants;
import dev.saperate.elementals.Elementals;
import dev.saperate.elementals.ElementalsNeoForge;
import dev.saperate.elementals.blocks.ElementalsBlocks;
import dev.saperate.elementals.client.particle.MetalShardParticle;
import dev.saperate.elementals.commands.BendingCommand;
import dev.saperate.elementals.commands.ElementArgumentType;
import dev.saperate.elementals.commands.ElementalsCommand;
import dev.saperate.elementals.items.ElementalsItems;
import dev.saperate.elementals.items.WaterPouchItem;
import dev.saperate.elementals.platform.services.IRegistryHelper;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
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
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

import static dev.saperate.elementals.Elementals.LIGHTNING_PARTICLE_TYPE;
import static dev.saperate.elementals.Elementals.METAL_SHARD_PARTICLE_TYPE;
import static dev.saperate.elementals.items.ElementalsItems.*;

public class NeoForgeRegistryHelper implements IRegistryHelper {
    public static IEventBus eventBus;

    @Override
    public CreativeModeTab createItemTab() {
        return CreativeModeTab.builder()
                .icon(() -> new ItemStack(SCROLL_ITEM::get))
                .title(Component.literal("Elementals"))
                .displayItems((context, entries) -> {
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
                }).build();
    }
    @Override
    public void registerCommands() {
        NeoForge.EVENT_BUS.addListener(((RegisterCommandsEvent event) -> {
            BendingCommand.register(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
            ElementalsCommand.register(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
        }));

        //Both are needed to properly register an argument type
        ElementalsNeoForge.COMMAND_ARGUMENT_TYPES.register(
                "bending", () -> SingletonArgumentInfo.contextFree(ElementArgumentType::element)
        );
        ArgumentTypeInfos.registerByClass(ElementArgumentType.class, SingletonArgumentInfo.contextFree(ElementArgumentType::element));
    }

    @Override
    public void registerLootTables() {
        NeoForge.EVENT_BUS.addListener((LootTableLoadEvent event) -> {
            if (event.getTable().getLootTableId().equals(BuiltInLootTables.DESERT_PYRAMID_ARCHAEOLOGY.location())) {
                event.getTable().addPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ElementalsItems.LIGHTNING_SCROLL_ITEM.get())).build());
            }

            if (event.getTable().getLootTableId().equals(BuiltInLootTables.FISHING_TREASURE.location())) {
                event.getTable().addPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(BLOOD_SCROLL_ITEM.get())).build());
            }
        });
    }

    @Override
    public void registerClientParticles() {
        eventBus.addListener((RegisterParticleProvidersEvent event) -> {
            event.registerSpriteSet(LIGHTNING_PARTICLE_TYPE, FlameParticle.Provider::new);
            event.registerSpriteSet(METAL_SHARD_PARTICLE_TYPE, MetalShardParticle.Provider::new);
        });
    }

    @Override
    public void registerClientColorProviders() {
        eventBus.addListener((RegisterColorHandlersEvent.Item event) -> {
            event.register(
                    (stack, tintIndex) ->
                            tintIndex == 0 ? ((WaterPouchItem) stack.getItem()).getColor(stack) : 0xFFFFFFFF,
                    WATER_POUCH_ITEM.get());
        });

        eventBus.addListener((RegisterColorHandlersEvent.Block event) -> {
            event.register(
                    (state, view, pos, tintIndex) ->
                            0x4253ed,
                    ElementalsBlocks.MOON_PEACH_LEAVES.get());
        });
    }

    @Override
    public KeyMapping registerClientKeyBinding(KeyMapping keyMapping) {
        eventBus.addListener(((RegisterKeyMappingsEvent event) -> event.register(keyMapping)));
        return keyMapping;
    }

    @Override
    public <T extends Entity> void registerClientEntityRenderer(Supplier<EntityType<T>> type, EntityRendererProvider<T> provider) {
        eventBus.addListener(((EntityRenderersEvent.RegisterRenderers event) -> 
                event.registerEntityRenderer(type.get(), provider)));
    }

    @Override
    public <T extends GameRules.Value<T>> GameRules.Key<T> registerGameRule(String name, GameRules.Category category, GameRules.Type<T> defaultValue) {
        return GameRules.register(name, category, defaultValue);
    }

    public GameRules.Type<GameRules.BooleanValue> createGameruleIntegerType(boolean defaultValue) {
        return GameRules.BooleanValue.create(defaultValue);
    }

    public GameRules.Type<GameRules.IntegerValue> createGameruleIntegerType(int defaultValue) {
        return GameRules.IntegerValue.create(defaultValue);
    }

    @Override
    public void registerClientModelLayer(ModelLayerLocation modelMetalLanceLayer, TexturedModelDataProvider provider) {
        eventBus.addListener((EntityRenderersEvent.RegisterLayerDefinitions event) -> {
            event.registerLayerDefinition(modelMetalLanceLayer, provider::create);
        });
    }

    @Override
    public @NotNull MobEffectHolder registerEffect(String name, MobEffect effect) {
        DeferredHolder<MobEffect, MobEffect> holder = ElementalsNeoForge.MOB_EFFECTS.register(name, () -> effect);
        return holder::getDelegate;
    }

    @Override
    public <U extends SimpleCriterionTrigger.SimpleInstance, T extends SimpleCriterionTrigger<U>> TriggerHolder<U, T> registerCriterion(String name, T criterion) {
        DeferredHolder<CriterionTrigger<?>, T> holder = ElementalsNeoForge.TRIGGERS.register(name, () -> criterion);
        return holder::get;
    }

    @Override
    public Supplier<Holder<ArmorMaterial>> registerArmorMaterial(String id, ArmorMaterial material) {
        DeferredHolder<ArmorMaterial, ArmorMaterial> holder = ElementalsNeoForge.ARMOR_MATERIALS.register(id, () -> material);
        return holder::getDelegate;
    }

    @Override
    public <T extends Item> Supplier<T> registerItem(String name, Supplier<T> item) {
        return ElementalsNeoForge.ITEMS.register(name, item);
    }

    @Override
    public <T extends Item, U extends DispenseItemBehavior> void registerDispenserBehavior(Supplier<T> item, Supplier<U> behavior) {
        eventBus.addListener((FMLCommonSetupEvent event) -> {
            event.enqueueWork(() -> DispenserBlock.registerBehavior(item.get(), behavior.get()));
        });
    }

    @Override
    public void registerCreativeTab(String name, CreativeModeTab creativeModeTab) {
        ElementalsNeoForge.CREATIVE_TABS.register(name, () -> creativeModeTab);
    }

    @Override
    public Supplier<Block> registerBlock(String name, Supplier<Block> block) {
        return ElementalsNeoForge.BLOCKS.register(name, block);
    }


    @Override
    public <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntityType(String name, Supplier<Block> block, BlockEntityTypeFactory<T> factory) {
        return ElementalsNeoForge.BLOCK_ENTITY_TYPES.register(name, 
                () -> BlockEntityType.Builder.of(factory::create, block.get())
                        .build(null));
    }

    @Override
    public <T extends Entity> Supplier<EntityType<T>> registerEntity(String name, EntityType.EntityFactory<T> factory, float width, float height) {
        return ElementalsNeoForge.ENTITY_TYPES.register(name, 
                ()-> EntityType.Builder.of(factory, MobCategory.MISC)
                .noSummon()
                .sized(width, height).build(name));
    }

    @Override
    public <T extends LivingEntity> void registerDefaultEntityAttribute(Supplier<EntityType<T>> entity, Supplier<AttributeSupplier.Builder> attributeSupplier) {
        eventBus.addListener((EntityAttributeCreationEvent event) -> {
            event.put(entity.get(), attributeSupplier.get().build());
        });
    }

    @Override
    public void registerSoundEvent(ResourceLocation id, SoundEvent event) {
        ElementalsNeoForge.SOUND_EVENTS.register(id.getPath(), () -> event);
    }

    @Override
    public void registerParticleType(String name, SimpleParticleType type) {
        ElementalsNeoForge.PARTICLE_TYPES.register(name, () -> type);
    }

    @Override
    public void registerClientOverlay(ResourceLocation id, LayeredDraw.Layer layer) {
        eventBus.addListener((RegisterGuiLayersEvent event) -> event.registerAboveAll(id, layer));
    }

}
