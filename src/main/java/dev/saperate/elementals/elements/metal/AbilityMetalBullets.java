package dev.saperate.elementals.elements.metal;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.metal.MetalBulletEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;

public class AbilityMetalBullets implements Ability {

    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerEntity player = bender.player;
        if (!bender.reduceChi(15) || !MetalElement.canBend(player, 16)) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }

        Vec3d pos = getEntityLookVector(player, 2);
        PlayerData plrData = PlayerData.get(bender.player);

        int bulletCount = 20;
        if (plrData.canUseUpgrade("airBulletsCountII")) {//fixme
            bulletCount = 15;
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
    public void onMiddleClick(Bender bender, boolean started) {//Buckshot
        MetalBulletEntity[] bullets = (MetalBulletEntity[]) bender.abilityData;
        if(started || bullets == null){
            return;
        }

        for (MetalBulletEntity bullet : bullets) {
            float speed = getBulletSpeed(bender.plrData) * 2;
            bullet.setControlled(false);
            bullet.setVelocity(bender.player, bender.player.getPitch(), bender.player.getYaw(), 0, speed, 10);
            bullet.setDamageMultiplier(2.5f);//FIXME
        }
        bender.abilityData = null;
        onRemove(bender);
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

        assert bullets != null;
        MetalBulletEntity bullet = bullets[bullets.length - 1];
        bullet.setControlled(false);
        PlayerData plrData = PlayerData.get(bender.player);

        float speed = getBulletSpeed(plrData);
        bullet.setVelocity(bender.player, bender.player.getPitch(), bender.player.getYaw(), 0, speed, 0);

        if (bullets.length == 1) {
            bender.abilityData = null; // Prevents onRemove from killing bullets
            onRemove(bender);
            return;
        }
        
        MetalBulletEntity[] newArray = new MetalBulletEntity[bullets.length - 1];
        for (int i = 0; i < bullets.length - 1; i++) {
            bullets[i].setArraySize(bullets.length - 1);
            newArray[i] = bullets[i];
        }
        bender.abilityData = newArray;
    }

    private static float getBulletSpeed(PlayerData plrData) {
        float speed = 2;
        if (plrData.canUseUpgrade("airBulletsSpeedII")) {//fixme
            speed = 2;
        } else if (plrData.canUseUpgrade("airBulletsSpeedI")) {
            speed = 1.5f;
        }
        return speed;
    }
}
