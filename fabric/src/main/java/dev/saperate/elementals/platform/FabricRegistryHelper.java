package dev.saperate.elementals.platform;

import dev.saperate.elementals.Elementals;
import dev.saperate.elementals.blocks.ElementalsBlocks;
import dev.saperate.elementals.client.particle.MetalShardParticle;
import dev.saperate.elementals.commands.BendingCommand;
import dev.saperate.elementals.commands.ElementalsCommand;
import dev.saperate.elementals.items.ElementalsItems;
import dev.saperate.elementals.items.WaterPouchItem;
import dev.saperate.elementals.platform.services.IRegistryHelper;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.entries.LootItem;

import static dev.saperate.elementals.Elementals.LIGHTNING_PARTICLE_TYPE;
import static dev.saperate.elementals.Elementals.METAL_SHARD_PARTICLE_TYPE;
import static dev.saperate.elementals.items.ElementalsItems.*;

public class FabricRegistryHelper implements IRegistryHelper {

    @Override
    public CreativeModeTab createItemTab() {
        return FabricItemGroup.builder()
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
        return BlockEntityType.Builder.of(factory::create, validBlocks).build();
    }

    @Override
    public void registerCommands() {
        CommandRegistrationCallback.EVENT.register(BendingCommand::register);
        CommandRegistrationCallback.EVENT.register(ElementalsCommand::register);
    }

    @Override
    public void registerLootTables() {
        LootTableEvents.MODIFY.register(((key, tableBuilder, source, registries) -> {
            if(source.isBuiltin() && (BuiltInLootTables.DESERT_PYRAMID_ARCHAEOLOGY.equals(key))){
                tableBuilder.modifyPools((builder)->{
                    builder.with(LootItem.lootTableItem(ElementalsItems.LIGHTNING_SCROLL_ITEM).build());
                });
            }else if(source.isBuiltin() && (BuiltInLootTables.FISHING_TREASURE.equals(key))){
                tableBuilder.modifyPools((builder)->{
                    builder.with(LootItem.lootTableItem(ElementalsItems.BLOOD_SCROLL_ITEM).build());
                });
            }
        }));
    }

    @Override
    public void registerClientParticles() {
        ParticleFactoryRegistry.getInstance().register(LIGHTNING_PARTICLE_TYPE, FlameParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(METAL_SHARD_PARTICLE_TYPE, MetalShardParticle.Factory::new);
    }

    @Override
    public void registerClientColorProviders() {
        ColorProviderRegistry.ITEM.register(
                (stack, tintIndex) -> 
                        tintIndex == 0 ? ((WaterPouchItem) stack.getItem()).getColor(stack) : 0xFFFFFFFF,
                ElementalsItems.WATER_POUCH_ITEM
        );

        ColorProviderRegistry.BLOCK.register(
                (state, view, pos, tintIndex) -> 
                        0x4253ed, 
                ElementalsBlocks.MOON_PEACH_LEAVES
        );
    }

    @Override
    public KeyMapping registerKeyBinding(KeyMapping keyMapping) {
        return KeyBindingHelper.registerKeyBinding(keyMapping);
    }

}
