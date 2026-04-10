package dev.saperate.elementals.items;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import dev.saperate.elementals.effects.ElementalsStatusEffects;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Stream;


public class EarthArmorItem extends ArmorItem{
    private static final String ITEMS_KEY = "Items";
    public static final int MAX_STORAGE = 1;


    public EarthArmorItem(RegistryEntry<ArmorMaterial> armorMaterial, Type type, Settings settings) {
        super(armorMaterial, type, settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (slot != 0) {
            return;
        }
        if(entity instanceof LivingEntity player){
            player.addStatusEffect(new StatusEffectInstance(ElementalsStatusEffects.SEISMIC_SENSE,60, 0, false, false, true));
            player.addStatusEffect(new StatusEffectInstance(ElementalsStatusEffects.DENSE,120,10, false, false, false));
        }
        //TODO figure out how to add armor points
    }
    
    /**
     * Stores the previous armor and colors the new one then returns the new armor
     * @param prevArmor The previous armor the player was wearing
     * @param standingBlock The block the player is standing on
     * @return the new armor as an item stack
     */
    public ItemStack getItemStack(ItemStack prevArmor, Block standingBlock, World world) {
        ItemStack item = getDefaultStack();
        //Fuck you for making this so painful
        RegistryEntry<Enchantment> enchant = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).entryOf(Enchantments.BINDING_CURSE);
        item.addEnchantment(enchant, 1);

        putItem(item,prevArmor);


        int color = standingBlock.getDefaultMapColor().color;
        if (standingBlock.equals(Blocks.GRASS_BLOCK)) {
            color = Blocks.DIRT.getDefaultMapColor().color;
        }
        item.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(darkenColor(color,4), false));

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


    static int darkenColor(int col, int amt) {
        int r = Math.max((col >> 16), amt);
        int b = Math.max(((col >> 8) & 0x00FF), amt);
        int g = Math.max((col & 0x0000FF), amt);
        return g | (b << 8) | (r << 16);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return false;
    }
}
