package dev.saperate.elementals.platform;

import dev.saperate.elementals.blocks.ElementalsBlocks;
import dev.saperate.elementals.client.particle.MetalShardParticle;
import dev.saperate.elementals.commands.BendingCommand;
import dev.saperate.elementals.commands.ElementalsCommand;
import dev.saperate.elementals.items.ElementalsItems;
import dev.saperate.elementals.items.WaterPouchItem;
import dev.saperate.elementals.platform.services.IRegistryHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import static dev.saperate.elementals.Elementals.LIGHTNING_PARTICLE_TYPE;
import static dev.saperate.elementals.Elementals.METAL_SHARD_PARTICLE_TYPE;
import static dev.saperate.elementals.items.ElementalsItems.*;

public class NeoForgeRegistryHelper implements IRegistryHelper {

    @Override
    public CreativeModeTab createItemTab() {
        return CreativeModeTab.builder()
                .icon(() -> new ItemStack(SCROLL_ITEM))
                .title(Component.literal("Elementals"))
                .displayItems((context,entries) -> {
                    entries.accept(SCROLL_ITEM);
                    entries.accept(FIRE_SCROLL_ITEM);
                    entries.accept(WATER_SCROLL_ITEM);
                    entries.accept(EARTH_SCROLL_ITEM);
                    entries.accept(AIR_SCROLL_ITEM);
                    entries.accept(LIGHTNING_SCROLL_ITEM);
                    entries.accept(BLOOD_SCROLL_ITEM);
                    entries.accept(METAL_SCROLL_ITEM);
                    entries.accept(DIRT_BOTTLE_ITEM);
                    entries.accept(LIGHTNING_BOTTLE_ITEM);
                    entries.accept(BOOMERANG_ITEM);
                    entries.accept(WATER_POUCH_ITEM);
                    entries.accept(GLIDER_ITEM);
                }).build();
    }

    @Override
    public <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BlockEntityTypeFactory<T> factory, Block... validBlocks) {
        return BlockEntityType.Builder.of(factory::create, validBlocks).build(null);
    }

    @Override
    public void registerCommands() {
        NeoForge.EVENT_BUS.addListener(((RegisterCommandsEvent event) -> {
            BendingCommand.register(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
            ElementalsCommand.register(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
        }));
    }

    @Override
    public void registerLootTables() {
        NeoForge.EVENT_BUS.addListener((LootTableLoadEvent event) -> {
            if (event.getTable().getLootTableId().equals(BuiltInLootTables.DESERT_PYRAMID_ARCHAEOLOGY.location())){
                event.getTable().addPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ElementalsItems.LIGHTNING_SCROLL_ITEM)).build());
            }

            if (event.getTable().getLootTableId().equals(BuiltInLootTables.FISHING_TREASURE.location())){
                event.getTable().addPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(BLOOD_SCROLL_ITEM)).build());
            }
        });
    }

    @Override
    public void registerClientParticles() {
        Minecraft.getInstance().particleEngine.register(LIGHTNING_PARTICLE_TYPE, FlameParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(METAL_SHARD_PARTICLE_TYPE, MetalShardParticle.Factory::new);
    }

    @Override
    public void registerClientColorProviders() {
        NeoForge.EVENT_BUS.addListener((RegisterColorHandlersEvent.Item event) -> {
            event.register(
                    (stack, tintIndex) ->
                            tintIndex == 0 ? ((WaterPouchItem) stack.getItem()).getColor(stack) : 0xFFFFFFFF,
                    WATER_POUCH_ITEM);
        });

        NeoForge.EVENT_BUS.addListener((RegisterColorHandlersEvent.Block event) -> {
            event.register(
                    (state, view, pos, tintIndex) ->
                            0x4253ed,
                    ElementalsBlocks.MOON_PEACH_LEAVES);
        });
    }

    @Override
    public KeyMapping registerKeyBinding(KeyMapping keyMapping) {
        NeoForge.EVENT_BUS.addListener(((RegisterKeyMappingsEvent event) -> {
            event.register(keyMapping);
        }));
        return keyMapping;
    }
}
