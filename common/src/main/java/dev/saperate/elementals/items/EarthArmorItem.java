package dev.saperate.elementals.items;

import dev.saperate.elementals.effects.ElementalsStatusEffects;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;


public class EarthArmorItem extends ArmorItem {
    private static final String ITEMS_KEY = "Items";
    public static final int MAX_STORAGE = 1;


    public EarthArmorItem(Holder<ArmorMaterial> armorMaterial, Type type, Properties settings) {
        super(armorMaterial, type, settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (slot != 0) {
            return;
        }
        if(entity instanceof LivingEntity living){
            living.addEffect(new MobEffectInstance(ElementalsStatusEffects.SEISMIC_SENSE,60, 0, false, false, true));
            living.addEffect(new MobEffectInstance(ElementalsStatusEffects.DENSE,120,10, false, false, false));
        }
        //TODO figure out how to add armor points
    }
    
    /**
     * Stores the previous armor and colors the new one then returns the new armor
     * @param prevArmor The previous armor the player was wearing
     * @param standingBlock The block the player is standing on
     * @return the new armor as an item stack
     */
    public ItemStack getItemStack(ItemStack prevArmor, Block standingBlock, Level world) {
        ItemStack item = getDefaultInstance();
        //Fuck you for making this so painful
        Holder<Enchantment> enchant = world.holderLookup(Registries.ENCHANTMENT).get(Enchantments.BINDING_CURSE).get();
        item.enchant(enchant, 1);

        putItem(item,prevArmor);


        int color = standingBlock.defaultMapColor().col;
        if (standingBlock.equals(Blocks.GRASS_BLOCK)) {
            color = Blocks.DIRT.defaultMapColor().col;
        }
        item.set(DataComponents.DYED_COLOR, new DyedItemColor(darkenColor(color,4), false));

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


    static int darkenColor(int col, int amt) {
        int r = Math.max((col >> 16), amt);
        int b = Math.max(((col >> 8) & 0x00FF), amt);
        int g = Math.max((col & 0x0000FF), amt);
        return g | (b << 8) | (r << 16);
    }
    
    @Override
    public boolean isFoil(ItemStack stack) {
        return false;
    }
}
