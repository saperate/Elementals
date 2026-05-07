package dev.saperate.elementals.mixin;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.air.AbilityAirScooter;
import dev.saperate.elementals.elements.air.AbilityAirShield;
import dev.saperate.elementals.elements.earth.AbilityEarthArmor;
import dev.saperate.elementals.elements.fire.AbilityFireShield;
import dev.saperate.elementals.elements.metal.AbilityMetalArmor;
import dev.saperate.elementals.elements.metal.AbilityMetalDecoy;
import dev.saperate.elementals.elements.water.AbilityWaterShield;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin {

    @Inject(at = @At("HEAD"), method = "die")
    private void onDeath(DamageSource damageSource, CallbackInfo ci) {
        Player player = ((Player) (Object) this);
        AbilityEarthArmor.removeArmorSet(player.getInventory().armor);
        AbilityMetalArmor.removeArmorSet(player.getInventory().armor);
    }

    @Inject(at = @At("HEAD"), method = "hurt", cancellable = true)
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        Player player = ((Player) (Object) this);
        if (!player.level().isClientSide) {
            Bender bender = Bender.getBender((ServerPlayer) player);
            if (bender.currAbility instanceof AbilityAirShield   //damage canceler
                    || bender.currAbility instanceof AbilityWaterShield
                    || bender.currAbility instanceof AbilityFireShield
                    || (bender.currAbility instanceof AbilityAirScooter && source.is(DamageTypes.FALL))) {

                if (source.is(DamageTypes.DRAGON_BREATH)//TODO make a list like earth bendable blocks
                        || source.is(DamageTypes.DROWN)
                        || source.is(DamageTypes.DRY_OUT)
                        || source.is(DamageTypes.FREEZE)
                        || source.is(DamageTypes.IN_FIRE)
                        || source.is(DamageTypes.ON_FIRE)
                        || source.is(DamageTypes.LAVA)
                        || source.is(DamageTypes.LIGHTNING_BOLT)
                        || source.is(DamageTypes.MAGIC)
                        || source.is(DamageTypes.FELL_OUT_OF_WORLD)
                        || source.is(DamageTypes.INDIRECT_MAGIC)
                        || source.is(DamageTypes.SONIC_BOOM)
                        || source.is(DamageTypes.STARVE)
                        || source.is(DamageTypes.OUTSIDE_BORDER)) {
                    return;
                }
                cir.setReturnValue(false);
                cir.cancel();
            }
        }
    }


    @Inject(at = @At("HEAD"), method = "openMenu", cancellable = true)
    private void init(MenuProvider factory, CallbackInfoReturnable<OptionalInt> cir) {
        Player player = ((Player) (Object) this);
        Bender bender = Bender.getBender((ServerPlayer) player);
        if(bender.currAbility instanceof AbilityMetalDecoy)
            cir.cancel();
    }
}
