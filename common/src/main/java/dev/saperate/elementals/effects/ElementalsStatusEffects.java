package dev.saperate.elementals.effects;


import dev.saperate.elementals.Constants;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

public final class ElementalsStatusEffects {
    public static Holder<MobEffect> STATIONARY = registerEffect("stationary", new StationaryStatusEffect());
    public static Holder<MobEffect> DENSE = registerEffect("dense", new DenseStatusEffect());
    public static Holder<MobEffect> DROWNING = registerEffect("drowning", new DrowningStatusEffect());
    public static Holder<MobEffect> SEISMIC_SENSE = registerEffect("seismic_sense", new SeismicSenseStatusEffect());
    public static Holder<MobEffect> SPIRIT_PROJECTION = registerEffect("spirit_projection", new SpiritProjectionStatusEffect());
    public static Holder<MobEffect> SHOCKED = registerEffect("shocked", new ShockedStatusEffect());
    public static Holder<MobEffect> STUNNED = registerEffect("stunned", new StunnedStatusEffect());
    public static Holder<MobEffect> STATIC_AURA = registerEffect("static_aura", new StaticAuraStatusEffect());
    public static Holder<MobEffect> OVERCHARGED = registerEffect("overcharged", new OverchargedStatusEffect());
    public static Holder<MobEffect> BURNOUT = registerEffect("burnout", new BurnoutStatusEffect());
    
    public static void registerEffects(){}
    
    public static Holder<MobEffect> registerEffect(String name, MobEffect effect){
        return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, 
                ResourceLocation.fromNamespaceAndPath(Constants.MODID, name), 
                effect);
    }
}
