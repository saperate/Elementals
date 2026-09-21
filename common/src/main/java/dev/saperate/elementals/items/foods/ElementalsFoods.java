package dev.saperate.elementals.items.foods;

import dev.saperate.elementals.blocks.AbstractPieBlock;
import dev.saperate.elementals.blocks.blockstates.PieTypeProperty;
import net.minecraft.world.food.FoodProperties;
import oshi.util.tuples.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ElementalsFoods {
    
    public static final FoodProperties LIGHTNING_BOTTLE_FOOD_COMPONENT = new FoodProperties.Builder()
            .alwaysEdible()
            .saturationModifier(-1.2f)
            .nutrition(-6)
            .build();
    
    public static void register(){
    }
}
