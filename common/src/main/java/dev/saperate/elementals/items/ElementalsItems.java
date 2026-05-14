package dev.saperate.elementals.items;

import dev.saperate.elementals.Constants;
import dev.saperate.elementals.armors.materials.ElementalsArmorMaterial;
import dev.saperate.elementals.blocks.ElementalsBlocks;
import dev.saperate.elementals.effects.ElementalsStatusEffects;
import dev.saperate.elementals.items.scrolls.*;
import dev.saperate.elementals.platform.Services;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BannerPattern;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class ElementalsItems {

    public static final TagKey<BannerPattern> AIR_BANNER_PATTERN_TAG = TagKey.create(Registries.BANNER_PATTERN, ResourceLocation.fromNamespaceAndPath(Constants.MODID,"air_banner_pattern"));
    
    public static final FoodProperties LIGHTNING_BOTTLE_FOOD_COMPONENT = new FoodProperties.Builder()
            .alwaysEdible()
            .saturationModifier(-1.2f)
            .nutrition(-6)
            .build();

    public static final ScrollItem SCROLL_ITEM = (ScrollItem) registerItem("scroll",
            () -> new ScrollItem(new Item.Properties()
                    .stacksTo(1)));
    public static final Set<Item> EARTH_ARMOR_SET = new HashSet<>();
    public static final EarthArmorItem EARTH_HELMET = (EarthArmorItem) registerItem("earth_helmet",
            new EarthArmorItem(ElementalsArmorMaterial.EARTH_ARMOR, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final EarthArmorItem EARTH_CHESTPLATE = (EarthArmorItem) registerItem("earth_chestplate",
            new EarthArmorItem(ElementalsArmorMaterial.EARTH_ARMOR, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final EarthArmorItem EARTH_LEGGINGS = (EarthArmorItem) registerItem("earth_leggings",
            new EarthArmorItem(ElementalsArmorMaterial.EARTH_ARMOR, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final EarthArmorItem EARTH_BOOTS = (EarthArmorItem) registerItem("earth_boots",
            new EarthArmorItem(ElementalsArmorMaterial.EARTH_ARMOR, ArmorItem.Type.BOOTS, new Item.Properties()));

    public static final Set<Item> METAL_ARMOR_SET = new HashSet<>();
    public static final MetalArmorItem METAL_HELMET = (MetalArmorItem) registerItem("metal_helmet",
            new MetalArmorItem(ElementalsArmorMaterial.METAL_ARMOR, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final MetalArmorItem METAL_CHESTPLATE = (MetalArmorItem) registerItem("metal_chestplate",
            new MetalArmorItem(ElementalsArmorMaterial.METAL_ARMOR, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final MetalArmorItem METAL_LEGGINGS = (MetalArmorItem) registerItem("metal_leggings",
            new MetalArmorItem(ElementalsArmorMaterial.METAL_ARMOR, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final MetalArmorItem METAL_BOOTS = (MetalArmorItem) registerItem("metal_boots",
            new MetalArmorItem(ElementalsArmorMaterial.METAL_ARMOR, ArmorItem.Type.BOOTS, new Item.Properties()));



    public static final FireScrollItem FIRE_SCROLL_ITEM = (FireScrollItem) registerItem("fire_scroll",
            new FireScrollItem(new Item.Properties()
                    .stacksTo(1)));

    public static final WaterScrollItem WATER_SCROLL_ITEM = (WaterScrollItem) registerItem("water_scroll",
            new WaterScrollItem(new Item.Properties()
                    .stacksTo(1)));

    public static final EarthScrollItem EARTH_SCROLL_ITEM = (EarthScrollItem) registerItem("earth_scroll",
            new EarthScrollItem(new Item.Properties()
                    .stacksTo(1)));

    public static final AirScrollItem AIR_SCROLL_ITEM = (AirScrollItem) registerItem("air_scroll",
            new AirScrollItem(new Item.Properties()
                    .stacksTo(1)));

    public static final LightningScrollItem LIGHTNING_SCROLL_ITEM = (LightningScrollItem) registerItem("lightning_scroll",
            new LightningScrollItem(new Item.Properties()
                    .stacksTo(1)));
    public static final LightningBottleItem LIGHTNING_BOTTLE_ITEM = (LightningBottleItem) registerItem("lightning_bottle",
            new LightningBottleItem(new Item.Properties()
                    .stacksTo(1)
                    .food(LIGHTNING_BOTTLE_FOOD_COMPONENT)
            ));
    public static final BloodScrollItem BLOOD_SCROLL_ITEM = (BloodScrollItem) registerItem("blood_scroll",
            new BloodScrollItem(new Item.Properties()
                    .stacksTo(1)));
    public static final MetalScrollItem METAL_SCROLL_ITEM = (MetalScrollItem) registerItem("metal_scroll",
            new MetalScrollItem(new Item.Properties()
                    .stacksTo(1)));
    public static final DirtBottleItem DIRT_BOTTLE_ITEM = (DirtBottleItem) registerItem("dirt_bottle",
            new DirtBottleItem(new Item.Properties()
                    .stacksTo(1)));
    public static final BoomerangItem BOOMERANG_ITEM = (BoomerangItem) registerItem("boomerang",
            new BoomerangItem(new Item.Properties()
                    .stacksTo(1)
            ));
    public static final WaterPouchItem WATER_POUCH_ITEM = (WaterPouchItem) registerItem("water_pouch",
            new WaterPouchItem(new Item.Properties()
                    .stacksTo(1)
            ));

    public static final GliderItem GLIDER_ITEM = (GliderItem) registerItem("glider",
            new GliderItem(new Item.Properties()
                    .stacksTo(1)));
    public static final BannerPatternItem AIR_BANNER_PATTERN_ITEM = Registry.register(BuiltInRegistries.ITEM, 
            ResourceLocation.fromNamespaceAndPath(Constants.MODID,"air_banner_pattern"), 
            new BannerPatternItem(AIR_BANNER_PATTERN_TAG,new Item.Properties()
                    .stacksTo(1)));
    
    //BLOCK ITEMS
    public static final BlockItem MOON_PEACH_LEAVES_ITEM = Registry.register(BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(Constants.MODID, "moon_peach_leaves"), 
            new BlockItem(ElementalsBlocks.MOON_PEACH_LEAVES, new Item.Properties()));
    public static final BlockItem MOON_LOG = Registry.register(BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(Constants.MODID, "moon_log"),
            new BlockItem(ElementalsBlocks.MOON_LOG, new Item.Properties()));
    public static final BlockItem MOON_STRIPPED_LOG = Registry.register(BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(Constants.MODID, "moon_stripped_log"),
            new BlockItem(ElementalsBlocks.MOON_STRIPPED_LOG, new Item.Properties()));
    public static final BlockItem MOON_PLANKS = Registry.register(BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(Constants.MODID, "moon_planks"),
            new BlockItem(ElementalsBlocks.MOON_PLANKS, new Item.Properties()));
    public static final CreativeModeTab ELEMENTALS_TAB = Services.REGISTRY.createItemTab();

    private static Supplier<Item> registerItem(String name, Supplier<Item> item) {
        Services.REGISTRY.registerItem(name, item);
        return null;
    }
    
    private static Item registerItem(String name, Item item) {
        return null;
    }

    public static void register() {
        EARTH_ARMOR_SET.add(EARTH_HELMET);
        EARTH_ARMOR_SET.add(EARTH_CHESTPLATE);
        EARTH_ARMOR_SET.add(EARTH_LEGGINGS);
        EARTH_ARMOR_SET.add(EARTH_BOOTS);
        
        METAL_ARMOR_SET.add(METAL_HELMET);
        METAL_ARMOR_SET.add(METAL_CHESTPLATE);
        METAL_ARMOR_SET.add(METAL_LEGGINGS);
        METAL_ARMOR_SET.add(METAL_BOOTS);

        DispenserBlock.registerBehavior(BOOMERANG_ITEM,BOOMERANG_ITEM);
        DispenserBlock.registerBehavior(DIRT_BOTTLE_ITEM,DIRT_BOTTLE_ITEM);
        
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ResourceLocation.fromNamespaceAndPath(Constants.MODID,"elementals_tab") , ELEMENTALS_TAB);
    }
}
