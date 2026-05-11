package dev.saperate.elementals.platform.services;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public interface IRegistryHelper {

    CreativeModeTab createItemTab();
    
    <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BlockEntityTypeFactory<T> factory, Block... validBlocks);

    /**
     * Helper interface for {@link #createBlockEntityType}
     * @param <T> A type that extends BlockEntity
     */
    @FunctionalInterface
    interface BlockEntityTypeFactory<T extends BlockEntity> {
        T create(BlockPos blockPos, BlockState blockState);
    }
    
    void registerCommands();
    void registerLootTables();
    void registerClientParticles();
    void registerClientColorProviders();
    KeyMapping registerClientKeyBinding(KeyMapping keyMapping);
    <T extends Entity> void registerClientEntityRenderer(EntityType<T> type, EntityRendererProvider<T> provider);
}
