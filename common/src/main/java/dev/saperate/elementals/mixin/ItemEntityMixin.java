package dev.saperate.elementals.mixin;

import dev.saperate.elementals.items.ElementalsItems;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    @Shadow
    public abstract ItemStack getItem();

    @Inject(at = @At("HEAD"), method = "hurt", cancellable = true)
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        ItemEntity entity = ((ItemEntity) (Object) this);
        ItemStack stack = getItem();
        if (source.is(DamageTypes.LIGHTNING_BOLT)) {
            if (stack.getItem().equals(Items.GLASS_BOTTLE)) {
                stack.shrink(1);
                ItemEntity lightningBottleEntity = new ItemEntity(
                        entity.level(),
                        entity.getX(), entity.getY(), entity.getZ(),
                        ElementalsItems.LIGHTNING_BOTTLE_ITEM.getDefaultInstance()
                );
                entity.level().addFreshEntity(lightningBottleEntity);
                cir.setReturnValue(true);
                cir.cancel();
            } else if (stack.getItem().equals(ElementalsItems.LIGHTNING_BOTTLE_ITEM)) {
                cir.setReturnValue(false);
                cir.cancel();
            }
        }
    }

}