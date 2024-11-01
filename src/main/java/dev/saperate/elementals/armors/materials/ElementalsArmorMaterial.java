package dev.saperate.elementals.armors.materials;

import dev.saperate.elementals.Elementals;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ElementalsArmorMaterial {
    public static final RegistryEntry<ArmorMaterial> EARTH_ARMOR = registerMaterial("earth_armor",
            // Defense (protection) point values for each armor piece.
            Map.of(
                    ArmorItem.Type.HELMET, 5,
                    ArmorItem.Type.CHESTPLATE, 10,
                    ArmorItem.Type.LEGGINGS, 8,
                    ArmorItem.Type.BOOTS, 5
            ),
            0,
            SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,
            Ingredient::ofItems,
            4,
            25,
            true);


    public static RegistryEntry<ArmorMaterial> registerMaterial(String id, Map<ArmorItem.Type, Integer> defensePoints, int enchantability, RegistryEntry<SoundEvent> equipSound, Supplier<Ingredient> repairIngredientSupplier, float toughness, float knockbackResistance, boolean dyeable) {
        List<ArmorMaterial.Layer> layers = List.of(
                new ArmorMaterial.Layer(Identifier.of(Elementals.MODID, id), "", dyeable)
        );

        ArmorMaterial material = new ArmorMaterial(defensePoints, enchantability, equipSound, repairIngredientSupplier, layers, toughness, knockbackResistance);
        material = Registry.register(Registries.ARMOR_MATERIAL, Identifier.of(Elementals.MODID, id), material);

        return RegistryEntry.of(material);
    }
}
