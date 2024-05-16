package dev.saperate.elementals.items;

import dev.saperate.elementals.armors.materials.ElementalArmorMaterial;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;

import static dev.saperate.elementals.Elementals.MODID;

public class ElementalItems {

    public static final Set<Item> EARTH_ARMOR_SET = new HashSet<>();
    public static final EarthArmorItem EARTH_HELMET = (EarthArmorItem) registerItem("earth_helmet",
            new EarthArmorItem(ElementalArmorMaterial.EARTH, ArmorItem.Type.HELMET, new FabricItemSettings()));
    public static final EarthArmorItem EARTH_CHESTPLATE = (EarthArmorItem) registerItem("earth_chestplate",
            new EarthArmorItem(ElementalArmorMaterial.EARTH, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));
    public static final EarthArmorItem EARTH_LEGGINGS = (EarthArmorItem) registerItem("earth_leggings",
            new EarthArmorItem(ElementalArmorMaterial.EARTH, ArmorItem.Type.LEGGINGS, new FabricItemSettings()));
    public static final EarthArmorItem EARTH_BOOTS = (EarthArmorItem) registerItem("earth_boots",
            new EarthArmorItem(ElementalArmorMaterial.EARTH, ArmorItem.Type.BOOTS, new FabricItemSettings()));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(MODID,name), item);
    }

    public static void registerModItems() {
        EARTH_ARMOR_SET.add(EARTH_HELMET);
        EARTH_ARMOR_SET.add(EARTH_CHESTPLATE);
        EARTH_ARMOR_SET.add(EARTH_LEGGINGS);
        EARTH_ARMOR_SET.add(EARTH_BOOTS);


    }
}
