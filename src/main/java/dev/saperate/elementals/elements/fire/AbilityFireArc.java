package dev.saperate.elementals.elements.fire;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.elements.water.WaterElement;
import dev.saperate.elementals.entities.fire.FireArcEntity;
import dev.saperate.elementals.entities.fire.FireBallEntity;
import dev.saperate.elementals.entities.water.WaterArcEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.joml.Vector3f;

import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;

public class AbilityFireArc implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerEntity player = bender.player;

        Vector3f pos = getEntityLookVector(player, 3).toVector3f();

        FireArcEntity entity = new FireArcEntity(player.getWorld(), player, pos.x, pos.y, pos.z);
        bender.abilityData = entity;
        entity.setIsBlue(PlayerData.get(player).canUseUpgrade("blueFire"));
        entity.createChain(player);
        player.getWorld().spawnEntity(entity);

        bender.setCurrAbility(this);

    }

    @Override
    public void onLeftClick(Bender bender, boolean started) {
        FireArcEntity entity = (FireArcEntity) bender.abilityData;
        onRemove(bender);
        if(entity == null){
            return;
        }
        PlayerData plrData = PlayerData.get(bender.player);

        float speed = 0.75f;
        if (plrData.canUseUpgrade("fireBallSpeedII")) {
            speed = 1.25f;
        } else if (plrData.canUseUpgrade("fireBallSpeedI")) {
            speed = 1f;
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
        FireArcEntity entity = (FireArcEntity) bender.abilityData;
        if (entity == null) {
            return;
        }
        entity.setControlled(false);
        //TODO check each time i do this stupid ass cast and remove it
        Bender.getBender((PlayerEntity) entity.getOwner()).setCurrAbility(null);
    }

}
