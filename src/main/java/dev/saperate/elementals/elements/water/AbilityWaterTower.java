package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.water.WaterCubeEntity;
import dev.saperate.elementals.entities.water.WaterJetEntity;
import dev.saperate.elementals.entities.water.WaterTowerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
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
        if (!bender.reduceChi(15)) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }


        if (bender.isAbilityInBackground(this)) {
            onRemove(bender);
        } else {
            bender.setCurrAbility(null);
            PlayerEntity player = bender.player;

            if (player.isTouchingWaterOrRain()) {
                player.addVelocity(0, 1, 0);
                player.velocityModified = true;
                player.move(MovementType.PLAYER, player.getVelocity());
            }

            WaterTowerEntity entity = new WaterTowerEntity(player.getWorld(), player);
            entity.setOwnerCouldFly(player.getAbilities().allowFlying);
            bender.abilityData = entity;
            bender.addBackgroundAbility(this, makeAbilityData(entity, player.getAbilities().allowFlying));

            int height = 10;
            PlayerData plrData = PlayerData.get(player);
            if (plrData.canUseUpgrade("waterTowerRangeI")) {
                height = 15;
            }
            entity.setMaxTowerHeight(height);

            player.getWorld().spawnEntity(entity);
        }
    }

    @Override
    public void onBackgroundTick(Bender bender, Object data) {
        if (!bender.reduceChi(0.1f)) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }

        PlayerEntity player = bender.player;
        WaterTowerEntity entity = getEntity(bender);

        int height = 10;
        PlayerData plrData = PlayerData.get(player);
        if (plrData.canUseUpgrade("waterTowerRangeI")) {
            height = 15;
        }

        BlockHitResult hit = raycastBlockCustomRotation(player, height, true, new Vec3d(0, -1, 0));

        boolean isAir = hit == null;

        if (!player.isSubmergedInWater() && (player.isOnGround()
                || entity.getY() - 0.25f > player.getY()
                || (!isAir && !WaterElement.isBlockBendable(hit.getBlockPos(), player.getWorld(), false, plrData.canUseUpgrade("waterPickupEfficiencyI"))))) {
            onRemove(bender);
            return;
        }

        if (entity == null) {
            return;
        }
        entity.setPosition(hit.getPos());
    }

    @Override
    public void onRemove(Bender bender) {
        WaterTowerEntity entity = getEntity(bender);
        bender.removeAbilityFromBackground(this);
        if (entity == null) {
            return;
        }
        entity.discard();
    }

    public WaterTowerEntity getEntity(Bender bender) {
        return ((WaterTowerEntity) ((Object[]) bender.getBackgroundAbilityData(this))[0]);
    }

    public boolean getFlyAbility(Bender bender) {
        return ((boolean) ((Object[]) bender.getBackgroundAbilityData(this))[1]);
    }

    public Object[] makeAbilityData(WaterTowerEntity entity, boolean flyAbility) {
        return new Object[]{entity, flyAbility};
    }
}
