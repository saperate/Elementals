package dev.saperate.elementals.elements.fire;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.fire.FireArcEntity;
import dev.saperate.elementals.entities.fire.FireShieldEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import org.joml.Vector3f;

import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;

public class AbilityFireShield implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerEntity player = bender.player;


        FireShieldEntity entity = new FireShieldEntity(player.getWorld(), player, player.getX(), player.getY(), player.getZ());
        bender.controlledEntity = entity;
        entity.setIsBlue(PlayerData.get(player).canUseUpgrade("blueFire"));
        player.getWorld().spawnEntity(entity);

        bender.setCurrAbility(this);

    }

    @Override
    public void onLeftClick(Bender bender) {

    }

    @Override
    public void onMiddleClick(Bender bender) {

    }

    @Override
    public void onRightClick(Bender bender) {

    }

    @Override
    public void onTick(Bender bender) {
        if(!bender.player.isSneaking()){

            bender.setCurrAbility(null);
            FireShieldEntity entity = (FireShieldEntity) bender.controlledEntity;
            if (entity == null) {
                return;
            }
            entity.discard();
        }
    }

}
