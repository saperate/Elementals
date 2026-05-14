package dev.saperate.elementals;


import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;

import static dev.saperate.elementals.Constants.MODID;

@Mod(Constants.MODID)
public class ElementalsNeoForge {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, MODID);
    public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS = DeferredRegister.create(Registries.TRIGGER_TYPE, MODID);
    
    public ElementalsNeoForge(IEventBus eventBus) {
        Elementals.init();

        MOB_EFFECTS.register(eventBus);
        TRIGGERS.register(eventBus);
    }
}