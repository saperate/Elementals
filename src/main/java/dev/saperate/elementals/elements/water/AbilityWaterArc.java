package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.water.WaterArcEntity;
import dev.saperate.elementals.entities.water.WaterCubeEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.joml.Vector3f;

public class AbilityWaterArc implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerEntity player = bender.player;
        Vector3f pos = WaterElement.canBend(player,true);

        if (pos != null) {
            WaterArcEntity entity = new WaterArcEntity(player.getWorld(), player, pos.x, pos.y, pos.z);
            bender.abilityData = entity;
            entity.createChain(player);
            player.getWorld().spawnEntity(entity);

            bender.setCurrAbility(this);
        } else {
            bender.setCurrAbility(null);
        }
    }

    @Override
    public void onLeftClick(Bender bender, boolean started) {
        WaterArcEntity entity = (WaterArcEntity) bender.abilityData;
        if(entity == null){
            throw new RuntimeException("Elementals: Tried to launch entity while having none!");
        }
        onRemove(bender);
        PlayerData plrData = PlayerData.get(bender.player);

        float speed = 1;
        if (plrData.canUseUpgrade("waterArcSpeedII")) {
            speed = 2;
        } else if (plrData.canUseUpgrade("waterArcSpeedI")) {
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
        WaterArcEntity entity = (WaterArcEntity) bender.abilityData;
        if (entity == null) {
            return;
        }
        entity.setControlled(false);
        bender.setCurrAbility(null);
    }

}
