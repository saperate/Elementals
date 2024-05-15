package dev.saperate.elementals.items;

import dev.saperate.elementals.armors.materials.ElementalArmorMaterial;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static dev.saperate.elementals.Elementals.MODID;

public class ElementalItems {

    public static final Item EARTH_HELMET = registerItem("earth_helmet",
            new ArmorItem(ElementalArmorMaterial.EARTH, ArmorItem.Type.HELMET, new FabricItemSettings()));
    public static final Item EARTH_CHESTPLATE = registerItem("earth_chestplate",
            new ArmorItem(ElementalArmorMaterial.EARTH, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));
    public static final Item EARTH_LEGGINGS = registerItem("earth_leggings",
            new ArmorItem(ElementalArmorMaterial.EARTH, ArmorItem.Type.LEGGINGS, new FabricItemSettings()));
    public static final Item EARTH_BOOTS = registerItem("earth_boots",
            new ArmorItem(ElementalArmorMaterial.EARTH, ArmorItem.Type.BOOTS, new FabricItemSettings()));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(MODID,name), item);
    }

    public static void registerModItems() {

    }
}
