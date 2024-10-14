package dev.saperate.elementals.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.saperate.elementals.items.ElementalItems;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

    @Inject(at = @At(value = "RETURN"), method = "getPossibleEntries", cancellable = true)
    private static void fixAcceptableItem(int power, ItemStack stack, boolean treasureAllowed, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir) {
        List<EnchantmentLevelEntry> enchants = new ArrayList<>();
        //We have to recheck all the enchantments because getPossibleEntries
        //check the right method to see if it is an acceptable item.
        for (EnchantmentLevelEntry entry : cir.getReturnValue()){
            if(entry.enchantment.isAcceptableItem(stack)) {
                enchants.add(entry);
            }
        }
        cir.setReturnValue(enchants);
    }
}