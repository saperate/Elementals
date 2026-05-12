package dev.saperate.elementals.elements.earth;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.effects.ElementalsStatusEffects;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.earth.EarthBlockEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;


import static dev.saperate.elementals.utils.SapsUtils.raycastFull;

public class AbilityEarthTrap implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {

        Player player = bender.player;

        HitResult hit = raycastFull(player,12,false);
        if(hit == null || !hit.getType().equals(HitResult.Type.ENTITY)){
            bender.setCurrAbility(null);
            return;
        }

        EntityHitResult eHit = (EntityHitResult) hit;
        BlockState state = player.level().getBlockState(eHit.getEntity().getOnPos().below());
        if (eHit.getEntity() instanceof LivingEntity victim && EarthElement.isBlockBendable(state, bender)) {
            EarthBlockEntity block = new EarthBlockEntity(player.level(), player, victim.getX(), victim.getY(), victim.getZ());
            bender.abilityData = block;
            block.setBlockState(state);
            block.setModelShapeId(2);
            player.level().addFreshEntity(block);
            if (!bender.reduceChi(5)) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }

            bender.abilityData = new Object[]{victim, block};
            bender.setCurrAbility(this);
            return;
        }


        bender.setCurrAbility(null);
    }

    @Override
    public void onTick(Bender bender) {
        if(bender.abilityData == null){
            onRemove(bender);
            return;
        }

        if (!bender.reduceChi(0.25f)) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }

        Object[] vars = ((Object[]) bender.abilityData);

        LivingEntity victim = ((LivingEntity) vars[0]);
        EarthBlockEntity block = ((EarthBlockEntity) vars[1]);

        double distance = victim
                .position().subtract(bender.player.position()).length();

        block.setTargetPosition(victim.position().toVector3f());
        bender.player.addEffect(new MobEffectInstance(ElementalsStatusEffects.STATIONARY.get(), 5, 1, false, false, false));
        victim.addEffect(new MobEffectInstance(ElementalsStatusEffects.STATIONARY.get(), 60, 1, false, false, true));


        if (!bender.player.isCrouching()
                || victim.isRemoved()
                || distance > 15) {
            block.discard();
            onRemove(bender);
        }
    }

    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
        bender.abilityData = null;
    }

}
