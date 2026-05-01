package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.fire.FireShieldEntity;
import dev.saperate.elementals.entities.water.WaterHelmetEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;


public class AbilityWaterHelmet implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        if (!bender.reduceChi(25)) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }
        bender.setCurrAbility(null);
        PlayerEntity player = bender.player;
        PlayerData plrData = PlayerData.get(player);

        WaterHelmetEntity entity = new WaterHelmetEntity(player.getWorld(), player, player.getX(), player.getY(), player.getZ());
        player.getWorld().spawnEntity(entity);
        entity.setStealthy(plrData.canUseUpgrade("waterHelmetStealth"));

        if(plrData.canUseUpgrade("waterHelmetMastery")){
            entity.maxLifeTime = -1;
        } else if (plrData.canUseUpgrade("waterHelmetDurationIV")) {
            entity.maxLifeTime = 6000;
        } else if (plrData.canUseUpgrade("waterHelmetDurationIII")) {
            entity.maxLifeTime = 4800;
        } else if (plrData.canUseUpgrade("waterHelmetDurationII")) {
            entity.maxLifeTime = 3600;
        }  else if (plrData.canUseUpgrade("waterHelmetDurationI")) {
            entity.maxLifeTime = 2400;
        }
    }


    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
    }

}
