package dev.saperate.elementals.mixin;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.lightning.LightningElement;
import dev.saperate.elementals.items.ElementalItems;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.saperate.elementals.effects.OverchargedStatusEffect.OVERCHARGED_EFFECT;
import static dev.saperate.elementals.effects.ShockedStatusEffect.SHOCKED_EFFECT;
import static dev.saperate.elementals.effects.StaticAuraStatusEffect.STATIC_AURA_EFFECT;
import static dev.saperate.elementals.effects.StunnedStatusEffect.STUNNED_EFFECT;
import static dev.saperate.elementals.utils.SapsUtils.safeHasStatusEffect;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
    @Shadow public abstract ItemStack getStack();

    @Inject(at = @At("HEAD"), method = "damage", cancellable = true)
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        ItemEntity entity = ((ItemEntity) (Object) this);
        ItemStack stack = getStack();

        if(source.isOf(DamageTypes.LIGHTNING_BOLT) && stack.getItem().equals(Items.GLASS_BOTTLE)){
            stack.decrement(1);
            ItemEntity lightningBottleEntity = new ItemEntity(
                    entity.getWorld(),
                    entity.getX(),entity.getY(),entity.getZ(),
                    ElementalItems.LIGHTNING_BOTTLE_ITEM.getDefaultStack()
            );
            entity.getWorld().spawnEntity(lightningBottleEntity);
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}