package dev.saperate.elementals.mixin;


import dev.saperate.elementals.entities.common.DecoyPlayerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.jetbrains.annotations.Nullable;
import java.util.function.Predicate;

@Mixin(NearestAttackableTargetGoal.class)
public abstract class NearestAttackableTargetGoalMixin<T extends LivingEntity> {

    @Shadow
    @Nullable
    protected LivingEntity target;

    @Shadow
    @Final
    protected Class<T> targetType;

    @Shadow
    protected abstract AABB getTargetSearchArea(double p_26069_);

    @Shadow
    protected TargetingConditions targetConditions;

    @Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/world/entity/Mob;Ljava/lang/Class;ZLjava/util/function/Predicate;)V")
    private void init(Mob mob, Class targetType, boolean mustSee, Predicate targetPredicate, CallbackInfo ci) {
        this.target = mob;
    }

    @Inject(at = @At("HEAD"), method = "findTarget", cancellable = true)
    private void findClosestTargetIncludeDecoys(CallbackInfo ci) {
        //TODO maybe add a config that disables this
        if(target == null){
            return;
        }
        if(targetType.equals(Player.class) || targetType.equals(ServerPlayer.class)) {
            Player plr = target.level().getNearestPlayer(this.targetConditions, this.target, this.target.getX(), this.target.getEyeY(), this.target.getZ());
            LivingEntity decoy = target.level().getNearestEntity(this.target.level().getEntitiesOfClass(DecoyPlayerEntity.class, getTargetSearchArea(this.elementals$customGetFollowRange()), livingEntity -> true), targetConditions, this.target, this.target.getX(), this.target.getEyeY(), this.target.getZ());
            if (plr == null) {
                if (decoy == null) {
                    ci.cancel();
                    return;
                } else {
                    target = decoy;
                }
            } else if (decoy == null) {
                target = plr;
            } else {
                target = plr.distanceToSqr(target) > decoy.distanceToSqr(target) ? decoy : plr;
            }
            ci.cancel();

        }
    }

    @Unique
    protected double elementals$customGetFollowRange() {
        if(this.target == null){
            return 0;
        }
        return this.target.getAttributeValue(Attributes.FOLLOW_RANGE);
    }

}
