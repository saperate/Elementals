package dev.saperate.elementals.data;

import com.google.gson.*;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.earth.EarthElement;
import dev.saperate.elementals.elements.water.AbilityWaterTower;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class ElementalConfig {
    //TODO sync configs when joining a server
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    //XP
    public float XP_MULTIPLIER = 0.1f;
    //CHI
    public int MAX_CHI = 100;
    public float CHI_REGENERATION_RATE = 0.1f;
    //GUI
    public int CHI_OVERLAY_THRESHOLD = 115;
    public boolean CHI_OVERLAY_TEXT = false;
    public boolean HIDE_TIMER = false;
    public HashMap<Item, Integer> METAL_COST_VALUE = new HashMap<>(){{
        put(Items.COPPER_INGOT, 9);
        put(Items.COPPER_ORE, 6);
        put(Items.IRON_INGOT, 9);
        put(Items.IRON_NUGGET, 1);
        put(Items.IRON_ORE, 6);
        put(Items.GOLD_INGOT, 9);
        put(Items.GOLD_ORE, 6);
        put(Items.GOLD_NUGGET, 1);
    }};
    /**
     * Ex: 1x Ingot -> 9x Iron Ingot
     */
    public HashMap<Item, Item> METAL_LOWER_VALUE_STACK = new HashMap<>(){{
        put(Items.IRON_INGOT, Items.IRON_NUGGET);
        put(Items.IRON_ORE, Items.IRON_NUGGET);
        put(Items.GOLD_INGOT, Items.GOLD_NUGGET);
        put(Items.GOLD_ORE, Items.GOLD_NUGGET);
    }};
    public boolean CRAFTABLE_SCROLLS = false;
 
    public void loadConfig() {
        JsonObject root;
        try {
            root = gson.fromJson(new FileReader(getConfigFile()), JsonElement.class).getAsJsonObject();
        } catch (FileNotFoundException fileE) {
            System.err.println("Could not find elementals config, generating a new one!");
            generateWriteConfig();
            return;
        }
        try{
            XP_MULTIPLIER = root.get("XP_MULTIPLIER").getAsFloat();
            MAX_CHI = root.get("MAX_CHI").getAsInt();
            CHI_REGENERATION_RATE = root.get("CHI_REGENERATION_RATE").getAsFloat();
            CHI_OVERLAY_THRESHOLD = root.get("CHI_OVERLAY_THRESHOLD").getAsInt();
            CHI_OVERLAY_TEXT = root.get("CHI_OVERLAY_TEXT").getAsBoolean();
            HIDE_TIMER = root.get("HIDE_TIMER").getAsBoolean();
            METAL_COST_VALUE = getMetalCostValue(root);
            METAL_LOWER_VALUE_STACK = getMetalLowerValueStack(root);
            CRAFTABLE_SCROLLS = root.get("CRAFTABLE_SCROLLS").getAsBoolean();
            
        }catch (Exception e){
            // This doesn't override the file, just makes it so we don't use what we loaded
            System.err.println("Elemental config was malformed or outdated! Try deleting your old config." +
                    " Reverting back to default values\n" + e);
            resetConfig();
        }

    }
    

    private void generateWriteConfig(){
        try{
            File file = getConfigFile();
            FileWriter writer = new FileWriter(file);

            JsonObject root = new JsonObject();
            root.addProperty("XP_MULTIPLIER", 0.1f);
            root.addProperty("MAX_CHI", 100);
            root.addProperty("CHI_REGENERATION_RATE", 0.1f);
            root.addProperty("CHI_OVERLAY_THRESHOLD", 115);
            root.addProperty("CHI_OVERLAY_TEXT", false);
            root.addProperty("HIDE_TIMER", false);

            
            JsonArray metalCostValue = new JsonArray();
            for (Item item : METAL_COST_VALUE.keySet()) {
                JsonObject entry = new JsonObject();
                entry.addProperty("ITEM",
                        Registries.ITEM.getId(item).toString());
                entry.addProperty("VALUE",
                        METAL_COST_VALUE.get(item));
                metalCostValue.add(entry);
            }
            root.add("METAL_COST_VALUE", metalCostValue);

            
            JsonArray metalLowerValueStack = new JsonArray();
            for (Item item : METAL_LOWER_VALUE_STACK.keySet()) {
                JsonObject entry = new JsonObject();
                entry.addProperty("ITEM_BEFORE", 
                        Registries.ITEM.getId(item).toString());
                entry.addProperty("ITEM_AFTER", 
                        Registries.ITEM.getId(METAL_LOWER_VALUE_STACK.get(item)).toString());
                metalLowerValueStack.add(entry);
            }
            root.add("METAL_LOWER_VALUE_STACK", metalLowerValueStack);


            root.addProperty("CRAFTABLE_SCROLLS", false);

            writer.append(gson.toJson(root));
            writer.close();
        }catch (Exception e){
            System.err.println("Could not generate config!\n" + e);
        }
    }
    
    


    private HashMap<Item, Integer> getMetalCostValue(JsonObject root) throws IllegalStateException {
        JsonArray array = root.get("METAL_COST_VALUE").getAsJsonArray();
        HashMap<Item, Integer> out = new HashMap<>();
        
        for(JsonElement rawEntry : array.asList()){
            JsonObject entry = rawEntry.getAsJsonObject();
            out.put(
                    Registries.ITEM.get(Identifier.splitOn(entry.get("ITEM").getAsString(),':')), 
                    entry.get("VALUE").getAsInt());
        }
        
        return out;
    }


    private HashMap<Item, Item> getMetalLowerValueStack(JsonObject root) throws IllegalStateException {
        JsonArray array = root.get("METAL_LOWER_VALUE_STACK").getAsJsonArray();
        HashMap<Item, Item> out = new HashMap<>();

        for(JsonElement rawEntry : array.asList()){
            JsonObject entry = rawEntry.getAsJsonObject();
            out.put(
                    Registries.ITEM.get(Identifier.splitOn(entry.get("ITEM_BEFORE").getAsString(),':')),
                    Registries.ITEM.get(Identifier.splitOn(entry.get("ITEM_AFTER").getAsString(),':')));
        }

        return out;
    }



    private File getConfigFile() {
        return new File(FabricLoader.getInstance().getConfigDir().toFile(), "elementals.json");
    }

    public ElementalConfig resetConfig() {
        instance = new ElementalConfig();
        return instance;
    }

    private ElementalConfig() {
    }

    private static ElementalConfig instance;

    public static ElementalConfig get() {
        if (instance == null) {
            instance = new ElementalConfig();
        }
        return instance;
    }

}
