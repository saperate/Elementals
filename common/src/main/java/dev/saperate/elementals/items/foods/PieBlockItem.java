package dev.saperate.elementals.items.foods;

import com.mojang.datafixers.util.Pair;
import dev.saperate.elementals.blocks.AbstractPieBlock;
import dev.saperate.elementals.blocks.CookedPieBlock;
import dev.saperate.elementals.blocks.ElementalsBlocks;
import dev.saperate.elementals.items.IGrillableItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class PieBlockItem extends ItemNameBlockItem implements IGrillableItem {
    static Item.Properties properties = new Item.Properties();
    public final String type;
    private static final HashMap<Pair<String, Boolean>, PieBlockItem> pieItems = HashMap.newHashMap(8);
    public PieBlockItem(boolean is_cooked, String type) {
        super(is_cooked ? ElementalsBlocks.COOKED_PIE_BLOCK.get() : ElementalsBlocks.UNCOOKED_PIE_BLOCK.get(), properties);
        this.type = type;
        
        Pair<String, Boolean> pair = new Pair<>(type, is_cooked);
        if(pieItems.containsKey(pair))
            pieItems.put(pair, this);
    }

    @Override
    protected @Nullable BlockState getPlacementState(BlockPlaceContext context) {
        return super.getPlacementState(context).setValue(AbstractPieBlock.PIE_TYPE, type);
    }

    @Override
    public BlockState getBlockState() {
        return getBlock().defaultBlockState().setValue(AbstractPieBlock.PIE_TYPE, type);
    }
    
    public static PieBlockItem getItemFromBlockState(BlockState state){
        if(state.getBlock() instanceof AbstractPieBlock){
            throw new RuntimeException("Expected AbstractPieBlock's blockstate, received : " + state.getBlock().getName());
        }
        return pieItems.get(new Pair<>(state.getValue(AbstractPieBlock.PIE_TYPE), state.getBlock() instanceof CookedPieBlock));
    }
}
