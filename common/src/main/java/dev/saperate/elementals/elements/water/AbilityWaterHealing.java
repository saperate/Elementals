package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3f;

public class AbilityWaterHealing implements Ability {

    @Override
    public void onCall(Bender bender, long deltaT) {
        if (!bender.reduceChi(30)) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }
        Player player = bender.player;
        Vector3f pos = WaterElement.canBend(player, true);

        if (pos != null) {
            WaterHealingEntity entity = new WaterHealingEntity(player.level(), player, pos.x, pos.y, pos.z);
            bender.abilityData = entity;
            player.level().addFreshEntity(entity);

            PlayerData plrData = PlayerData.get(player);
            if (plrData.canUseUpgrade("waterHealingEfficiencyII")) {
                entity.setHealing(4);
            } else if (plrData.canUseUpgrade("waterHealingEfficiencyI")) {
                entity.setHealing(2);
            }

            bender.setCurrAbility(this);
        } else {
            bender.setCurrAbility(null);
        }
    }


    @Override
    public void onLeftClick(Bender bender, boolean started) {
        WaterHealingEntity entity = (WaterHealingEntity) bender.abilityData;
        onRemove(bender);
        if (entity == null) {
            return;
        }

        entity.setVelocity(bender.player, bender.player.getXRot(), bender.player.getYRot(), 0, 1, 0);
    }

    @Override
    public void onRightClick(Bender bender, boolean started) {
        Player player = bender.player;
        if (WaterElement.tryStoreWater(player)) {
            WaterHealingEntity entity = (WaterHealingEntity) bender.abilityData;
            if (entity == null) {
                return;
            }
            entity.discard();
            bender.setCurrAbility(null);
            return;
        }
        onRemove(bender);
    }
    
    @Override
    public void onRemove(Bender bender) {
        WaterHealingEntity entity = (WaterHealingEntity) bender.abilityData;
        if (entity == null) {
            return;
        }
        entity.setControlled(false);
        bender.setCurrAbility(null);
    }

}
