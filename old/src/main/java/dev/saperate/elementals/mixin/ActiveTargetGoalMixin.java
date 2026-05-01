package dev.saperate.elementals.mixin;


import dev.saperate.elementals.entities.common.DecoyPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(ActiveTargetGoal.class)
public abstract class ActiveTargetGoalMixin {

    @Unique
    protected LivingEntity mob;
    @Shadow @Nullable protected LivingEntity targetEntity;

    @Shadow @Final protected Class targetClass;

    @Shadow protected abstract Box getSearchBox(double distance);

    @Shadow protected TargetPredicate targetPredicate;

    @Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/entity/mob/MobEntity;Ljava/lang/Class;IZZLjava/util/function/Predicate;)V")
    private void init(MobEntity mob, Class targetClass, int reciprocalChance, boolean checkVisibility, boolean checkCanNavigate, Predicate targetPredicate, CallbackInfo ci) {
        this.mob = mob;
    }

    @Inject(at = @At("HEAD"), method = "findClosestTarget", cancellable = true)
    private void findClosestTargetIncludeDecoys(CallbackInfo ci) {
        //TODO maybe add a config that disables this
        if(targetClass == PlayerEntity.class || targetClass == ServerPlayerEntity.class) {
            PlayerEntity plr = mob.getWorld().getClosestPlayer(this.targetPredicate, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
            LivingEntity decoy = mob.getWorld().getClosestEntity(this.mob.getWorld().getEntitiesByClass(DecoyPlayerEntity.class, getSearchBox(this.customGetFollowRange()), livingEntity -> true), targetPredicate, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
            if (plr == null) {
                if (decoy == null) {
                    ci.cancel();
                    return;
                } else {
                    targetEntity = decoy;
                }
            } else if (decoy == null) {
                targetEntity = plr;
            } else {
                targetEntity = plr.squaredDistanceTo(mob) > decoy.squaredDistanceTo(mob) ? decoy : plr;
            }
            ci.cancel();

        }
    }

    protected double customGetFollowRange() {
        return this.mob.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE);
    }

}
