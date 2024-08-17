package dev.saperate.elementals.blocks;

import dev.saperate.elementals.blocks.blockEntities.LitAirBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static dev.saperate.elementals.Elementals.LIT_AIR_BLOCK_ENTITY;
import static dev.saperate.elementals.Elementals.MODID;

public class LitAir extends BlockWithEntity {
    public static final Block LIT_AIR = Registry.register(Registries.BLOCK, new Identifier(MODID, "lit_air"), new LitAir(FabricBlockSettings.create()
            .strength(0f).luminance(15).nonOpaque().noCollision()
            .emissiveLighting(Blocks::always).blockVision(Blocks::never)
    ));

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    public LitAir(Settings settings) {
        super(settings);
    }

    public static Block registerBlock() {
        return LIT_AIR;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LitAirBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, LIT_AIR_BLOCK_ENTITY, LitAirBlockEntity::tick);
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return true;
    }
}
