package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.effects.ElementalsStatusEffects;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.water.WaterShieldEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

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
        Player player = bender.player;

        if(WaterElement.canBend(player,true) == null){
            bender.setCurrAbility( null);
            return;
        }

        WaterShieldEntity entity = new WaterShieldEntity(player.level(), player, player.getX(), player.getY(), player.getZ());
        bender.abilityData = entity;
        player.level().addFreshEntity(entity);


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
        bender.player.addEffect(new MobEffectInstance(ElementalsStatusEffects.STATIONARY.get(),1,1,false,false,false));
        if(!bender.player.isCrouching()){
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
