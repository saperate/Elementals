package dev.saperate.elementals.enchantments;

import dev.saperate.elementals.items.WaterPouchItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class VolumeEnchantment extends Enchantment{


    public VolumeEnchantment(){
        super(Rarity.COMMON, EnchantmentTarget.BREAKABLE
        , new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinLevel() {
        return 1;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public int getMinPower(int level) {
        return 1 + level * 10;
    }

    @Override
    public int getMaxPower(int level) {
        return this.getMinPower(level) + 5;
    }


    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof WaterPouchItem;
    }
}
