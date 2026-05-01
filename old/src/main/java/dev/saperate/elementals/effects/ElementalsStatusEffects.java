package dev.saperate.elementals.effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import static dev.saperate.elementals.Elementals.MODID;

public final class ElementalsStatusEffects {
    public static RegistryEntry<StatusEffect> STATIONARY;
    public static RegistryEntry<StatusEffect> DENSE;
    public static RegistryEntry<StatusEffect> DROWNING;
    public static RegistryEntry<StatusEffect> SEISMIC_SENSE;
    public static RegistryEntry<StatusEffect> SPIRIT_PROJECTION;
    public static RegistryEntry<StatusEffect> SHOCKED;
    public static RegistryEntry<StatusEffect> STUNNED;
    public static RegistryEntry<StatusEffect> STATIC_AURA;
    public static RegistryEntry<StatusEffect> OVERCHARGED;
    public static RegistryEntry<StatusEffect> BURNOUT;
    
    public static void registerEffects(){
        STATIONARY = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(MODID, "stationary"), new StationaryStatusEffect());
        DENSE = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(MODID, "dense"), new DenseStatusEffect());
        DROWNING = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(MODID, "drowning"), new DrowningStatusEffect());
        SEISMIC_SENSE = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(MODID, "seismic_sense"), new SeismicSenseStatusEffect());
        SPIRIT_PROJECTION = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(MODID, "spirit_projection"), new SpiritProjectionStatusEffect());
        SHOCKED = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(MODID, "shocked"), new ShockedStatusEffect());
        STUNNED = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(MODID, "stunned"), new StunnedStatusEffect());
        STATIC_AURA = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(MODID, "static_aura"), new StaticAuraStatusEffect());
        OVERCHARGED = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(MODID, "overcharged"), new OverchargedStatusEffect());
        BURNOUT = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(MODID, "burnout"), new BurnoutStatusEffect());
    }
}
