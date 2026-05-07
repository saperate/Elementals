package dev.saperate.elementals.enchantments;


import dev.saperate.elementals.Constants;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

public final class ElementalsEnchantments {
    public static final ResourceKey<Enchantment> VOLUME = registerEnchantment("volume");

    private static ResourceKey<Enchantment> registerEnchantment(String name) {
        return ResourceKey.create(Registries.ENCHANTMENT, 
                ResourceLocation.fromNamespaceAndPath(Constants.MODID, name));
    }

    public static void registerEnchantments(){}
}
