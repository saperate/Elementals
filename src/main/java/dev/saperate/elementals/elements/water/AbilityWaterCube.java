package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.water.WaterCubeEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.vec3fToBlockPos;

public class AbilityWaterCube extends Ability {
    public WaterCubeEntity entity;

    @Override
    public void onCall(PlayerEntity player) {
        BlockHitResult hit = (BlockHitResult) player.raycast(5, 0, true);
        BlockState blockState = player.getEntityWorld().getBlockState(hit.getBlockPos());

        if (hit.getType() == HitResult.Type.BLOCK && blockState.getBlock().equals(Blocks.WATER)) {
            if (blockState.get(IntProperty.of("level", 0, 15)).equals(0)) {


                Vector3f pos = getEntityLookVector(player, 3);
                player.getWorld().setBlockState(hit.getBlockPos(), Blocks.AIR.getDefaultState());

                entity = new WaterCubeEntity(player.getWorld(), player, pos.x, pos.y, pos.z);
                player.getWorld().spawnEntity(entity);

                Bender.getBender(player).setCurrAbility(this);
            }
        }
    }


    @Override
    public void onLeftClick() {
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
    public void onMiddleClick() {

    }

    @Override
    public void onRightClick() {
        if (entity == null) {
            return;
        }
        entity.setControlled(false);
        Bender.getBender((PlayerEntity) entity.getOwner()).setCurrAbility(null);
    }
}
