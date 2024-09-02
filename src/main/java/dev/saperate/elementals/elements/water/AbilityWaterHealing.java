package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.water.WaterArcEntity;
import dev.saperate.elementals.entities.water.WaterCubeEntity;
import dev.saperate.elementals.entities.water.WaterHealingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
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
        PlayerEntity player = bender.player;
        Vector3f pos = WaterElement.canBend(player, true);

        if (pos != null) {
            WaterHealingEntity entity = new WaterHealingEntity(player.getWorld(), player, pos.x, pos.y, pos.z);
            bender.abilityData = entity;
            player.getWorld().spawnEntity(entity);

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

        entity.setVelocity(bender.player, bender.player.getPitch(), bender.player.getYaw(), 0, 1, 0);
    }

    @Override
    public void onMiddleClick(Bender bender, boolean started) {

    }

    @Override
    public void onRightClick(Bender bender, boolean started) {
        PlayerEntity player = bender.player;
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
    public void onTick(Bender bender) {

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
