package dev.saperate.elementals.items;

import dev.saperate.elementals.armors.materials.ElementalArmorMaterial;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;

import static dev.saperate.elementals.Elementals.MODID;
import static dev.saperate.elementals.effects.OverchargedStatusEffect.OVERCHARGED_EFFECT;

public class ElementalItems {

    public static final FoodComponent LIGHTNING_BOTTLE_FOOD_COMPONENT = new FoodComponent.Builder()
            .alwaysEdible()
            .saturationModifier(-1.2f)
            .hunger(-6)
            .statusEffect(new StatusEffectInstance(OVERCHARGED_EFFECT,400,0,false,false,true), 1)
            .build();


    public static final Set<Item> EARTH_ARMOR_SET = new HashSet<>();
    public static final EarthArmorItem EARTH_HELMET = (EarthArmorItem) registerItem("earth_helmet",
            new EarthArmorItem(ElementalArmorMaterial.EARTH, ArmorItem.Type.HELMET, new FabricItemSettings()));
    public static final EarthArmorItem EARTH_CHESTPLATE = (EarthArmorItem) registerItem("earth_chestplate",
            new EarthArmorItem(ElementalArmorMaterial.EARTH, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));
    public static final EarthArmorItem EARTH_LEGGINGS = (EarthArmorItem) registerItem("earth_leggings",
            new EarthArmorItem(ElementalArmorMaterial.EARTH, ArmorItem.Type.LEGGINGS, new FabricItemSettings()));
    public static final EarthArmorItem EARTH_BOOTS = (EarthArmorItem) registerItem("earth_boots",
            new EarthArmorItem(ElementalArmorMaterial.EARTH, ArmorItem.Type.BOOTS, new FabricItemSettings()));

    public static final FireScrollItem FIRE_SCROLL_ITEM = (FireScrollItem) registerItem("fire_scroll",
            new FireScrollItem(new FabricItemSettings()
                    .maxCount(1)));
    public static final LightningBottleItem LIGHTNING_BOTTLE_ITEM = (LightningBottleItem) registerItem("lightning_bottle",
            new LightningBottleItem(new FabricItemSettings()
                    .maxCount(1)
                    .food(LIGHTNING_BOTTLE_FOOD_COMPONENT)
            ));

    public static final DirtBottleItem DIRT_BOTTLE_ITEM = (DirtBottleItem) registerItem("dirt_bottle",
            new DirtBottleItem(new FabricItemSettings()
                    .maxCount(1)));



    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(MODID,name), item);
    }

    public static void register() {
        EARTH_ARMOR_SET.add(EARTH_HELMET);
        EARTH_ARMOR_SET.add(EARTH_CHESTPLATE);
        EARTH_ARMOR_SET.add(EARTH_LEGGINGS);
        EARTH_ARMOR_SET.add(EARTH_BOOTS);
    }
}
