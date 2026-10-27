package dev.saperate.elementals.items;

import com.mojang.datafixers.util.Pair;
import dev.saperate.elementals.Constants;
import dev.saperate.elementals.armors.materials.ElementalsArmorMaterial;
import dev.saperate.elementals.blocks.ElementalsBlocks;
import dev.saperate.elementals.items.foods.ElementalsFoods;
import dev.saperate.elementals.items.foods.PieBlockItem;
import dev.saperate.elementals.items.scrolls.*;
import dev.saperate.elementals.platform.Services;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BannerPattern;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class ElementalsItems {

    public static final TagKey<BannerPattern> AIR_BANNER_PATTERN_TAG = TagKey.create(Registries.BANNER_PATTERN, ResourceLocation.fromNamespaceAndPath(Constants.MODID,"air_banner_pattern"));

    private static final Set<Item> EARTH_ARMOR_SET = new HashSet<>();
    public static final Supplier<EarthArmorItem> EARTH_HELMET = registerItem("earth_helmet",
            () -> new EarthArmorItem(ElementalsArmorMaterial.EARTH_ARMOR, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final Supplier<EarthArmorItem> EARTH_CHESTPLATE = registerItem("earth_chestplate",
            () -> new EarthArmorItem(ElementalsArmorMaterial.EARTH_ARMOR, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final Supplier<EarthArmorItem> EARTH_LEGGINGS = registerItem("earth_leggings",
            () -> new EarthArmorItem(ElementalsArmorMaterial.EARTH_ARMOR, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final Supplier<EarthArmorItem> EARTH_BOOTS = registerItem("earth_boots",
            () -> new EarthArmorItem(ElementalsArmorMaterial.EARTH_ARMOR, ArmorItem.Type.BOOTS, new Item.Properties()));

    private static final Set<Item> METAL_ARMOR_SET = new HashSet<>();
    public static final Supplier<MetalArmorItem> METAL_HELMET = registerItem("metal_helmet",
            () -> new MetalArmorItem(ElementalsArmorMaterial.METAL_ARMOR, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final Supplier<MetalArmorItem> METAL_CHESTPLATE = registerItem("metal_chestplate",
            () -> new MetalArmorItem(ElementalsArmorMaterial.METAL_ARMOR, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final Supplier<MetalArmorItem> METAL_LEGGINGS = registerItem("metal_leggings",
            () -> new MetalArmorItem(ElementalsArmorMaterial.METAL_ARMOR, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final Supplier<MetalArmorItem> METAL_BOOTS = registerItem("metal_boots",
            () -> new MetalArmorItem(ElementalsArmorMaterial.METAL_ARMOR, ArmorItem.Type.BOOTS, new Item.Properties()));


    public static final Supplier<ScrollItem> SCROLL_ITEM = registerItem("scroll",
            () -> new ScrollItem(new Item.Properties()
                    .stacksTo(1)));

    public static final Supplier<FireScrollItem> FIRE_SCROLL_ITEM = registerItem("fire_scroll",
            () -> new FireScrollItem(new Item.Properties()
                    .stacksTo(1)));

    public static final Supplier<WaterScrollItem> WATER_SCROLL_ITEM = registerItem("water_scroll",
            () -> new WaterScrollItem(new Item.Properties()
                    .stacksTo(1)));

    public static final Supplier<EarthScrollItem> EARTH_SCROLL_ITEM = registerItem("earth_scroll",
            () -> new EarthScrollItem(new Item.Properties()
                    .stacksTo(1)));

    public static final Supplier<AirScrollItem> AIR_SCROLL_ITEM = registerItem("air_scroll",
            () -> new AirScrollItem(new Item.Properties()
                    .stacksTo(1)));

    public static final Supplier<LightningScrollItem> LIGHTNING_SCROLL_ITEM = registerItem("lightning_scroll",
            () -> new LightningScrollItem(new Item.Properties()
                    .stacksTo(1)));
    public static final Supplier<LightningBottleItem> LIGHTNING_BOTTLE_ITEM = registerItem("lightning_bottle",
            () -> new LightningBottleItem(new Item.Properties()
                    .stacksTo(1)
                    .food(ElementalsFoods.LIGHTNING_BOTTLE_FOOD_COMPONENT)
            ));
    public static final Supplier<BloodScrollItem> BLOOD_SCROLL_ITEM = registerItem("blood_scroll",
            () -> new BloodScrollItem(new Item.Properties()
                    .stacksTo(1)));
    public static final Supplier<MetalScrollItem> METAL_SCROLL_ITEM = registerItem("metal_scroll",
            () -> new MetalScrollItem(new Item.Properties()
                    .stacksTo(1)));
    public static final Supplier<DirtBottleItem> DIRT_BOTTLE_ITEM = registerItem("dirt_bottle",
            () -> new DirtBottleItem(new Item.Properties()
                    .stacksTo(1)));
    public static final Supplier<BoomerangItem> BOOMERANG_ITEM = registerItem("boomerang",
            () -> new BoomerangItem(new Item.Properties()
                    .stacksTo(1)
            ));
    public static final Supplier<WaterPouchItem> WATER_POUCH_ITEM = registerItem("water_pouch",
            () -> new WaterPouchItem(new Item.Properties()
                    .stacksTo(1)
            ));

    public static final Supplier<GliderItem> GLIDER_ITEM = registerItem("glider",
            () -> new GliderItem(new Item.Properties()
                    .stacksTo(1)));
    public static final Supplier<BannerPatternItem> AIR_BANNER_PATTERN_ITEM = registerItem( 
            "air_banner_pattern",
            () -> new BannerPatternItem(AIR_BANNER_PATTERN_TAG,new Item.Properties()
                    .stacksTo(1)));
    
    //BLOCK ITEMS
    public static final Supplier<BlockItem> MOONPEACH_LEAVES_ITEM = registerItem(
            "moonpeach_leaves",
            () -> new BlockItem(ElementalsBlocks.MOON_PEACH_LEAVES.get(), new Item.Properties()));
    public static final Supplier<BlockItem> MOON_PEACH_LOG_ITEM = registerItem(
            "moonpeach_log",
            () -> new BlockItem(ElementalsBlocks.MOON_PEACH_LOG.get(), new Item.Properties()));
    public static final Supplier<BlockItem> MOON_PEACH_STRIPPED_LOG_ITEM = registerItem(
            "moonpeach_stripped_log",
            () -> new BlockItem(ElementalsBlocks.MOON_PEACH_STRIPPED_LOG.get(), new Item.Properties()));
    public static final Supplier<BlockItem> MOON_PEACH_PLANKS_ITEM = registerItem(
            "moonpeach_planks",
            () -> new BlockItem(ElementalsBlocks.MOON_PEACH_PLANKS.get(), new Item.Properties()));

    public static final Supplier<BlockItem> GRILL_ITEM = registerItem(
            "grill",
            () -> new BlockItem(ElementalsBlocks.GRILL_BLOCK.get(), new Item.Properties()));
    public static final Supplier<KettleBlockItem> KETTLE_ITEM = registerItem(
            "kettle",
            () -> new KettleBlockItem(new Item.Properties().stacksTo(1)));
    
    // FOODS 
    /// Relates the pie type (string) and cooked status (boolean) to its corresponding blockItem
    public static final HashMap<Pair<String, Boolean>, Supplier<PieBlockItem>> PIE_ITEMS = registerPieItems();
    
    public static final CreativeModeTab ELEMENTALS_TAB = Services.REGISTRY.createItemTab();

    private static <T extends Item> Supplier<T> registerItem(String name, Supplier<T> item) {
        return Services.REGISTRY.registerItem(name, item);
    }

    public static void register() {
        Services.REGISTRY.registerDispenserBehavior(BOOMERANG_ITEM, BOOMERANG_ITEM);
        Services.REGISTRY.registerDispenserBehavior(DIRT_BOTTLE_ITEM, DIRT_BOTTLE_ITEM);
        Services.REGISTRY.registerCreativeTab("elementals_tab", ELEMENTALS_TAB);
    }
    
    public static HashMap<Pair<String, Boolean>, Supplier<PieBlockItem>> registerPieItems(){
        HashMap<Pair<String, Boolean>, Supplier<PieBlockItem>> map = new HashMap<>(8);
        HashSet<String> types = ElementalsFoods.getPieTypes();
        
        for (String type : types){
            map.put(new Pair<>(type, true), registerItem("cooked_" + type + "_pie", 
                    () -> new PieBlockItem(type, true)));
            map.put(new Pair<>(type, false), registerItem("uncooked_" + type + "_pie", 
                    () -> new PieBlockItem(type, false)));
        }
        
        return map;
    }
    
    public static Set<Item> getEarthArmorSet(){
        if(EARTH_ARMOR_SET.isEmpty()){
            EARTH_ARMOR_SET.add(EARTH_HELMET.get());
            EARTH_ARMOR_SET.add(EARTH_CHESTPLATE.get());
            EARTH_ARMOR_SET.add(EARTH_LEGGINGS.get());
            EARTH_ARMOR_SET.add(EARTH_BOOTS.get());
        }
        return EARTH_ARMOR_SET;
    }

    public static Set<Item> getMetalArmorSet(){
        if(METAL_ARMOR_SET.isEmpty()){
            METAL_ARMOR_SET.add(METAL_HELMET.get());
            METAL_ARMOR_SET.add(METAL_CHESTPLATE.get());
            METAL_ARMOR_SET.add(METAL_LEGGINGS.get());
            METAL_ARMOR_SET.add(METAL_BOOTS.get());
        }
        return METAL_ARMOR_SET;
    }
}
