package dev.saperate.elementals.items;

import dev.saperate.elementals.armors.materials.ElementalArmorMaterial;
import dev.saperate.elementals.items.scrolls.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
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

    public static final ScrollItem SCROLL_ITEM = (ScrollItem) registerItem("scroll",
            new ScrollItem(new FabricItemSettings()
                    .maxCount(1)));

    public static final FireScrollItem FIRE_SCROLL_ITEM = (FireScrollItem) registerItem("fire_scroll",
            new FireScrollItem(new FabricItemSettings()
                    .maxCount(1)));

    public static final WaterScrollItem WATER_SCROLL_ITEM = (WaterScrollItem) registerItem("water_scroll",
            new WaterScrollItem(new FabricItemSettings()
                    .maxCount(1)));

    public static final EarthScrollItem EARTH_SCROLL_ITEM = (EarthScrollItem) registerItem("earth_scroll",
            new EarthScrollItem(new FabricItemSettings()
                    .maxCount(1)));

    public static final AirScrollItem AIR_SCROLL_ITEM = (AirScrollItem) registerItem("air_scroll",
            new AirScrollItem(new FabricItemSettings()
                    .maxCount(1)));

    public static final LightningScrollItem LIGHTNING_SCROLL_ITEM = (LightningScrollItem) registerItem("lightning_scroll",
            new LightningScrollItem(new FabricItemSettings()
                    .maxCount(1)));
    public static final LightningBottleItem LIGHTNING_BOTTLE_ITEM = (LightningBottleItem) registerItem("lightning_bottle",
            new LightningBottleItem(new FabricItemSettings()
                    .maxCount(1)
                    .food(LIGHTNING_BOTTLE_FOOD_COMPONENT)
            ));
    public static final DirtBottleItem DIRT_BOTTLE_ITEM = (DirtBottleItem) registerItem("dirt_bottle",
            new DirtBottleItem(new FabricItemSettings()
                    .maxCount(1)));
    public static final BoomerangItem BOOMERANG_ITEM = (BoomerangItem) registerItem("boomerang",
            new BoomerangItem(new FabricItemSettings()
                    .maxCount(1)
            ));
    public static final WaterPouchItem WATER_POUCH_ITEM = (WaterPouchItem) registerItem("water_pouch",
            new WaterPouchItem(new FabricItemSettings()
                    .maxCount(1)));

    public static  final ItemGroup ELEMENTALS_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(SCROLL_ITEM))
            .displayName(Text.of("Elementals"))
            .entries((context,entries) -> {
                entries.add(SCROLL_ITEM);
                entries.add(FIRE_SCROLL_ITEM);
                entries.add(WATER_SCROLL_ITEM);
                entries.add(EARTH_SCROLL_ITEM);
                entries.add(AIR_SCROLL_ITEM);
                entries.add(LIGHTNING_SCROLL_ITEM);
                entries.add(DIRT_BOTTLE_ITEM);
                entries.add(LIGHTNING_BOTTLE_ITEM);
                entries.add(BOOMERANG_ITEM);
                entries.add(WATER_POUCH_ITEM);
            }).build();



    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(MODID,name), item);
    }

    public static void register() {
        EARTH_ARMOR_SET.add(EARTH_HELMET);
        EARTH_ARMOR_SET.add(EARTH_CHESTPLATE);
        EARTH_ARMOR_SET.add(EARTH_LEGGINGS);
        EARTH_ARMOR_SET.add(EARTH_BOOTS);
        Registry.register(Registries.ITEM_GROUP, Identifier.of("tutorial", "test_group"), ELEMENTALS_GROUP);
    }
}
