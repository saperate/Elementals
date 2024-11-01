package dev.saperate.elementals.effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import static dev.saperate.elementals.Elementals.MODID;
import static dev.saperate.elementals.effects.DenseStatusEffect.DENSE_EFFECT;
import static dev.saperate.elementals.effects.DrowningStatusEffect.DROWNING_EFFECT;
import static dev.saperate.elementals.effects.SeismicSenseStatusEffect.SEISMIC_SENSE_EFFECT;
import static dev.saperate.elementals.effects.ShockedStatusEffect.SHOCKED_EFFECT;
import static dev.saperate.elementals.effects.SpiritProjectionStatusEffect.SPIRIT_PROJECTION_EFFECT;
import static dev.saperate.elementals.effects.StationaryStatusEffect.STATIONARY_EFFECT;
import static dev.saperate.elementals.effects.StunnedStatusEffect.STUNNED_EFFECT;

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
        STATIONARY = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(MODID, "stationary"), STATIONARY_EFFECT);
        DENSE = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(MODID, "dense"), DENSE_EFFECT);
        DROWNING = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(MODID, "drowning"), DROWNING_EFFECT);
        SEISMIC_SENSE = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(MODID, "seismic_sense"), SEISMIC_SENSE_EFFECT);
        SPIRIT_PROJECTION = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(MODID, "spirit_projection"), SPIRIT_PROJECTION_EFFECT);
        SHOCKED = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(MODID, "shocked"), SHOCKED_EFFECT);
        STUNNED = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(MODID, "stunned"), STUNNED_EFFECT);
        STATIC_AURA = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(MODID, "static_aura"), new StaticAuraStatusEffect());
        OVERCHARGED = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(MODID, "overcharged"), new OverchargedStatusEffect());
        BURNOUT = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(MODID, "burnout"), new BurnoutStatusEffect());
    }
}
