package dev.saperate.elementals.items;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.saperate.elementals.Constants;
import dev.saperate.elementals.Elementals;
import dev.saperate.elementals.data.ElementalConfig;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;

public class ElementalsDynamicRecipes {
    
    public static ArrayList<RecipeEntry> entries = new ArrayList<>();
    
    
    public static void register(){
        if(!ElementalConfig.get().CRAFTABLE_SCROLLS)
            return;
        
        
         entries.add(createShapedRecipeJson(
                Lists.newArrayList('p', 'c', 'd'),
                Lists.newArrayList(ResourceLocation.withDefaultNamespace("paper"), 
                        ResourceLocation.withDefaultNamespace("fire_charge"), 
                        ResourceLocation.withDefaultNamespace("diamond")),
                Lists.newArrayList("item", "item", "item"),
                Lists.newArrayList(
                        "dcd",
                        "dpd",
                        "dcd"
                ), 
                ResourceLocation.fromNamespaceAndPath(Constants.MODID, "fire_scroll")
        ));


        entries.add(createShapedRecipeJson(
                Lists.newArrayList('p', 'c', 'd'),
                Lists.newArrayList(ResourceLocation.withDefaultNamespace("paper"),
                        ResourceLocation.withDefaultNamespace("sponge"),
                        ResourceLocation.withDefaultNamespace("diamond")),
                Lists.newArrayList("item", "item", "item"),
                Lists.newArrayList(
                        "dcd",
                        "dpd",
                        "dcd"
                ),
                ResourceLocation.fromNamespaceAndPath(Constants.MODID, "water_scroll")
        ));


        entries.add(createShapedRecipeJson(
                Lists.newArrayList('p', 'c', 'd'),
                Lists.newArrayList(ResourceLocation.withDefaultNamespace("paper"),
                        ResourceLocation.withDefaultNamespace("mud"),
                        ResourceLocation.withDefaultNamespace("diamond")),
                Lists.newArrayList("item", "item", "item"),
                Lists.newArrayList(
                        "dcd",
                        "dpd",
                        "dcd"
                ),
                ResourceLocation.fromNamespaceAndPath(Constants.MODID, "earth_scroll")
        ));


        entries.add(createShapedRecipeJson(
                Lists.newArrayList('p', 'c', 'd'),
                Lists.newArrayList(ResourceLocation.withDefaultNamespace("paper"),
                        ResourceLocation.withDefaultNamespace("wind_charge"),
                        ResourceLocation.withDefaultNamespace("diamond")),
                Lists.newArrayList("item", "item", "item"),
                Lists.newArrayList(
                        "dcd",
                        "dpd",
                        "dcd"
                ),
                ResourceLocation.fromNamespaceAndPath(Constants.MODID, "air_scroll")
        ));
    }
    
    /**
     * @see "https://wiki.fabricmc.net/tutorial:dynamic_recipe_generation"
     */
    public static RecipeEntry createShapedRecipeJson(ArrayList<Character> keys, ArrayList<ResourceLocation> items, ArrayList<String> type, ArrayList<String> pattern, ResourceLocation output) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "minecraft:crafting_shaped");

        
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(pattern.get(0));
        jsonArray.add(pattern.get(1));
        jsonArray.add(pattern.get(2));

        
        json.add("pattern", jsonArray);

        
        JsonObject individualKey;
        JsonObject keyList = new JsonObject();

        for (int i = 0; i < keys.size(); ++i) {
            individualKey = new JsonObject();
            individualKey.addProperty(type.get(i), items.get(i).toString());
            keyList.add(keys.get(i) + "", individualKey);
        }

        json.add("key", keyList);
        
        JsonObject result = new JsonObject();
        result.addProperty("id", output.toString());
        result.addProperty("count", 1);
        json.add("result", result);
        return new RecipeEntry(json, output);
    }
    
    public static final class RecipeEntry{
        public final JsonObject object;
        public final ResourceLocation ID;
        private RecipeEntry(JsonObject object, ResourceLocation ID){
            this.object = object;
            this.ID = ID;
        }
    }
}
