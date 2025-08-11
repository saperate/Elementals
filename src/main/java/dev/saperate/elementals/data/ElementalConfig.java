package dev.saperate.elementals.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Collections;

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
        }catch (Exception e){
            // This doesn't override the file, just makes it so we don't use what we loaded
            System.err.println("Elemental config was malformed! Reverting back to default values\n" + e);
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

            writer.append(gson.toJson(root));
            writer.close();
        }catch (Exception e){
            System.err.println("Could not generate config!\n" + e);
        }
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
