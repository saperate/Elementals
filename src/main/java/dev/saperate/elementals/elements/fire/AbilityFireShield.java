package dev.saperate.elementals.elements.fire;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.fire.FireArcEntity;
import dev.saperate.elementals.entities.fire.FireShieldEntity;
import net.minecraft.entity.Entity;
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
        FireArcEntity entity = (FireArcEntity) bender.controlledEntity;
        if (entity == null) {
            return;
        }
        entity.setControlled(false);

        Entity owner = entity.getOwner();
        if (owner == null) {
            return;
        }
        entity.setVelocity(owner, owner.getPitch(), owner.getYaw(), 0, 1, 0);
        Bender.getBender((PlayerEntity) entity.getOwner()).setCurrAbility(null);
    }

    @Override
    public void onMiddleClick(Bender bender) {

    }

    @Override
    public void onRightClick(Bender bender) {
        FireArcEntity entity = (FireArcEntity) bender.controlledEntity;
        if (entity == null) {
            return;
        }
        entity.setControlled(false);
        Bender.getBender((PlayerEntity) entity.getOwner()).setCurrAbility(null);
    }

    @Override
    public void onTick(Bender bender) {

    }

}
