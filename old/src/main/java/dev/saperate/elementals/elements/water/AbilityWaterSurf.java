package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.water.WaterArcEntity;
import net.minecraft.block.Blocks;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3f;

import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.serverSummonParticles;

public class AbilityWaterSurf implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        bender.setCurrAbility(this);
        bender.abilityData = false; //have we surfed yet or are we surfing
    }

    @Override
    public void onLeftClick(Bender bender, boolean started) {

    }

    @Override
    public void onMiddleClick(Bender bender, boolean started) {
        onRemove(bender);
    }

    @Override
    public void onRightClick(Bender bender, boolean started) {

    }

    @Override
    public void onTick(Bender bender) {
        if (!bender.reduceChi(0.25f)) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }

        if(bender.player.isSneaking()){
            onRemove(bender);
        }

        PlayerEntity player = bender.player;
        float power = 1.35f;
        PlayerData plrData = PlayerData.get(player);
        if (plrData.canUseUpgrade("waterSurfSpeedI")) {
            power = 1.65f;
        }else if (plrData.canUseUpgrade("waterSurfSpeedII")) {
            power = 1.5f;
        }
        serverSummonParticles((ServerWorld) player.getWorld(),
                ParticleTypes.SPLASH, player, player.getRandom(),
                0, 0.1f, 0,
                0.1f, 4,
                0, -0.5f, 0, 0);
        if(player.isTouchingWater() && !player.isSubmergedInWater()){
            //TODO maybe make an upgrade that makes it also work in the rain
            movePlayer(player,bender,power,0);

            serverSummonParticles((ServerWorld) player.getWorld(),
                    ParticleTypes.CLOUD, player, player.getRandom(),
                    0, 0.1f, 0,
                    0.1f, 1,
                    0, -0.5f, 0, 0);
        } else if (player.isSubmergedInWater()) {
            player.startFallFlying();
            movePlayer(player,bender,power,1);

            serverSummonParticles((ServerWorld) player.getWorld(),
                    ParticleTypes.BUBBLE, player, player.getRandom(),
                    0, 0.1f, 0,
                    0.1f, 5,
                    0, -0.5f, 0, 0);
        } else if (bender.abilityData.equals(true)) {
            onRemove(bender);
        }
    }

    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
    }

    private void movePlayer(PlayerEntity player, Bender bender, float power, int yMult){
        Vector3f velocity = getEntityLookVector(player, 2)
                .subtract(player.getEyePos()).multiply(1,yMult,1)
                .normalize().multiply(power).toVector3f();
        player.setVelocity(velocity.x, velocity.y, velocity.z);
        player.velocityModified = true;
        player.move(MovementType.PLAYER, player.getVelocity());
        bender.abilityData = true;
    }

}
