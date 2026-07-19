package dev.saperate.elementals.elements.fire;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.effects.ElementalsStatusEffects;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.fire.FireShieldEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

public class AbilityFireShield implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        if (!PlayerData.get(bender.player).canUseUpgrade("fireShield")) {
            bender.setCurrAbility(null);
            return;
        }
        if (!bender.reduceChi(5)) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }
        Player player = bender.player;

        FireShieldEntity entity = new FireShieldEntity(player.level(), player, player.getX(), player.getY(), player.getZ());
        bender.abilityData = entity;
        entity.setIsBlue(PlayerData.get(player).canUseUpgrade("blueFire"));
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
        if(!bender.player.isShiftKeyDown()){
            onRemove(bender);
        }
    }

    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
        FireShieldEntity entity = (FireShieldEntity) bender.abilityData;
        if (entity == null) {
            return;
        }
        entity.discard();
    }

}