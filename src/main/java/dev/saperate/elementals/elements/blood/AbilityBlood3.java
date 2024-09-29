package dev.saperate.elementals.elements.blood;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;

import static dev.saperate.elementals.effects.BurnoutStatusEffect.BURNOUT_EFFECT;
import static dev.saperate.elementals.effects.OverchargedStatusEffect.OVERCHARGED_EFFECT;
import static dev.saperate.elementals.utils.SapsUtils.safeHasStatusEffect;


public class AbilityBlood3 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerEntity player = bender.player;
        PlayerData playerData = PlayerData.get(player);


        if (deltaT >= 500 && !safeHasStatusEffect(OVERCHARGED_EFFECT, player) && !safeHasStatusEffect(BURNOUT_EFFECT, player) ) {
            if (!bender.reduceChi(15)) {
                return;
            }
            player.addStatusEffect(new StatusEffectInstance(OVERCHARGED_EFFECT, 400, 1, false, false, true));
        }else if(player.isOnGround()){
            SapsUtils.launchEntity(player,2);
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

    }

    @Override
    public void onRemove(Bender bender) {

    }

}
