package dev.saperate.elementals.items.foods;

import com.mojang.datafixers.util.Pair;
import dev.saperate.elementals.blocks.AbstractPieBlock;
import net.minecraft.world.food.FoodProperties;

import java.util.HashMap;
import java.util.HashSet;

public class ElementalsFoods {
    /// Used to override which food property the cooked pie type will use
    private static final HashMap<String, FoodProperties> foodPropertyPieType = new HashMap<>();
    
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

    public static final FoodProperties PIE_SLICE_FOOD_COMPONENT = new FoodProperties.Builder()
            .saturationModifier(4)
            .nutrition(2)
            .build();

    public static final FoodProperties JAM_FOOD_COMPONENT = new FoodProperties.Builder()
            .saturationModifier(3.75f)
            .nutrition(2)
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
    
    private static HashMap<String, FoodProperties> getFoodPropertiesForPies(){
        HashMap<String, FoodProperties> foodProperties = new HashMap<>();
        foodProperties.put("plain", UNCOOKED_PIE_FOOD_COMPONENT);
        return foodProperties;
    }
    
    public static FoodProperties getFoodPropertyForPieType(String type, boolean isCooked){
        if(!isCooked){
            return UNCOOKED_PIE_FOOD_COMPONENT;
        }
        else if(!foodPropertyPieType.containsKey(type)){
            return COOKED_PIE_FOOD_COMPONENT;
        }
        return foodPropertyPieType.get(type);
    }
}
