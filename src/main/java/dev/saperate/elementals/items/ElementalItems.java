package dev.saperate.elementals.items;

import dev.saperate.elementals.armors.materials.ElementalsArmorMaterial;
import dev.saperate.elementals.effects.ElementalsStatusEffects;
import dev.saperate.elementals.armors.materials.ElementalArmorMaterial;
import dev.saperate.elementals.blocks.ElementalsBlocks;
import dev.saperate.elementals.items.glider.GliderItem;
import dev.saperate.elementals.items.scrolls.*;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;

import static dev.saperate.elementals.Elementals.MODID;

public class ElementalItems {

    public static final TagKey<BannerPattern> AIR_BANNER_PATTERN_TAG = TagKey.of(RegistryKeys.BANNER_PATTERN,Identifier.of("minecraft","pattern_item/air"));
    
    public static final FoodComponent LIGHTNING_BOTTLE_FOOD_COMPONENT = new FoodComponent.Builder()
            .alwaysEdible()
            .saturationModifier(-1.2f)
            .nutrition(-6)
            .statusEffect(new StatusEffectInstance(ElementalsStatusEffects.OVERCHARGED,400,0,false,false,true), 1)
            .build();


    public static final Set<Item> EARTH_ARMOR_SET = new HashSet<>();
    public static final EarthArmorItem EARTH_HELMET = (EarthArmorItem) registerItem("earth_helmet",
            new EarthArmorItem(ElementalsArmorMaterial.EARTH_ARMOR, ArmorItem.Type.HELMET, new Item.Settings()));
    public static final EarthArmorItem EARTH_CHESTPLATE = (EarthArmorItem) registerItem("earth_chestplate",
            new EarthArmorItem(ElementalsArmorMaterial.EARTH_ARMOR, ArmorItem.Type.CHESTPLATE, new Item.Settings()));
    public static final EarthArmorItem EARTH_LEGGINGS = (EarthArmorItem) registerItem("earth_leggings",
            new EarthArmorItem(ElementalsArmorMaterial.EARTH_ARMOR, ArmorItem.Type.LEGGINGS, new Item.Settings()));
    public static final EarthArmorItem EARTH_BOOTS = (EarthArmorItem) registerItem("earth_boots",
            new EarthArmorItem(ElementalsArmorMaterial.EARTH_ARMOR, ArmorItem.Type.BOOTS, new Item.Settings()));

    public static final Set<Item> METAL_ARMOR_SET = new HashSet<>();
    public static final MetalArmorItem METAL_HELMET = (MetalArmorItem) registerItem("metal_helmet",
            new MetalArmorItem(ElementalArmorMaterial.METAL, ArmorItem.Type.HELMET, new Item.Settings()));
    public static final MetalArmorItem METAL_CHESTPLATE = (MetalArmorItem) registerItem("metal_chestplate",
            new MetalArmorItem(ElementalArmorMaterial.METAL, ArmorItem.Type.CHESTPLATE, new Item.Settings()));
    public static final MetalArmorItem METAL_LEGGINGS = (MetalArmorItem) registerItem("metal_leggings",
            new MetalArmorItem(ElementalArmorMaterial.METAL, ArmorItem.Type.LEGGINGS, new Item.Settings()));
    public static final MetalArmorItem METAL_BOOTS = (MetalArmorItem) registerItem("metal_boots",
            new MetalArmorItem(ElementalArmorMaterial.METAL, ArmorItem.Type.BOOTS, new Item.Settings()));

    public static final ScrollItem SCROLL_ITEM = (ScrollItem) registerItem("scroll",
            new ScrollItem(new Item.Settings()
                    .maxCount(1)));

    public static final FireScrollItem FIRE_SCROLL_ITEM = (FireScrollItem) registerItem("fire_scroll",
            new FireScrollItem(new Item.Settings()
                    .maxCount(1)));

    public static final WaterScrollItem WATER_SCROLL_ITEM = (WaterScrollItem) registerItem("water_scroll",
            new WaterScrollItem(new Item.Settings()
                    .maxCount(1)));

    public static final EarthScrollItem EARTH_SCROLL_ITEM = (EarthScrollItem) registerItem("earth_scroll",
            new EarthScrollItem(new Item.Settings()
                    .maxCount(1)));

    public static final AirScrollItem AIR_SCROLL_ITEM = (AirScrollItem) registerItem("air_scroll",
            new AirScrollItem(new Item.Settings()
                    .maxCount(1)));

    public static final LightningScrollItem LIGHTNING_SCROLL_ITEM = (LightningScrollItem) registerItem("lightning_scroll",
            new LightningScrollItem(new Item.Settings()
                    .maxCount(1)));
    public static final LightningBottleItem LIGHTNING_BOTTLE_ITEM = (LightningBottleItem) registerItem("lightning_bottle",
            new LightningBottleItem(new Item.Settings()
                    .maxCount(1)
                    .food(LIGHTNING_BOTTLE_FOOD_COMPONENT)
            ));
    public static final BloodScrollItem BLOOD_SCROLL_ITEM = (BloodScrollItem) registerItem("blood_scroll",
            new BloodScrollItem(new Item.Settings()
                    .maxCount(1)));
    public static final DirtBottleItem DIRT_BOTTLE_ITEM = (DirtBottleItem) registerItem("dirt_bottle",
            new DirtBottleItem(new Item.Settings()
                    .maxCount(1)));
    public static final BoomerangItem BOOMERANG_ITEM = (BoomerangItem) registerItem("boomerang",
            new BoomerangItem(new Item.Settings()
                    .maxCount(1)
            ));
    public static final WaterPouchItem WATER_POUCH_ITEM = (WaterPouchItem) registerItem("water_pouch",
            new WaterPouchItem(new Item.Settings()
                    .maxCount(1)
            ));

    public static final GliderItem GLIDER_ITEM = (GliderItem) registerItem("glider",
            new GliderItem(new Item.Settings()
                    .maxCount(1)));
    public static final BannerPatternItem AIR_BANNER_PATTERN_ITEM = Registry.register(Registries.ITEM, 
            new Identifier(MODID,"air_banner_pattern"), 
            new BannerPatternItem(AIR_BANNER_PATTERN_TAG,new FabricItemSettings()
                    .maxCount(1)));
    public static final BannerPattern AIR_BANNER_PATTERN = new BannerPattern("elementals_air");
            

    //BLOCK ITEMS
    public static final BlockItem MOON_PEACH_LEAVES_ITEM = Registry.register(Registries.ITEM,
            new Identifier(MODID, "moon_peach_leaves"), 
            new BlockItem(ElementalsBlocks.MOON_PEACH_LEAVES, new FabricItemSettings()));
    public static final BlockItem MOON_LOG = Registry.register(Registries.ITEM,
            new Identifier(MODID, "moon_log"),
            new BlockItem(ElementalsBlocks.MOON_LOG, new FabricItemSettings()));
    public static final BlockItem MOON_STRIPPED_LOG = Registry.register(Registries.ITEM,
            new Identifier(MODID, "moon_stripped_log"),
            new BlockItem(ElementalsBlocks.MOON_STRIPPED_LOG, new FabricItemSettings()));
    public static final BlockItem MOON_PLANKS = Registry.register(Registries.ITEM,
            new Identifier(MODID, "moon_planks"),
            new BlockItem(ElementalsBlocks.MOON_PLANKS, new FabricItemSettings()));

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
                entries.add(BLOOD_SCROLL_ITEM);
                entries.add(DIRT_BOTTLE_ITEM);
                entries.add(LIGHTNING_BOTTLE_ITEM);
                entries.add(BOOMERANG_ITEM);
                entries.add(WATER_POUCH_ITEM);
                entries.add(GLIDER_ITEM);
                entries.add(AIR_BANNER_PATTERN_ITEM);
                entries.add(MOON_LOG);
                entries.add(MOON_STRIPPED_LOG);
                entries.add(MOON_PLANKS);
                entries.add(MOON_PEACH_LEAVES_ITEM);
            }).build();



    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(MODID,name), item);
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
        
        Registry.register(Registries.ITEM_GROUP, Identifier.of(MODID, "elementals_group"), ELEMENTALS_GROUP);
        Registry.register(Registries.BANNER_PATTERN,Identifier.of(MODID,"air"),AIR_BANNER_PATTERN);
    }
}
