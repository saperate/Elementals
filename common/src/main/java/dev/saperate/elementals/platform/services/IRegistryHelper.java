package dev.saperate.elementals.platform.services;

import dev.saperate.elementals.advancements.HasElementCriterion;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
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
    <T extends GameRules.Value<T>> GameRules.Key<T> registerGameRule(String name, GameRules.Category category, GameRules.Type<T> defaultValue);
    GameRules.Type<GameRules.BooleanValue> createGameruleIntegerType(boolean defaultValue);
    GameRules.Type<GameRules.IntegerValue> createGameruleIntegerType(int defaultValue);
    void registerClientModelLayer(ModelLayerLocation modelMetalLanceLayer, TexturedModelDataProvider provider);
    @FunctionalInterface
    interface TexturedModelDataProvider {
        LayerDefinition create();
    }
    @NotNull MobEffectHolder registerEffect(String name, MobEffect effect);
    @FunctionalInterface
    interface MobEffectHolder {
        Holder<MobEffect> get();
    }
    <U extends SimpleCriterionTrigger.SimpleInstance, T extends SimpleCriterionTrigger<U>> TriggerHolder<U,T> registerCriterion(String name, T criterion);
    @FunctionalInterface
    interface TriggerHolder<U extends SimpleCriterionTrigger.SimpleInstance, T extends SimpleCriterionTrigger<U>>{
        T get();
    }
}
