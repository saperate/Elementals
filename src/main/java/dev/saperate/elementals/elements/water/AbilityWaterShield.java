package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.effects.ElementalsStatusEffects;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.fire.FireShieldEntity;
import dev.saperate.elementals.entities.water.WaterBulletEntity;
import dev.saperate.elementals.entities.water.WaterShieldEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;


public class AbilityWaterShield implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        if (!bender.reduceChi(5)) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }
        PlayerEntity player = bender.player;

        if(WaterElement.canBend(player,true) == null){
            bender.setCurrAbility( null);
            return;
        }

        WaterShieldEntity entity = new WaterShieldEntity(player.getWorld(), player, player.getX(), player.getY(), player.getZ());
        bender.abilityData = entity;
        player.getWorld().spawnEntity(entity);


        bender.setCurrAbility(this);
    }


    @Override
    public void onTick(Bender bender) {
        if (!bender.reduceChi(0.15f)) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }
        bender.player.addStatusEffect(new StatusEffectInstance(ElementalsStatusEffects.STATIONARY,1,1,false,false,false));
        if(!bender.player.isSneaking()){
            onRemove(bender);
        }
    }

    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
        WaterShieldEntity entity = (WaterShieldEntity) bender.abilityData;
        if (entity == null) {
            return;
        }
        entity.discard();
    }

}
