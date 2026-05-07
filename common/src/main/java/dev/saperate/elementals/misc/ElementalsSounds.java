package dev.saperate.elementals.misc;

import dev.saperate.elementals.Constants;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class ElementalsSounds {

    private static final ResourceLocation WIND_SOUND_ID = ResourceLocation.fromNamespaceAndPath(
            Constants.MODID, "wind");
    private static final ResourceLocation WIND_BURST_SOUND_ID = ResourceLocation.fromNamespaceAndPath(
            Constants.MODID, "wind_burst");
    private static final ResourceLocation METAL_BREAK_SOUND_ID = ResourceLocation.fromNamespaceAndPath(
            Constants.MODID, "metal_break");
    public static SoundEvent WIND_SOUND_EVENT = SoundEvent.createVariableRangeEvent(WIND_SOUND_ID);
    public static SoundEvent WIND_BURST_SOUND_EVENT = SoundEvent.createVariableRangeEvent(WIND_BURST_SOUND_ID);
    public static SoundEvent METAL_BREAK_SOUND_EVENT = SoundEvent.createVariableRangeEvent(METAL_BREAK_SOUND_ID);
    
    public static void register(){
        Registry.register(BuiltInRegistries.SOUND_EVENT, WIND_SOUND_ID, WIND_SOUND_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, WIND_BURST_SOUND_ID, WIND_BURST_SOUND_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, METAL_BREAK_SOUND_ID, METAL_BREAK_SOUND_EVENT);
    }
}
