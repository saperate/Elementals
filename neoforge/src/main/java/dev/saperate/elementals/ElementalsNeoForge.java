package dev.saperate.elementals;


import dev.saperate.elementals.platform.NeoForgeRegistryHelper;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;

import static dev.saperate.elementals.Constants.MODID;

@Mod(Constants.MODID)
public class ElementalsNeoForge {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, MODID);
    public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS = DeferredRegister.create(Registries.TRIGGER_TYPE, MODID);
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(BuiltInRegistries.ARMOR_MATERIAL, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, MODID);
    public ElementalsNeoForge(IEventBus eventBus) {
        NeoForgeRegistryHelper.eventBus = eventBus;
        
        MOB_EFFECTS.register(eventBus);
        TRIGGERS.register(eventBus);
        ARMOR_MATERIALS.register(eventBus);
        ITEMS.register(eventBus);
        CREATIVE_TABS.register(eventBus);

        Elementals.init();
    }
}