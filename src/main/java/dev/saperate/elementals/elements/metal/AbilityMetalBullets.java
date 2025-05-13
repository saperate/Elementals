package dev.saperate.elementals.elements.metal;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.metal.MetalBulletEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;

public class AbilityMetalBullets implements Ability {//TODO make it so you can hold left click for full auto

    @Override
    public void onCall(Bender bender, long deltaT) {
        if (!bender.reduceChi(15)) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }
        PlayerEntity player = bender.player;

        Vec3d pos = getEntityLookVector(player, 2);
        PlayerData plrData = PlayerData.get(bender.player);

        int bulletCount = 20;//TODO basic count w/out metal (standing on earth), enhanced by sacrificing ingots
        if (plrData.canUseUpgrade("airBulletsCountII")) {//fixme
            bulletCount = 20;
        } else if (plrData.canUseUpgrade("airBulletsCountI")) {
            bulletCount = 10;
        }

        MetalBulletEntity[] bullets = new MetalBulletEntity[bulletCount];
        for (int i = 0; i < bulletCount; i++) {
            MetalBulletEntity entity = new MetalBulletEntity(player.getWorld(), player, pos.x, pos.y, pos.z);
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
        if (started) {
            return;
        }
        FireBullet(bender);
    }

    @Override
    public void onTick(Bender bender) {
        if(bender.isHolding(0,4) && bender.player.age % 3 == 0){
            FireBullet(bender);
        }
    }

    @Override
    public void onRemove(Bender bender) {
        MetalBulletEntity[] bullets = (MetalBulletEntity[]) bender.abilityData;
        if(bullets != null){
            for (MetalBulletEntity bullet : bullets){
                bullet.kill();
            }
        }
        bender.setCurrAbility(null);
    }

    private void FireBullet(Bender bender) {
        MetalBulletEntity[] bullets = (MetalBulletEntity[]) bender.abilityData;

        if (bullets.length == 1) {
            onRemove(bender);
        }

        MetalBulletEntity bullet = bullets[bullets.length - 1];
        bullet.setControlled(false);
        PlayerData plrData = PlayerData.get(bender.player);

        float speed = 1;
        if (plrData.canUseUpgrade("airBulletsSpeedII")) {//fixme
            speed = 2;
        } else if (plrData.canUseUpgrade("airBulletsSpeedI")) {
            speed = 1.5f;
        }
        bullet.setVelocity(bender.player, bender.player.getPitch(), bender.player.getYaw(), 0, speed, 0);

        MetalBulletEntity[] newArray = new MetalBulletEntity[bullets.length - 1];
        for (int i = 0; i < bullets.length - 1; i++) {
            bullets[i].setArraySize(bullets.length - 1);
            newArray[i] = bullets[i];
        }
        bender.abilityData = newArray;
    }
}
