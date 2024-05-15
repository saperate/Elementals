package dev.saperate.elementals.elements.earth;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.water.WaterTowerEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.raycastBlockCustomRotation;

public class AbilityEarthJump implements Ability {

    @Override
    public void onCall(Bender bender, long deltaT) {
        bender.setCurrAbility(null);
        PlayerEntity player = bender.player;

        BlockHitResult hit = raycastBlockCustomRotation(player, 12, true, new Vec3d(0, -1, 0));

        if(!EarthElement.isBlockBendable(player.getWorld().getBlockState(hit.getBlockPos()))){
            return;
        }
        Vector3f velocity = getEntityLookVector(player, 1)
                .subtract(player.getEyePos())
                .normalize().multiply(2).toVector3f();

        player.setVelocity(velocity.x,
                velocity.y > 0 ? Math.min(velocity.y,1) : Math.max(velocity.y,-1),
                velocity.z);
        player.velocityModified = true;
        player.move(MovementType.PLAYER, player.getVelocity());
    }

    @Override
    public void onLeftClick(Bender bender, boolean started) {

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

    }

}
