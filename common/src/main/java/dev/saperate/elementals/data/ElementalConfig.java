package dev.saperate.elementals.data;

import com.google.common.io.Files;
import com.google.gson.*;
import dev.saperate.elementals.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.io.*;
import java.util.HashMap;

public final class ElementalConfig {
    //TODO sync configs when joining a server
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private boolean shouldRewriteConfig = false;
    //XP
    public float XP_MULTIPLIER = 0.1f;
    //CHI
    public int MAX_CHI = 100;
    public float CHI_REGENERATION_RATE = 0.1f;
    /// Adds the level times this multiplier to the chi regen.
    public float LEVEL_CHI_REGEN_MULTIPLIER = 0;
    //GUI
    public int CHI_OVERLAY_THRESHOLD = 115;
    public boolean CHI_OVERLAY_TEXT = false;
    public boolean HIDE_TIMER = false;
    //MISC
    public float BENDING_DAMAGE_MULTIPLIER = 1;//TODO full rework of bending damage scaling
    public HashMap<Item, Integer> METAL_COST_VALUE = new HashMap<>() {{
        put(Items.COPPER_INGOT, 9);
        put(Items.RAW_COPPER, 6);
        put(Items.IRON_INGOT, 9);
        put(Items.IRON_NUGGET, 1);
        put(Items.RAW_IRON, 6);
        put(Items.GOLD_INGOT, 9);
        put(Items.RAW_GOLD, 6);
        put(Items.GOLD_NUGGET, 1);
    }};
    /**
     * Ex: 1x Ingot -> 9x Iron Ingot
     */
    public HashMap<Item, Item> METAL_LOWER_VALUE_STACK = new HashMap<>() {{
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
        XP_MULTIPLIER = getOrDefault(root, "XP_MULTIPLIER", XP_MULTIPLIER);
        MAX_CHI = getOrDefault(root, "MAX_CHI", MAX_CHI);
        CHI_REGENERATION_RATE = getOrDefault(root, "CHI_REGENERATION_RATE", CHI_REGENERATION_RATE);
        LEVEL_CHI_REGEN_MULTIPLIER = getOrDefault(root, "LEVEL_CHI_REGEN_MULTIPLIER", LEVEL_CHI_REGEN_MULTIPLIER);
        CHI_OVERLAY_THRESHOLD = getOrDefault(root, "CHI_OVERLAY_THRESHOLD", CHI_OVERLAY_THRESHOLD);
        CHI_OVERLAY_TEXT = getOrDefault(root, "CHI_OVERLAY_TEXT", CHI_OVERLAY_TEXT);
        HIDE_TIMER = getOrDefault(root, "HIDE_TIMER", HIDE_TIMER);
        CRAFTABLE_SCROLLS = getOrDefault(root, "CRAFTABLE_SCROLLS", CRAFTABLE_SCROLLS);
        BENDING_DAMAGE_MULTIPLIER = getOrDefault(root, "BENDING_DAMAGE_MULTIPLIER", BENDING_DAMAGE_MULTIPLIER);
        METAL_COST_VALUE = getMetalCostValueOrDefault(root, METAL_COST_VALUE);
        METAL_LOWER_VALUE_STACK = getMetalLowerValueStackOrDefault(root, METAL_LOWER_VALUE_STACK);

        //Something somewhere was misplaced, we're recreating the config from what we have
        if(shouldRewriteConfig) {
            //Moves the old file somewhere else and creates a new config at the location
            System.err.println("Elemental config was malformed or outdated! Renaming your old config to \"elementals-old.json\" and replacing this one");
            try {
                if (getOldConfigFile().exists()) {
                    try {
                        getOldConfigFile().delete();
                    } catch (Exception ignored) {
                    }
                }
                Files.copy(getConfigFile(), getOldConfigFile());
                generateWriteConfig();
            } catch (IOException ex) {
                System.err.println("Could not copy old config file! Creating a new config file was aborted to save your settings. Reverted to default values \n" + ex);
                resetConfig();
            }
        }
    }

    private void generateWriteConfig() {
        try {
            File file = getConfigFile();
            FileWriter writer = new FileWriter(file);

            JsonObject root = new JsonObject();
            root.addProperty("XP_MULTIPLIER", XP_MULTIPLIER);
            root.addProperty("MAX_CHI", MAX_CHI);
            root.addProperty("CHI_REGENERATION_RATE", CHI_REGENERATION_RATE);
            root.addProperty("LEVEL_CHI_REGEN_MULTIPLIER", LEVEL_CHI_REGEN_MULTIPLIER);
            root.addProperty("CHI_OVERLAY_THRESHOLD", CHI_OVERLAY_THRESHOLD);
            root.addProperty("CHI_OVERLAY_TEXT", CHI_OVERLAY_TEXT);
            root.addProperty("HIDE_TIMER", HIDE_TIMER);
            root.addProperty("BENDING_DAMAGE_MULTIPLIER", BENDING_DAMAGE_MULTIPLIER);
            root.addProperty("CRAFTABLE_SCROLLS", CRAFTABLE_SCROLLS);
            root.add("METAL_COST_VALUE", getDefaultMetalCostValue());
            root.add("METAL_LOWER_VALUE_STACK", getDefaultMetalLowerValueStack());

            writer.append(gson.toJson(root));
            writer.close();
        } catch (Exception e) {
            System.err.println("Could not generate config!\n" + e);
        }
    }


    private JsonArray getDefaultMetalCostValue() {
        JsonArray metalCostValue = new JsonArray();
        for (Item item : METAL_COST_VALUE.keySet()) {
            JsonObject entry = new JsonObject();
            entry.addProperty("ITEM",
                    BuiltInRegistries.ITEM.getResourceKey(item).get().location().toString());
            entry.addProperty("VALUE",
                    METAL_COST_VALUE.get(item));
            metalCostValue.add(entry);
        }
        return metalCostValue;
    }

    private JsonArray getDefaultMetalLowerValueStack() {
        JsonArray metalLowerValueStack = new JsonArray();
        for (Item item : METAL_LOWER_VALUE_STACK.keySet()) {
            JsonObject entry = new JsonObject();
            entry.addProperty("ITEM_BEFORE",
                    BuiltInRegistries.ITEM.getResourceKey(item).get().location().toString());
            entry.addProperty("ITEM_AFTER",
                    BuiltInRegistries.ITEM.getResourceKey(METAL_LOWER_VALUE_STACK.get(item)).get().location().toString());
            metalLowerValueStack.add(entry);
        }
        return metalLowerValueStack;
    }

    private HashMap<Item, Integer> getMetalCostValue(JsonObject root) throws IllegalStateException {
        JsonArray array = root.get("METAL_COST_VALUE").getAsJsonArray();
        HashMap<Item, Integer> out = new HashMap<>();

        for (JsonElement rawEntry : array.asList()) {
            JsonObject entry = rawEntry.getAsJsonObject();
            out.put(
                    BuiltInRegistries.ITEM.get(ResourceLocation.bySeparator(entry.get("ITEM").getAsString(), ':')),
                    entry.get("VALUE").getAsInt());
        }

        return out;
    }


    private HashMap<Item, Item> getMetalLowerValueStack(JsonObject root) throws IllegalStateException {
        JsonArray array = root.get("METAL_LOWER_VALUE_STACK").getAsJsonArray();
        HashMap<Item, Item> out = new HashMap<>();

        for (JsonElement rawEntry : array.asList()) {
            JsonObject entry = rawEntry.getAsJsonObject();
            out.put(
                    BuiltInRegistries.ITEM.get(ResourceLocation.bySeparator(entry.get("ITEM_BEFORE").getAsString(), ':')),
                    BuiltInRegistries.ITEM.get(ResourceLocation.bySeparator(entry.get("ITEM_AFTER").getAsString(), ':')));
        }

        return out;
    }

    /**
     * Gets the value from the passed JsonObject. If it doesn't exist, it will default to the value passed
     * @param root the root from which we take the value from
     * @param name the name of the object we want
     * @param defaultValue the default value if the specified object does not exist 
     * @return The value of name within root or defaultValue
     */
    private int getOrDefault(JsonObject root, String name, int defaultValue) {
        if (root.has(name)) {
            try {
                return root.get(name).getAsInt();
            } catch (Exception ignored) {
            } //By ignoring it, it goes to the default return
        }
        shouldRewriteConfig = true;
        return defaultValue;
    }

    /**
     * Gets the value from the passed JsonObject. If it doesn't exist, it will default to the value passed
     * @param root the root from which we take the value from
     * @param name the name of the object we want
     * @param defaultValue the default value if the specified object does not exist 
     * @return The value of name within root or defaultValue
     */
    private float getOrDefault(JsonObject root, String name, float defaultValue) {
        if (root.has(name)) {
            try {
                return root.get(name).getAsFloat();
            } catch (Exception ignored) {
            } //By ignoring it, it goes to the default return
        }
        shouldRewriteConfig = true;
        return defaultValue;
    }

    /**
     * Gets the value from the passed JsonObject. If it doesn't exist, it will default to the value passed
     * @param root the root from which we take the value from
     * @param name the name of the object we want
     * @param defaultValue the default value if the specified object does not exist 
     * @return The value of name within root or defaultValue
     */
    private boolean getOrDefault(JsonObject root, String name, boolean defaultValue) {
        if (root.has(name)) {
            try {
                return root.get(name).getAsBoolean();
            } catch (Exception ignored) {
            } //By ignoring it, it goes to the default return
        }
        shouldRewriteConfig = true;
        return defaultValue;
    }

    /**
     * Gets the value from the passed JsonObject. If it doesn't exist, it will default to the value passed
     * @param root the root from which we take the value from
     * @param name the name of the object we want
     * @param defaultValue the default value if the specified object does not exist 
     * @return The value of name within root or defaultValue
     */
    private JsonArray getOrDefault(JsonObject root, String name, JsonArray defaultValue) {
        if (root.has(name)) {
            try {
                return root.get(name).getAsJsonArray();
            } catch (Exception ignored) {
            } //By ignoring it, it goes to the default return
        }
        shouldRewriteConfig = true;
        return defaultValue;
    }

    /**
     * Gets the value from the passed JsonObject. If it doesn't exist, it will default to the value passed
     * @param root the root from which we take the value from
     * @param defaultValue the default value if the specified object does not exist 
     * @return The value of name within root or defaultValue
     */
    private HashMap<Item, Integer> getMetalCostValueOrDefault(JsonObject root, HashMap<Item, Integer> defaultValue) {
        try {
            return getMetalCostValue(root);
        } catch (Exception ignored) {
        } //By ignoring it, it goes to the default return
        shouldRewriteConfig = true;
        return defaultValue;
    }

    /**
     * Gets the value from the passed JsonObject. If it doesn't exist, it will default to the value passed
     * @param root the root from which we take the value from
     * @param defaultValue the default value if the specified object does not exist 
     * @return The value of name within root or defaultValue
     */
    private HashMap<Item, Item> getMetalLowerValueStackOrDefault(JsonObject root, HashMap<Item, Item> defaultValue) {
        try {
            return getMetalLowerValueStack(root);
        } catch (Exception ignored) {
        } //By ignoring it, it goes to the default return
        shouldRewriteConfig = true;
        return defaultValue;
    }

    private File getConfigFile() {
        return new File(Services.PLATFORM.getConfigDir(), "elementals.json");
    }

    private File getOldConfigFile() {
        return new File(Services.PLATFORM.getConfigDir(), "elementals-old.json");
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
