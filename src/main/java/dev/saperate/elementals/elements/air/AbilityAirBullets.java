package dev.saperate.elementals.elements.air;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.elements.water.WaterElement;
import dev.saperate.elementals.entities.air.AirBulletEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;

public class AbilityAirBullets implements Ability {
    private int bulletCount = 20;
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerEntity player = bender.player;

        Vec3d pos = getEntityLookVector(player,2.85f);

            AirBulletEntity[] bullets = new AirBulletEntity[bulletCount];
            for (int i = 0; i < bulletCount; i++) {
                AirBulletEntity entity = new AirBulletEntity(player.getWorld(), player, pos.x, pos.y, pos.z);
                entity.setArrayId(i);
                entity.setArraySize(bulletCount);
                bullets[i] = entity;

                player.getWorld().spawnEntity(entity);
            }
            bender.abilityData = bullets;
            bender.setCurrAbility(this);
    }


    @Override
    public void onLeftClick(Bender bender, boolean started) {
        if(started) {
            return;
        }
        AirBulletEntity[] bullets = (AirBulletEntity[]) bender.abilityData;

        if(bullets.length == 1){
            onRemove(bender);
        }

        AirBulletEntity bullet = bullets[bullets.length - 1];
        bullet.setControlled(false);
        bullet.setVelocity(bender.player, bender.player.getPitch(), bender.player.getYaw(), 0, 1, 0);

        AirBulletEntity[] newArray = new AirBulletEntity[bullets.length - 1];
        for (int i = 0; i < bullets.length - 1; i++) {
            bullets[i].setArraySize(bullets.length - 1);
            newArray[i] = bullets[i];
        }
        bender.abilityData = newArray;
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
        bender.setCurrAbility(null);
    }

}