package dev.saperate.elementals.items.foods;

import com.mojang.datafixers.util.Pair;
import dev.saperate.elementals.blocks.ElementalsBlocks;
import dev.saperate.elementals.blocks.blockEntities.GrillBlockEntity;
import dev.saperate.elementals.items.ElementalsItems;
import dev.saperate.elementals.items.IGrillableItem;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Supplier;

public class PieBlockItem extends ItemNameBlockItem implements IGrillableItem {
    static Item.Properties properties = new Item.Properties();
    public final String type;
    public final boolean isCooked;
    public PieBlockItem(String type, boolean isCooked) {
        super(ElementalsBlocks.PIE_BLOCKS.get(new Pair<>(type, isCooked)).get(), 
                properties.food(ElementalsFoods.getFoodPropertyForPieType(type, isCooked)).stacksTo(isCooked ? 1 : 16));
        this.type = type;
        this.isCooked = isCooked;
    }

    public PieBlockItem getWithCooked(boolean isCooked){
        return ElementalsItems.PIE_ITEMS.get(new Pair<>(type, isCooked)).get();
    }
    
    public static PieBlockItem getWithCooked(PieBlockItem current, boolean isCooked){
        for(Map.Entry<Pair<String, Boolean>, Supplier<PieBlockItem>> entry : ElementalsItems.PIE_ITEMS.entrySet()){
            if(entry.getValue().get().equals(current)){
                return ElementalsItems.PIE_ITEMS.get(new Pair<>(entry.getKey().getFirst(), isCooked)).get();
            }
        }
        return current;
    }

    @Override
    public void grillTick(ItemStack stack, int cookTime, Level level, BlockPos pos, BlockState state, GrillBlockEntity blockEntity) {
        if(level.isClientSide && !isCooked){
            SapsUtils.summonParticles(level, pos.getBottomCenter().add(1, 12/16f, 1), level.random, ParticleTypes.WHITE_SMOKE, 0, 1, 0);
        }
        
        if(cookTime >= 300 && !isCooked){
            blockEntity.removeContainedStack();
            blockEntity.putContainedStack(getWithCooked(true).getDefaultInstance());
        }
    }
}
