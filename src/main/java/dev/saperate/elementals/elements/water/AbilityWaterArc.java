package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.data.Bender;
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
        Vector3f pos = WaterElement.canBend(player);

        if (pos != null) {
            WaterArcEntity entity = new WaterArcEntity(player.getWorld(), player, pos.x, pos.y, pos.z);
            bender.controlledEntity = entity;
            entity.createChain(player);
            player.getWorld().spawnEntity(entity);

            bender.setCurrAbility(this);
        }else{
            bender.setCurrAbility(null);
        }
    }

    @Override
    public void onLeftClick(Bender bender) {
        WaterArcEntity entity = (WaterArcEntity) bender.controlledEntity;
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
        WaterArcEntity entity = (WaterArcEntity) bender.controlledEntity;
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
