package dev.saperate.elementals.effects;


import dev.saperate.elementals.platform.Services;
import dev.saperate.elementals.platform.services.IRegistryHelper.MobEffectHolder;

public final class ElementalsStatusEffects {
    public static MobEffectHolder STATIONARY = Services.REGISTRY.registerEffect("stationary", new StationaryStatusEffect());
    public static MobEffectHolder DENSE = Services.REGISTRY.registerEffect("dense", new DenseStatusEffect());
    public static MobEffectHolder DROWNING = Services.REGISTRY.registerEffect("drowning", new DrowningStatusEffect());
    public static MobEffectHolder SEISMIC_SENSE = Services.REGISTRY.registerEffect("seismic_sense", new SeismicSenseStatusEffect());
    public static MobEffectHolder SPIRIT_PROJECTION = Services.REGISTRY.registerEffect("spirit_projection", new SpiritProjectionStatusEffect());
    public static MobEffectHolder SHOCKED = Services.REGISTRY.registerEffect("shocked", new ShockedStatusEffect());
    public static MobEffectHolder STUNNED = Services.REGISTRY.registerEffect("stunned", new StunnedStatusEffect());
    public static MobEffectHolder STATIC_AURA = Services.REGISTRY.registerEffect("static_aura", new StaticAuraStatusEffect());
    public static MobEffectHolder OVERCHARGED = Services.REGISTRY.registerEffect("overcharged", new OverchargedStatusEffect());
    public static MobEffectHolder BURNOUT = Services.REGISTRY.registerEffect("burnout", new BurnoutStatusEffect());
    
    public static void register(){}
}
