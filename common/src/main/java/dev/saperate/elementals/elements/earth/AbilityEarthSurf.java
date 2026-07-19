package dev.saperate.elementals.elements.earth;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.earth.EarthBlockEntity;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVectorIgnorePitch;
import static dev.saperate.elementals.utils.SapsUtils.serverSummonParticles;

/**
 * Surfs on top of two conjured chips of whatever block the player is standing on
 * (sand, dirt, stone, etc.), skating forward faster while sprinting on the ground.
 * Mirrors the flow of {@link dev.saperate.elementals.elements.water.AbilityWaterSurf}.
 */
public class AbilityEarthSurf implements Ability {

    @Override
    public void onCall(Bender bender, long deltaT) {
        Player player = bender.player;

        if (!player.getRootVehicle().onGround()) {
            bender.setCurrAbility(null);
            return;
        }

        BlockState groundState = player.level().getBlockState(player.blockPosition().below());
        if (!EarthElement.isBlockBendable(groundState, bender)) {
            bender.setCurrAbility(null);
            return;
        }

        if (!bender.reduceChi(10)) {
            bender.setCurrAbility(null);
            return;
        }

        EarthBlockEntity[] skates = new EarthBlockEntity[]{
                spawnSkate(bender, groundState),
                spawnSkate(bender, groundState)
        };

        bender.abilityData = skates;
        bender.setCurrAbility(this);
    }

    private EarthBlockEntity spawnSkate(Bender bender, BlockState state) {
        Player player = bender.player;
        EarthBlockEntity entity = new EarthBlockEntity(player.level(), player, player.getX(), player.getY(), player.getZ());
        entity.setBlockState(state);
        entity.setModelShapeId(1); //small cosmetic shape, doesn't collide or drop as a real block
        entity.setCollidable(false);
        entity.setDamageOnTouch(false);
        entity.setDrops(false);
        entity.setUseOffset(false);
        entity.setMovementSpeed(0.9f);
        entity.maxLifeTime = -1; //we discard these manually in onRemove
        player.level().addFreshEntity(entity);
        return entity;
    }

    @Override
    public void onTick(Bender bender) {
        Player player = bender.player;

        if (!bender.reduceChi(0.2f)) {
            onRemove(bender);
            return;
        }

        if (!player.getRootVehicle().onGround() || !player.isSprinting() || player.isShiftKeyDown()) {
            onRemove(bender);
            return;
        }

        Object data = bender.abilityData;
        if (!(data instanceof EarthBlockEntity[] skates) || skates.length != 2
                || !skates[0].isAlive() || !skates[1].isAlive()) {
            onRemove(bender);
            return;
        }

        //Skating animation: the two blocks alternate side to side just under the player's feet
        double angleRad = Math.toRadians(player.getYRot() + 90);
        float sideOffset = 0.4f;
        float bob = (float) Math.sin(player.tickCount * 1.5) * 0.06f;

        Vec3 pos = player.position();
        skates[0].setTargetPosition(new Vector3f(
                (float) (pos.x + Math.cos(angleRad) * sideOffset),
                (float) pos.y + 0.05f + bob,
                (float) (pos.z + Math.sin(angleRad) * sideOffset)));
        skates[1].setTargetPosition(new Vector3f(
                (float) (pos.x - Math.cos(angleRad) * sideOffset),
                (float) pos.y + 0.05f - bob,
                (float) (pos.z - Math.sin(angleRad) * sideOffset)));

        PlayerData plrData = PlayerData.get(player);
        float power = plrData.canUseUpgrade("earthSurfSpeedI") ? 1.5f : 1.3f;

        Vec3 lookDir = getEntityLookVectorIgnorePitch(player, 2).subtract(player.getEyePosition());
        Vector3f velocity = lookDir.normalize().scale(power).toVector3f();

        player.setDeltaMovement(velocity.x, player.getDeltaMovement().y, velocity.z);
        player.hurtMarked = true;
        player.move(MoverType.PLAYER, player.getDeltaMovement());
        player.fallDistance = 0;

        serverSummonParticles((ServerLevel) player.level(),
                new BlockParticleOption(ParticleTypes.BLOCK, skates[0].getBlockState()),
                player, player.getRandom(),
                0, 0, 0,
                0.05, 3,
                0, -0.5f, 0, 0);
    }

    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
        Object data = bender.abilityData;
        bender.abilityData = null;
        if (data instanceof EarthBlockEntity[] skates) {
            for (EarthBlockEntity skate : skates) {
                if (skate != null) {
                    skate.discard();
                }
            }
        }
    }
}