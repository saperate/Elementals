package dev.saperate.elementals.elements.earth;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.earth.EarthBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

import static dev.saperate.elementals.effects.StationaryStatusEffect.STATIONARY_EFFECT;
import static dev.saperate.elementals.utils.SapsUtils.raycastFull;

public class AbilityEarthTrap implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {

        PlayerEntity player = bender.player;

        HitResult hit = raycastFull(player,12,false);
        if(hit == null || !hit.getType().equals(HitResult.Type.ENTITY)){
            bender.setCurrAbility(null);
            return;
        }

        EntityHitResult eHit = (EntityHitResult) hit;
        BlockState state = player.getWorld().getBlockState(eHit.getEntity().getBlockPos().down());
        if (eHit.getEntity() instanceof LivingEntity victim && EarthElement.isBlockBendable(state)) {
            EarthBlockEntity block = new EarthBlockEntity(player.getWorld(), player, victim.getX(), victim.getY(), victim.getZ());
            bender.abilityData = block;
            block.setBlockState(state);
            block.setModelShapeId(2);
            player.getWorld().spawnEntity(block);
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
                .getPos().subtract(bender.player.getPos()).length();

        block.setTargetPosition(victim.getPos().toVector3f());
        bender.player.addStatusEffect(new StatusEffectInstance(STATIONARY_EFFECT, 1, 1, false, false, false));
        victim.addStatusEffect(new StatusEffectInstance(STATIONARY_EFFECT, 1, 1, false, false, false));



        if (!bender.player.isSneaking()
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
