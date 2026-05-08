package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.water.WaterBulletEntity;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3f;

public class AbilityWaterBullet implements Ability {
    private static final int bulletCount = 20;
    @Override
    public void onCall(Bender bender, long deltaT) {
        Player player = bender.player;
        Vector3f pos = WaterElement.canBend(player,true);

        if (pos != null) {
            WaterBulletEntity[] bullets = new WaterBulletEntity[bulletCount];
            for (int i = 0; i < bulletCount; i++) {
                WaterBulletEntity entity = new WaterBulletEntity(player.level(), player, pos.x, pos.y, pos.z);
                entity.setArrayId(i);
                entity.setArraySize(bulletCount);
                bullets[i] = entity;

                player.level().addFreshEntity(entity);
            }
            bender.abilityData = bullets;
            bender.setCurrAbility(this);
        }else{
            bender.setCurrAbility(null);
        }
    }


    @Override
    public void onLeftClick(Bender bender, boolean started) {
        if(started) {
            return;
        }
        WaterBulletEntity[] bullets = (WaterBulletEntity[]) bender.abilityData;

        if(bullets.length == 1){
            onRemove(bender);
        }

        WaterBulletEntity bullet = bullets[bullets.length - 1];
        bullet.setControlled(false);
        bullet.setDeltaMovement(bender.player, bender.player.getXRot(), bender.player.getYRot(), 0, 1, 0);

        WaterBulletEntity[] newArray = new WaterBulletEntity[bullets.length - 1];
        for (int i = 0; i < bullets.length - 1; i++) {
            bullets[i].setArraySize(bullets.length - 1);
            newArray[i] = bullets[i];
        }
        bender.abilityData = newArray;
    }

    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
    }

}
