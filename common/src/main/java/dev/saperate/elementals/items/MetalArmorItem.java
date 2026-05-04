package dev.saperate.elementals.items;

import dev.saperate.elementals.Elementals;
import dev.saperate.elementals.effects.ElementalsStatusEffects;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MetalArmorItem extends ArmorItem implements GeoItem {
    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);
    private static final String ITEMS_KEY = "Items";
    public static final int MAX_STORAGE = 1;


    public MetalArmorItem(Holder<ArmorMaterial> armorMaterial, Type type, Properties settings) {
        super(armorMaterial, type, settings);
    }
    

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        if(slot != 0){
            return;
        }
        if(entity instanceof LivingEntity living){//Not inlining since i might need that later
            if(entity instanceof Player player){
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,60, 3, false, false, false));
                player.addEffect(new MobEffectInstance(ElementalsStatusEffects.SEISMIC_SENSE,120,0, false, false, false));
                player.addEffect(new MobEffectInstance(ElementalsStatusEffects.DENSE,120,10, false, false, false));
            }
            //TODO figure out how to add armor points
        }
    }
    
    /**
     * Stores the previous armor and colors the new one then returns the new armor
     * @param prevArmor The previous armor the player was wearing
     * @param colorMultiply The color to dye the armor with
     * @return the new armor as an item stack
     */
    public ItemStack getItemStack(ItemStack prevArmor, int colorMultiply, Level world) {
        ItemStack item = getDefaultInstance();

        Holder<Enchantment> bindingEnchant = world.holderLookup(Registries.ENCHANTMENT).get(Enchantments.BINDING_CURSE).get();
        Holder<Enchantment> protectionEnchant = world.holderLookup(Registries.ENCHANTMENT).get(Enchantments.PROTECTION).get();
        item.enchant(bindingEnchant,0);
        item.enchant(protectionEnchant,3);
        //FIXME
        //item.addHideFlag(ItemStack.TooltipSection.ENCHANTMENTS);
        //item.addHideFlag(ItemStack.TooltipSection.DYE);
        
        setColor(item, colorMultiply);
        putItem(item,prevArmor);
        return item;
    }

    /**
     * Puts an {@link ItemStack} in another by mimicking how the {@link BundleItem} stores other {@link ItemStack}.
     * This will only store a single item though, and it will override anything already in it.
     * @param armor where the stack will be held
     * @param stack the stack to add
     * @return True if item was added
     */
    private static boolean putItem(ItemStack armor, ItemStack stack) {
        if(stack == ItemStack.EMPTY){//nothing to do
            return false;
        }
        //TODO handle when there is already items in armor
        ArrayList<ItemStack> items = new ArrayList<>();
        items.add(stack);

        BundleContents contents = new BundleContents(items);
        armor.set(DataComponents.BUNDLE_CONTENTS, contents);
        return true;
    }

    /**
     * Retrieves an {@link ItemStack} stored in another. This will only get the first one, so if there is multiple
     * they will be ignored.
     * @param armor where we retrieve the item
     * @return the stack previously added with {@link #putItem(ItemStack, ItemStack)} or {@link ItemStack#EMPTY}
     */
    public static ItemStack getItem(ItemStack armor){
        BundleContents contents = armor.get(DataComponents.BUNDLE_CONTENTS);
        if(contents == null){
            return ItemStack.EMPTY;
        }
        return contents.getItemUnsafe(0);
    }

    public static int getColor(ItemStack stack) {
        DyedItemColor component = stack.get(DataComponents.DYED_COLOR);
        if(component == null){
            return 0xFFFFFFFF;
        }
        return component.rgb();
    }

    public static void removeColor(ItemStack stack) {
        setColor(stack, 0xFFFFFFFF);
    }

    public static void setColor(ItemStack stack, int color) {
        stack.set(DataComponents.DYED_COLOR, new DyedItemColor(color, false));
    }
    
    @Override
    public boolean hasGlint(ItemStack stack) {
        return false;
    }

    
    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(Elementals.METAL_ARMOR_RENDER_PROVIDER.Create());
    }
    

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animatableInstanceCache;
    }


}
