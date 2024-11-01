package dev.saperate.elementals.items;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import dev.saperate.elementals.effects.ElementalsStatusEffects;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
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
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

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
        if(slot != 0){
            return;
        }
        if(entity instanceof LivingEntity player){
            player.addStatusEffect(new StatusEffectInstance(ElementalsStatusEffects.SEISMIC_SENSE,60, 0, false, false, false));
            player.addStatusEffect(new StatusEffectInstance(ElementalsStatusEffects.DENSE,120,10, false, false, false));

            //TODO figure out how to add armor points
        }
    }


    /**
     * Stores the previous armor and colors the new one then returns the new armor
     * @param prevArmor The previous armor the player was wearing
     * @param standingBlock The block the player is standing on
     * @return the new armor as an item stack
     */
    public ItemStack getItemStack(ItemStack prevArmor, Block standingBlock) {
        ItemStack item = getDefaultStack();
        ArmorMaterials.LEATHER
        item.addEnchantment(Enchantments.BINDING_CURSE, 1);
        item.addHideFlag(ItemStack.TooltipSection.ENCHANTMENTS);
        item.addHideFlag(ItemStack.TooltipSection.DYE);


        addToBundle(item,prevArmor);


        int color = standingBlock.getDefaultMapColor().color;
        if(standingBlock.equals(Blocks.GRASS_BLOCK)){
            color = Blocks.DIRT.getDefaultMapColor().color;
        }
        setColor(item, darkenColor(color,4));

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




    static int darkenColor(int col, int amt) {
        int r = Math.max((col >> 16), amt);
        int b = Math.max(((col >> 8) & 0x00FF), amt);
        int g = Math.max((col & 0x0000FF), amt);
        return g | (b << 8) | (r << 16);
    }
}
