package dev.saperate.elementals.items;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.item.BundleTooltipData;
import net.minecraft.client.item.TooltipData;
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
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Stream;

import static dev.saperate.elementals.effects.DenseStatusEffect.DENSE_EFFECT;
import static dev.saperate.elementals.effects.SeismicSenseStatusEffect.SEISMIC_SENSE_EFFECT;

public class EarthArmorItem extends DyeableArmorItem {
    private static final String ITEMS_KEY = "Items";
    public static final int MAX_STORAGE = 1;


    public EarthArmorItem(ArmorMaterial armorMaterial, Type type, Settings settings) {
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
                //TODO if this move is too op, add mining fatigue
                player.addStatusEffect(new StatusEffectInstance(SEISMIC_SENSE_EFFECT,60, 0, false, false, false));
                player.addStatusEffect(new StatusEffectInstance(DENSE_EFFECT,60,10, false, false, false));
            }
            //TODO figure out how to add armor points
        }
    }


    public static int getAdditionalProtection(ItemStack stack){
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        return nbtCompound.getInt("additional_protection");
    }

    public static float getAdditionalToughness(ItemStack stack){
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        return nbtCompound.getFloat("additional_toughness");
    }

    public static void setAdditionalProtection(ItemStack stack, int val){
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        nbtCompound.putInt("additional_protection", val);
    }

    public static void setAdditionalToughness(ItemStack stack, float val){
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        nbtCompound.putFloat("additional_toughness", val);
    }


    public ItemStack getItemStack(ItemStack prevArmor) {
        ItemStack item = getDefaultStack();
        item.addEnchantment(Enchantments.BINDING_CURSE, 1);
        item.addHideFlag(ItemStack.TooltipSection.ENCHANTMENTS);
        item.addHideFlag(ItemStack.TooltipSection.DYE);

        if(prevArmor.getItem() instanceof ArmorItem armorItem){
            setAdditionalProtection(item, armorItem.getProtection());
            setAdditionalToughness(item, armorItem.getToughness());
        }

        addToBundle(item,prevArmor);
        return item;
    }

    //Taken from vanilla bundle code

    private static int addToBundle(ItemStack bundle, ItemStack stack) {
        if (stack.isEmpty() || !stack.getItem().canBeNested()) {
            return 0;
        }
        NbtCompound nbtCompound = bundle.getOrCreateNbt();
        if (!nbtCompound.contains(ITEMS_KEY)) {
            nbtCompound.put(ITEMS_KEY, new NbtList());
        }
        int i = getBundleOccupancy(bundle);
        int j = getItemOccupancy(stack);
        int k = Math.min(stack.getCount(), (MAX_STORAGE - i) / j);
        if (k == 0) {
            return 0;
        }
        NbtList nbtList = nbtCompound.getList(ITEMS_KEY, NbtElement.COMPOUND_TYPE);
        Optional<NbtCompound> optional = canMergeStack(stack, nbtList);
        if (optional.isPresent()) {
            NbtCompound nbtCompound2 = optional.get();
            ItemStack itemStack = ItemStack.fromNbt(nbtCompound2);
            itemStack.increment(k);
            itemStack.writeNbt(nbtCompound2);
            nbtList.remove(nbtCompound2);
            nbtList.add(0, nbtCompound2);
        } else {
            ItemStack itemStack2 = stack.copyWithCount(k);
            NbtCompound nbtCompound3 = new NbtCompound();
            itemStack2.writeNbt(nbtCompound3);
            nbtList.add(0, nbtCompound3);
        }
        return k;
    }

    private static Optional<NbtCompound> canMergeStack(ItemStack stack, NbtList items) {
        if (stack.isOf(Items.BUNDLE)) {
            return Optional.empty();
        }
        return items.stream().filter(NbtCompound.class::isInstance).map(NbtCompound.class::cast).filter(item -> ItemStack.canCombine(ItemStack.fromNbt(item), stack)).findFirst();
    }

    private static int getItemOccupancy(ItemStack stack) {
        NbtCompound nbtCompound;
        if (stack.isOf(Items.BUNDLE)) {
            return 4 + getBundleOccupancy(stack);
        }
        if ((stack.isOf(Items.BEEHIVE) || stack.isOf(Items.BEE_NEST)) && stack.hasNbt() && (nbtCompound = BlockItem.getBlockEntityNbt(stack)) != null && !nbtCompound.getList("Bees", NbtElement.COMPOUND_TYPE).isEmpty()) {
            return MAX_STORAGE;
        }
        return MAX_STORAGE / stack.getMaxCount();
    }

    private static int getBundleOccupancy(ItemStack stack) {
        return getBundledStacks(stack).mapToInt(itemStack -> getItemOccupancy(itemStack) * itemStack.getCount()).sum();
    }
    public static Stream<ItemStack> getBundledStacks(ItemStack stack) {
        NbtCompound nbtCompound = stack.getNbt();
        if (nbtCompound == null) {
            return Stream.empty();
        }
        NbtList nbtList = nbtCompound.getList(ITEMS_KEY, NbtElement.COMPOUND_TYPE);
        return nbtList.stream().map(NbtCompound.class::cast).map(ItemStack::fromNbt);
    }


    @Override
    public boolean hasGlint(ItemStack stack) {
        return false;
    }

}
