package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.water.WaterCubeEntity;
import dev.saperate.elementals.entities.water.WaterJetEntity;
import dev.saperate.elementals.entities.water.WaterTowerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import static dev.saperate.elementals.effects.StationaryStatusEffect.STATIONARY_EFFECT;
import static dev.saperate.elementals.utils.SapsUtils.*;

public class AbilityWaterTower implements Ability {

    //TODO add a method which is like on keypress or smth so when one of the 4 key is pressed it calls here
    //TODO could be useful for more intuitive uncast
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerEntity player = bender.player;


        if (true) {//If below us is water or we are touching water
            WaterTowerEntity entity = new WaterTowerEntity(player.getWorld(), player);
            bender.abilityData = entity;
            entity.setMaxTowerHeight(10);

            player.getWorld().spawnEntity(entity);
            bender.setCurrAbility(this);
        } else {
            bender.setCurrAbility(null);
        }
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
        PlayerEntity player = bender.player;

        BlockHitResult hit = raycastBlockCustomRotation(player, 12, true, new Vec3d(0, -1, 0));

        boolean isAir = hit == null;

        if (!player.isSubmergedInWater() && (player.isOnGround()
                || ((WaterTowerEntity) bender.abilityData).getY() - 0.25f > player.getY()
                || (!isAir && !WaterElement.isBlockBendable(hit.getBlockPos(), player.getWorld(), false)))) {
            //TODO we could make it so players can bend while using this move by delegating this part to the entity
            //TODO we could even make an upgrade that switches between the two behaviors
            onRemove(bender);
            return;
        }
        WaterTowerEntity entity = (WaterTowerEntity) bender.abilityData;
        if (entity == null) {
            return;
        }
        entity.setPosition(hit.getPos());
    }

    @Override
    public void onRemove(Bender bender) {
        WaterTowerEntity entity = (WaterTowerEntity) bender.abilityData;
        bender.setCurrAbility(null);
        bender.abilityData = null;
        if (entity == null) {
            return;
        }
        entity.discard();
    }

}
