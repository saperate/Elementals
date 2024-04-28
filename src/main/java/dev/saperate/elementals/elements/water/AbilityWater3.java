package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3f;

import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.serverSummonParticles;

public class AbilityWater3 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerData playerData = PlayerData.get(bender.player);

        if (bender.player.isSprinting() && playerData.canUseUpgrade("surf")
                && (bender.player.isOnGround() || bender.player.isSubmergedInWater())) {
            WaterElement.get().abilityList.get(13).onCall(bender, deltaT);
            return;
        }

        bender.setCurrAbility(this);
        bender.abilityData = -1;
    }

    @Override
    public void onLeftClick(Bender bender, boolean started) {
        PlayerEntity player = bender.player;
        if (player.isTouchingWaterOrRain()) {
            bender.abilityData = 1;

            Vector3f velocity = getEntityLookVector(player, 1)
                    .subtract(player.getEyePos())
                    .normalize().multiply(1.75f).toVector3f();

            player.setVelocity(velocity.x,
                    velocity.y > 0 ? Math.min(velocity.y, 1) : Math.max(velocity.y, -1),
                    velocity.z);
            player.velocityModified = true;
            player.move(MovementType.PLAYER, player.getVelocity());
        }
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
        serverSummonParticles((ServerWorld) player.getWorld(),
                ParticleTypes.SPLASH, player, player.getRandom(),
                0, 0.1f, 0,
                0.1f, 4,
                0, -0.5f, 0, 0);

        if (bender.abilityData == null || (player.isSneaking() && ((int) bender.abilityData) < 0)) {
            onRemove(bender);
            return;
        }

        //TODO add a system that if is not touching water search inventory for water containers
        if (!bender.player.isOnGround() && ((int) bender.abilityData) < 0) {
            WaterElement.get().abilityList.get(16).onCall(bender, -1);//dT doesn't matter here
        } else if (bender.abilityData.equals(1) && (bender.player.isOnGround() || bender.player.isSubmergedInWater())) {
            //Trigger when jump touches the ground, use for abilities
            onRemove(bender);
        }
    }

    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
        bender.abilityData = null;
    }

}
