package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.water.WaterBladeEntity;
import dev.saperate.elementals.entities.water.WaterCubeEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.joml.Vector3f;

public class AbilityWaterBlade implements Ability {

    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerEntity player = bender.player;
        Vector3f pos = WaterElement.canBend(player, true);

        if (pos != null) {
            if (!bender.reduceChi(15)) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }
            WaterBladeEntity entity = new WaterBladeEntity(player.getWorld(), player, pos.x, pos.y, pos.z);
            bender.abilityData = entity;
            player.getWorld().spawnEntity(entity);

            PlayerData plrData = PlayerData.get(player);
            if (plrData.canUseUpgrade("waterBladeDamageI")) {
                entity.setDamage(2);
            }

            bender.setCurrAbility(this);
        } else {
            bender.setCurrAbility(null);
        }
    }


    @Override
    public void onLeftClick(Bender bender, boolean started) {
        WaterBladeEntity entity = (WaterBladeEntity) bender.abilityData;
        if (entity == null) {
            onRemove(bender);
            return;
        }
        onRemove(bender);
        float speed = 1;
        PlayerData plrData = PlayerData.get(bender.player);
        if (plrData.canUseUpgrade("waterBladeSpeedII")) {
            speed = 2;
        } else if (plrData.canUseUpgrade("waterBladeSpeedI")) {
            speed = 1.5f;
        }
        entity.setVelocity(bender.player, bender.player.getPitch(), bender.player.getYaw(), 0, speed, 0);
    }

    @Override
    public void onMiddleClick(Bender bender, boolean started) {

    }

    @Override
    public void onRightClick(Bender bender, boolean started) {
        onRemove(bender);
    }

    @Override
    public void onTick(Bender bender) {

    }

    @Override
    public void onRemove(Bender bender) {
        WaterBladeEntity entity = (WaterBladeEntity) bender.abilityData;
        if (entity == null) {
            return;
        }
        entity.setControlled(false);
        bender.setCurrAbility(null);
    }

}
