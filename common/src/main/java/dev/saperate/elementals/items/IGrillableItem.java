package dev.saperate.elementals.items;

import dev.saperate.elementals.blocks.blockEntities.GrillBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IGrillableItem {
    default void grillTick(ItemStack stack, int cookTime, Level level, BlockPos pos, BlockState state, GrillBlockEntity blockEntity){};

    default boolean useWithoutItem(ItemStack stack, Level level, BlockPos pos, Player player){return false;}
    
    default boolean useItemOn(ItemStack stack, Level level, BlockPos pos, Player player, ItemStack itemUsed){return false;}
}
