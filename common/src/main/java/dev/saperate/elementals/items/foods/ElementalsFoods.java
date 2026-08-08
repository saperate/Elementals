package dev.saperate.elementals.items.foods;

import net.minecraft.world.food.FoodProperties;

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
