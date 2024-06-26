package dev.saperate.elementals.elements.earth;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.items.EarthArmorItem;
import net.minecraft.block.Block;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.util.HashSet;

import static dev.saperate.elementals.effects.DenseStatusEffect.DENSE_EFFECT;
import static dev.saperate.elementals.effects.SeismicSenseStatusEffect.SEISMIC_SENSE_EFFECT;
import static dev.saperate.elementals.items.ElementalItems.*;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.raycastBlockCustomRotation;

public class AbilityEarthArmor implements Ability {

    @Override
    public void onCall(Bender bender, long deltaT) {
        bender.setCurrAbility(null);
        PlayerEntity player = bender.player;
        DefaultedList<ItemStack> inv = player.getInventory().armor;

        if (player.getInventory().containsAny(EARTH_ARMOR_SET)) {
            removeArmorSet(inv);

            player.removeStatusEffect(SEISMIC_SENSE_EFFECT);
            player.removeStatusEffect(DENSE_EFFECT);
            player.removeStatusEffect(StatusEffects.NIGHT_VISION);
            player.removeStatusEffect(StatusEffects.BLINDNESS);
            return;
        }

        BlockHitResult hit = raycastBlockCustomRotation(player, 4, true, new Vec3d(0, -1, 0));

        if (!EarthElement.isBlockBendable(player.getWorld().getBlockState(hit.getBlockPos())) || !player.isOnGround()) {
            return;
        }
        if (!bender.reduceChi(30)) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }

        Block standingBlock = player.getWorld().getBlockState(hit.getBlockPos()).getBlock();

        inv.set(EquipmentSlot.HEAD.getEntitySlotId(), EARTH_HELMET.getItemStack(inv.get(3), standingBlock));
        inv.set(EquipmentSlot.CHEST.getEntitySlotId(), EARTH_CHESTPLATE.getItemStack(inv.get(2), standingBlock));
        inv.set(EquipmentSlot.LEGS.getEntitySlotId(), EARTH_LEGGINGS.getItemStack(inv.get(1), standingBlock));
        inv.set(EquipmentSlot.FEET.getEntitySlotId(), EARTH_BOOTS.getItemStack(inv.get(0), standingBlock));


    }

    public static void removeArmorSet(DefaultedList<ItemStack> inv) {
        ItemStack[] armor = getArmorStacks(inv);

        removeArmor(EquipmentSlot.HEAD, armor[0], inv);
        removeArmor(EquipmentSlot.CHEST, armor[1], inv);
        removeArmor(EquipmentSlot.LEGS, armor[2], inv);
        removeArmor(EquipmentSlot.FEET, armor[3], inv);
    }

    public static void removeArmor(EquipmentSlot slot, ItemStack stack, DefaultedList<ItemStack> inv) {
        if (!stack.isEmpty() && stack.getItem() instanceof EarthArmorItem) {
            ItemStack item = EarthArmorItem.getBundledStacks(stack).findFirst().orElse(ItemStack.EMPTY);
            inv.set(slot.getEntitySlotId(), item);
        }
    }

    public static ItemStack[] getArmorStacks(DefaultedList<ItemStack> inv) {
        return new ItemStack[]{
                inv.get(EquipmentSlot.HEAD.getEntitySlotId()),
                inv.get(EquipmentSlot.CHEST.getEntitySlotId()),
                inv.get(EquipmentSlot.LEGS.getEntitySlotId()),
                inv.get(EquipmentSlot.FEET.getEntitySlotId())
        };
    }

    @Override
    public void onLeftClick(Bender bender, boolean started) {

    }

    @Override
    public void onMiddleClick(Bender bender, boolean started) {

    }

    @Override
    public void onRightClick(Bender bender, boolean started) {

    }

    @Override
    public void onTick(Bender bender) {

    }

    @Override
    public void onRemove(Bender bender) {

    }

}
