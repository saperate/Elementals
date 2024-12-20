package dev.saperate.elementals.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import static dev.saperate.elementals.Elementals.MODID;

public final class ElementalsEnchantments {
    public static final RegistryKey<Enchantment> VOLUME = of("volume");

    private static RegistryKey<Enchantment> of(String name) {
        return RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(MODID, name));
    }

    public static void init(){

    }

    private ElementalsEnchantments(){
    }
}
