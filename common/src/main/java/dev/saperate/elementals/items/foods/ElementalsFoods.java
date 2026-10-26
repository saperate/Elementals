package dev.saperate.elementals.items.foods;

import com.mojang.datafixers.util.Pair;
import dev.saperate.elementals.blocks.AbstractPieBlock;
import net.minecraft.world.food.FoodProperties;

import java.util.HashMap;
import java.util.HashSet;

public class ElementalsFoods {
    
    public static final FoodProperties LIGHTNING_BOTTLE_FOOD_COMPONENT = new FoodProperties.Builder()
            .alwaysEdible()
            .saturationModifier(-1.2f)
            .nutrition(-6)
            .build();

    public static final FoodProperties UNCOOKED_PIE_FOOD_COMPONENT = new FoodProperties.Builder()
            .saturationModifier(2)
            .nutrition(4)
            .build();

    public static final FoodProperties COOKED_PIE_FOOD_COMPONENT = new FoodProperties.Builder()
            .saturationModifier(15)
            .nutrition(8)
            .build();
    
    public static void register(){
    }
    
    public static HashSet<String> getPieTypes(){
        HashSet<String> set = new HashSet<>(4);
        set.add("plain");
        set.add("moonpeach");
        set.add("starberry");
        set.add("sweetberry");
        return set;
    }
}
