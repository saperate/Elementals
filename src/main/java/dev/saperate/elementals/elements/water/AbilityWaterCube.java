package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.water.WaterCubeEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.joml.Vector3f;

import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;

public class AbilityWaterCube extends Ability {
    public WaterCubeEntity entity;

    @Override
    public void onCall(Bender bender) {
        PlayerEntity player = bender.player;
        Vector3f pos = WaterElement.canBend(player);

        if (pos != null) {
            entity = new WaterCubeEntity(player.getWorld(), player, pos.x, pos.y, pos.z);
            player.getWorld().spawnEntity(entity);

            bender.setCurrAbility(this);
        }
    }


    @Override
    public void onLeftClick(Bender bender) {
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
