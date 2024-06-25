package dev.saperate.elementals.mixin;

import com.mojang.brigadier.ParseResults;
import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.data.StateDataSaverAndLoader;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.air.AbilityAirScooter;
import dev.saperate.elementals.elements.air.AbilityAirShield;
import dev.saperate.elementals.elements.earth.AbilityEarthArmor;
import dev.saperate.elementals.elements.fire.AbilityFireIgnite;
import dev.saperate.elementals.elements.fire.AbilityFireShield;
import dev.saperate.elementals.elements.water.AbilityWaterShield;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.saperate.elementals.effects.SpiritProjectionStatusEffect.SPIRIT_PROJECTION_EFFECT;
import static dev.saperate.elementals.utils.SapsUtils.safeHasStatusEffect;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Shadow
    public abstract void remove(Entity.RemovalReason reason);

    @Inject(at = @At("TAIL"), method = "tick")
    private void tick(CallbackInfo ci) {
        PlayerEntity player = ((PlayerEntity) (Object) this);


        if (safeHasStatusEffect(SPIRIT_PROJECTION_EFFECT, player)) {
            //checks if we are inside a wall
            float f = player.getDimensions(player.getPose()).width * 0.8f;
            Box box = Box.of(player.getEyePos(), f, 1.0E-6, f);

            if (SapsUtils.checkBlockCollision(player, 0.1f, false, true, box) != null) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 21, 0, false, false, false));
            }
        }
        Bender bender = Bender.getBender(player);
        if (bender == null || player.getWorld().isClient) {
            return;
        }
        bender.tick();
        if (bender.castTime != null) {
            return;
        }

        if (bender.currAbility != null && !player.getWorld().isClient) {
            bender.currAbility.onTick(bender);
        }
    }

}