package dev.saperate.elementals.blocks;

import dev.saperate.elementals.Constants;
import dev.saperate.elementals.blocks.blockEntities.LitAirBlockEntity;
import dev.saperate.elementals.platform.Services;
import dev.saperate.elementals.platform.services.IRegistryHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.ToIntFunction;

import static dev.saperate.elementals.Constants.MODID;

public class ElementalsBlocks {

    public static final Block LIT_AIR = Registry.register(BuiltInRegistries.BLOCK, 
            ResourceLocation.fromNamespaceAndPath(MODID, "lit_air"), 
            new LitAir(BlockBehaviour.Properties.of()
            .strength(0f).lightLevel(value -> 15).noOcclusion().noCollission()
            .emissiveRendering(ElementalsBlocks::always).isViewBlocking(ElementalsBlocks::never)
    ));

    public static final BlockEntityType<LitAirBlockEntity> LIT_AIR_BLOCK_ENTITY = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath(MODID, "lit_air_block_entity"),
            Services.REGISTRY.createBlockEntityType(LitAirBlockEntity::new, LIT_AIR)
    );
    
    public static final Block SOUL_FIRE_CORE = Registry.register(BuiltInRegistries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(MODID,"soul_fire_core"), 
            new SoulFireCore(BlockBehaviour.Properties.of().strength(1f)));
    
    public static final Block WATER_RAPID = Registry.register(BuiltInRegistries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(MODID,"water_rapid"), 
            new WaterRapid(BlockBehaviour.Properties.of().strength(1f)));

    public static final Block MOON_PEACH_LEAVES = Registry.register(BuiltInRegistries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(MODID,"moon_leaves"),
            new LeavesBlock(BlockBehaviour.Properties.of().strength(1f).noOcclusion()));

    public static final Block MOON_LOG = Registry.register(BuiltInRegistries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(MODID,"moon_log"),
            new RotatedPillarBlock(BlockBehaviour.Properties.of().strength(1f).noOcclusion().sound(SoundType.WOOD).ignitedByLava()));

    public static final Block MOON_STRIPPED_LOG = Registry.register(BuiltInRegistries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(MODID,"moon_stripped_log"),
            new RotatedPillarBlock(BlockBehaviour.Properties.of().strength(1f).noOcclusion().sound(SoundType.WOOD).ignitedByLava()));

    public static final Block MOON_PLANKS = Registry.register(BuiltInRegistries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(MODID,"moon_planks"),
            new Block(BlockBehaviour.Properties.of().strength(1f).noOcclusion()));
    
    
    public static void registerBlocks(){
        System.out.println("Registering elementals blocks..");
    }
    
    private static ToIntFunction<BlockState> always15() {
        return value -> 15;
    }

    private static boolean always(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return true;
    }

    private static boolean never(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return false;
    }

}
