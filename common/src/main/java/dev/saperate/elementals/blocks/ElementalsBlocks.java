package dev.saperate.elementals.blocks;

import com.mojang.datafixers.util.Pair;
import dev.saperate.elementals.Constants;
import dev.saperate.elementals.blocks.blockEntities.GrillBlockEntity;
import dev.saperate.elementals.blocks.blockEntities.LitAirBlockEntity;
import dev.saperate.elementals.items.foods.ElementalsFoods;
import dev.saperate.elementals.items.foods.PieBlockItem;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import static dev.saperate.elementals.Constants.MODID;

public class ElementalsBlocks {

    public static final Supplier<Block> LIT_AIR = Services.REGISTRY.registerBlock(
            "lit_air",
            () -> new LitAir(BlockBehaviour.Properties.of()
                    .strength(0f).lightLevel(value -> 15).noOcclusion().noCollission()
                    .emissiveRendering(ElementalsBlocks::always).isViewBlocking(ElementalsBlocks::never)
            ));

    public static final Supplier<BlockEntityType<LitAirBlockEntity>> LIT_AIR_BLOCK_ENTITY =
            Services.REGISTRY.registerBlockEntityType(
                    "lit_air_block_entity",
                    LIT_AIR, LitAirBlockEntity::new
            );

    public static final Supplier<Block> SOUL_FIRE_CORE = Services.REGISTRY.registerBlock(
            "soul_fire_core",
            () -> new SoulFireCore(BlockBehaviour.Properties.of().strength(1f)));

    public static final Supplier<Block> WATER_RAPID = Services.REGISTRY.registerBlock(
            "water_rapid",
            () -> new WaterRapid(BlockBehaviour.Properties.of().strength(1f)));

    public static final Supplier<Block> MOON_PEACH_LEAVES = Services.REGISTRY.registerBlock(
            "moonpeach_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.of()
                    .strength(1f).noOcclusion()));

    public static final Supplier<Block> MOON_PEACH_LOG = Services.REGISTRY.registerBlock(
            "moonpeach_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()
                    .strength(1f).noOcclusion().sound(SoundType.WOOD).ignitedByLava()));

    public static final Supplier<Block> MOON_PEACH_STRIPPED_LOG = Services.REGISTRY.registerBlock(
            "moonpeach_stripped_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()
                    .strength(1f).noOcclusion().sound(SoundType.WOOD).ignitedByLava()));

    public static final Supplier<Block> MOON_PEACH_PLANKS = Services.REGISTRY.registerBlock(
            "moonpeach_planks",
            () -> new Block(BlockBehaviour.Properties.of().strength(1f).noOcclusion()));

    /// Relates the pie type (string) and cooked status (boolean) to its corresponding block
    public static final HashMap<Pair<String, Boolean>, Supplier<Block>> PIE_BLOCKS = registerPieBlocks();
    
    public static final Supplier<Block> GRILL_BLOCK = Services.REGISTRY.registerBlock(
            "grill",
            () -> new GrillBlock(BlockBehaviour.Properties.of()
                    .strength(1f).noOcclusion()
                    .lightLevel(litBlockEmission(15))
            ));

    public static final Supplier<BlockEntityType<GrillBlockEntity>> GRILL_BLOCK_ENTITY =
            Services.REGISTRY.registerBlockEntityType(
                    "grill_block_entity",
                    GRILL_BLOCK, GrillBlockEntity::new
            );
    
    public static void register() {
        System.out.println("Registering elementals blocks..");
    }

    public static HashMap<Pair<String, Boolean>, Supplier<Block>> registerPieBlocks(){
        HashMap<Pair<String, Boolean>, Supplier<Block>> map = new HashMap<>(8);
        HashSet<String> types = ElementalsFoods.getPieTypes();

        for (String type : types){
            map.put(new Pair<>(type, true), Services.REGISTRY.registerBlock("cooked_" + type + "_pie", CookedPieBlock::new));
            map.put(new Pair<>(type, false), Services.REGISTRY.registerBlock("uncooked_" + type + "_pie", UncookedPieBlock::new));
        }

        return map;
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

    private static ToIntFunction<BlockState> litBlockEmission(int lightValue) {
        return (p_50763_) -> (Boolean)p_50763_.getValue(BlockStateProperties.LIT) ? lightValue : 0;
    }

}
