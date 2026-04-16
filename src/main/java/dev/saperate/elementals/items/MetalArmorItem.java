package dev.saperate.elementals.items;

import dev.saperate.elementals.Elementals;
import dev.saperate.elementals.effects.ElementalsStatusEffects;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

//FIXME port the geckolib impl
public class MetalArmorItem extends ArmorItem implements GeoItem {
    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);
    private static final String ITEMS_KEY = "Items";
    public static final int MAX_STORAGE = 1;


    public MetalArmorItem(RegistryEntry<ArmorMaterial> armorMaterial, Type type, Settings settings) {
        super(armorMaterial, type, settings);
    }
    

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if(slot != 0){
            return;
        }
        if(entity instanceof LivingEntity living){//Not inlining since i might need that later
            if(entity instanceof PlayerEntity player){
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS,60, 3, false, false, false));
                player.addStatusEffect(new StatusEffectInstance(ElementalsStatusEffects.SEISMIC_SENSE,120,0, false, false, false));
                player.addStatusEffect(new StatusEffectInstance(ElementalsStatusEffects.DENSE,120,10, false, false, false));
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
    public ItemStack getItemStack(ItemStack prevArmor, int colorMultiply, World world) {
        ItemStack item = getDefaultStack();
        
        RegistryEntry<Enchantment> bindingEnchant = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).entryOf(Enchantments.BINDING_CURSE);
        RegistryEntry<Enchantment> protectionEnchant = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).entryOf(Enchantments.PROTECTION);
        item.addEnchantment(bindingEnchant,0);
        item.addEnchantment(protectionEnchant,3);
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

        BundleContentsComponent contents = new BundleContentsComponent(items);
        armor.set(DataComponentTypes.BUNDLE_CONTENTS, contents);
        return true;
    }

    /**
     * Retrieves an {@link ItemStack} stored in another. This will only get the first one, so if there is multiple
     * they will be ignored.
     * @param armor where we retrieve the item
     * @return the stack previously added with {@link #putItem(ItemStack, ItemStack)} or {@link ItemStack#EMPTY}
     */
    public static ItemStack getItem(ItemStack armor){
        BundleContentsComponent contents = armor.get(DataComponentTypes.BUNDLE_CONTENTS);
        if(contents == null){
            return ItemStack.EMPTY;
        }
        return contents.get(0);
    }

    public static int getColor(ItemStack stack) {
        DyedColorComponent component = stack.get(DataComponentTypes.DYED_COLOR);
        if(component == null){
            return 0xFFFFFFFF;
        }
        return component.rgb();
    }

    public static void removeColor(ItemStack stack) {
        setColor(stack, 0xFFFFFFFF);
    }

    public static void setColor(ItemStack stack, int color) {
        stack.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(color, false));
    }
    
    @Override
    public boolean hasGlint(ItemStack stack) {
        return false;
    }

    
    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(Elementals.METAL_ARMOR_RENDER_PROVIDER.Create());
    }

    @Override
    public Supplier<Object> getRenderProvider() {
        return renderProvider;
    }
    

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animatableInstanceCache;
    }


}
