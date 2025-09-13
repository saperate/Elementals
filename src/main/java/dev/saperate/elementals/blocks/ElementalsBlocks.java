package dev.saperate.elementals.blocks;

import dev.saperate.elementals.blocks.blockEntities.LitAirBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import static dev.saperate.elementals.Elementals.MODID;

public class ElementalsBlocks {

    public static final Block LIT_AIR = Registry.register(Registries.BLOCK, 
            new Identifier(MODID, "lit_air"), new LitAir(FabricBlockSettings.create()
            .strength(0f).luminance(15).nonOpaque().noCollision()
            .emissiveLighting(Blocks::always).blockVision(Blocks::never)
    ));

    public static final BlockEntityType<LitAirBlockEntity> LIT_AIR_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier(MODID, "lit_air_block_entity"),
            BlockEntityType.Builder.create(LitAirBlockEntity::new, LIT_AIR).build(null)
    );
    
    public static final Block SOUL_FIRE_CORE = Registry.register(Registries.BLOCK,
            new Identifier(MODID,"soul_fire_core"), 
            new SoulFireCore(FabricBlockSettings.create().strength(1f)));
    
    public static final Block WATER_RAPID = Registry.register(Registries.BLOCK,
            new Identifier(MODID,"water_rapid"), 
            new WaterRapid(FabricBlockSettings.create().strength(1f)));

    public static final Block MOON_PEACH_LEAVES = Registry.register(Registries.BLOCK,
            new Identifier(MODID,"moon_leaves"),
            new LeavesBlock(FabricBlockSettings.create().strength(1f).nonOpaque()));

    public static final Block MOON_LOG = Registry.register(Registries.BLOCK,
            new Identifier(MODID,"moon_log"),
            new PillarBlock(FabricBlockSettings.create().strength(1f).nonOpaque().sounds(BlockSoundGroup.WOOD).burnable()));

    public static final Block MOON_STRIPPED_LOG = Registry.register(Registries.BLOCK,
            new Identifier(MODID,"moon_stripped_log"),
            new PillarBlock(FabricBlockSettings.create().strength(1f).nonOpaque().sounds(BlockSoundGroup.WOOD).burnable()));

    public static final Block MOON_PLANKS = Registry.register(Registries.BLOCK,
            new Identifier(MODID,"moon_planks"),
            new Block(FabricBlockSettings.create().strength(1f).nonOpaque()));
    
    
    public static void registerBlocks(){
        System.out.println("Registering elementals blocks..");
    }
}
