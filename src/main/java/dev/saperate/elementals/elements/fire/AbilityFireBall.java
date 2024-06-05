package dev.saperate.elementals.elements.fire;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.fire.FireArcEntity;
import dev.saperate.elementals.entities.fire.FireBallEntity;
import dev.saperate.elementals.entities.water.WaterCubeEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.joml.Vector3f;

import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;

public class AbilityFireBall implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerEntity player = bender.player;

        Vector3f pos = getEntityLookVector(player, 3).toVector3f();

        FireBallEntity entity = new FireBallEntity(player.getWorld(), player, pos.x, pos.y, pos.z);
        bender.abilityData = entity;
        entity.setIsBlue(PlayerData.get(player).canUseUpgrade("blueFire"));
        player.getWorld().spawnEntity(entity);

        bender.setCurrAbility(this);

    }

    @Override
    public void onLeftClick(Bender bender, boolean started) {
        FireBallEntity entity = (FireBallEntity) bender.abilityData;
        if(entity == null){
            throw new RuntimeException("Elementals: Tried to launch entity while having none!");
        }
        onRemove(bender);

        PlayerData plrData = PlayerData.get(bender.player);

        float speed = 1;
        if (plrData.canUseUpgrade("fireArcSpeedII")) {
            speed = 2;
        } else if (plrData.canUseUpgrade("fireArcSpeedI")) {
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
        FireBallEntity entity = (FireBallEntity) bender.abilityData;
        if (entity == null) {
            return;
        }
        entity.setControlled(false);
        bender.setCurrAbility(null);
    }
}
