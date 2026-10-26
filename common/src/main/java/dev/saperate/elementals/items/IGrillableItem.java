package dev.saperate.elementals.items;

import dev.saperate.elementals.blocks.blockEntities.GrillBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IGrillableItem {
    default void grillTick(ItemStack stack, int cookTime, Level level, BlockPos pos, BlockState state, GrillBlockEntity blockEntity){};
}
