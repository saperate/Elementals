package dev.saperate.elementals.items.foods;

import dev.saperate.elementals.blocks.AbstractPieBlock;
import dev.saperate.elementals.blocks.ElementalsBlocks;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class PieBlockItem extends ItemNameBlockItem {
    static Item.Properties properties = new Item.Properties();
    public final String type;
    public PieBlockItem(boolean is_cooked, String type) {
        super(is_cooked ? ElementalsBlocks.COOKED_PIE_BLOCK.get() : ElementalsBlocks.UNCOOKED_PIE_BLOCK.get(), properties);
        this.type = type; 
    }

    @Override
    protected @Nullable BlockState getPlacementState(BlockPlaceContext context) {
        return super.getPlacementState(context).setValue(AbstractPieBlock.PIE_TYPE, type);
    }
}
