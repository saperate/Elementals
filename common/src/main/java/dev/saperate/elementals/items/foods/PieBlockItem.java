package dev.saperate.elementals.items.foods;

import com.mojang.datafixers.util.Pair;
import dev.saperate.elementals.blocks.AbstractPieBlock;
import dev.saperate.elementals.blocks.ElementalsBlocks;
import dev.saperate.elementals.items.IGrillableItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class PieBlockItem extends ItemNameBlockItem implements IGrillableItem {
    static Item.Properties properties = new Item.Properties();
    public PieBlockItem(String type, boolean isCooked) {
        super(ElementalsBlocks.PIE_BLOCKS.get(new Pair<>(type, isCooked)).get(), properties);
    }

    @Override
    protected @Nullable BlockState getPlacementState(BlockPlaceContext context) {
        return super.getPlacementState(context);//.setValue(AbstractPieBlock.PIE_TYPE, type);
    }
}
