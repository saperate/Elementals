package dev.saperate.elementals.mixin;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.air.AbilityAirScooter;
import dev.saperate.elementals.elements.air.AbilityAirShield;
import dev.saperate.elementals.elements.earth.AbilityEarthArmor;
import dev.saperate.elementals.elements.fire.AbilityFireShield;
import dev.saperate.elementals.elements.water.AbilityWaterShield;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    @Inject(at = @At("TAIL"), method = "<init>")
    private void init(CallbackInfo info) {
        PlayerEntity player = ((PlayerEntity) (Object) this);
        if (Bender.getBender(player) == null) {
            new Bender(player, null);
        } else {
            Bender.getBender(player).player = player;
        }
    }

    @Inject(at = @At("HEAD"), method = "onDeath")
    private void onDeath(DamageSource damageSource, CallbackInfo ci) {
        PlayerEntity player = ((PlayerEntity) (Object) this);

        System.out.println("removing armor");
        AbilityEarthArmor.removeArmorSet(player.getInventory().armor);
    }

    @Inject(at = @At("HEAD"), method = "damage", cancellable = true)
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = ((PlayerEntity) (Object) this);
        if (!player.getWorld().isClient) {
            Bender bender = Bender.getBender(player);
            if (bender.currAbility instanceof AbilityAirShield   //damage canceler
                    || bender.currAbility instanceof AbilityWaterShield
                    || bender.currAbility instanceof AbilityFireShield
                    || (bender.currAbility instanceof AbilityAirScooter && source.isOf(DamageTypes.FALL))) {

                if (source.isOf(DamageTypes.DRAGON_BREATH)//TODO make a list like earth bendable blocks
                        && source.isOf(DamageTypes.DROWN)
                        && source.isOf(DamageTypes.DRY_OUT)
                        && source.isOf(DamageTypes.FREEZE)
                        && source.isOf(DamageTypes.IN_FIRE)
                        && source.isOf(DamageTypes.ON_FIRE)
                        && source.isOf(DamageTypes.LAVA)
                        && source.isOf(DamageTypes.LIGHTNING_BOLT)
                        && source.isOf(DamageTypes.MAGIC)
                        && source.isOf(DamageTypes.OUT_OF_WORLD)
                        && source.isOf(DamageTypes.INDIRECT_MAGIC)
                        && source.isOf(DamageTypes.SONIC_BOOM)
                        && source.isOf(DamageTypes.STARVE)
                        && source.isOf(DamageTypes.OUTSIDE_BORDER)) {
                    return;
                }
                cir.setReturnValue(false);
                cir.cancel();
            }
        }
    }
}
