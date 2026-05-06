package dev.saperate.elementals.armors.materials;

import dev.saperate.elementals.Constants;
import dev.saperate.elementals.Elementals;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;


import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ElementalsArmorMaterial {
    public static final Holder<ArmorMaterial> EARTH_ARMOR = registerMaterial("earth_armor",
            Map.of(
                    ArmorItem.Type.HELMET, 5,
                    ArmorItem.Type.CHESTPLATE, 10,
                    ArmorItem.Type.LEGGINGS, 8,
                    ArmorItem.Type.BOOTS, 5
            ),
            0,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            Ingredient::of,
            6,
            0.9f,
            true
    );

    public static final Holder<ArmorMaterial> METAL_ARMOR = registerMaterial("metal_armor",
            Map.of(
                    ArmorItem.Type.HELMET, 9,
                    ArmorItem.Type.CHESTPLATE, 16,
                    ArmorItem.Type.LEGGINGS, 13,
                    ArmorItem.Type.BOOTS, 8
            ),
            0,
            SoundEvents.ARMOR_EQUIP_IRON,
            Ingredient::of,
            8,
            1,
            true
    );


    public static Holder<ArmorMaterial> registerMaterial(String id, Map<ArmorItem.Type, Integer> defensePoints, int enchantability, Holder<SoundEvent> equipSound, Supplier<Ingredient> repairIngredientSupplier, float toughness, float knockbackResistance, boolean dyeable) {
        List<ArmorMaterial.Layer> layers = List.of(
                new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(Constants.MODID, id), "", dyeable)
        );

        ArmorMaterial material = new ArmorMaterial(defensePoints, enchantability, equipSound, repairIngredientSupplier, layers, toughness, knockbackResistance);
        material = Registry.register(BuiltInRegistries.ARMOR_MATERIAL, ResourceLocation.fromNamespaceAndPath(Constants.MODID, id), material);

        return Holder.direct(material);
    }
}
