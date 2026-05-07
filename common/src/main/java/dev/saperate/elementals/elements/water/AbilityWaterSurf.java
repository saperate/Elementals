package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3f;

import java.util.Random;

import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.serverSummonParticles;

public class AbilityWaterSurf implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        bender.setCurrAbility(this);
        bender.abilityData = false; //have we surfed yet or are we surfing
    }

    @Override
    public void onMiddleClick(Bender bender, boolean started) {
        onRemove(bender);
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

        if(bender.player.isCrouching()){
            onRemove(bender);
        }

        Player player = bender.player;
        float power = 1.35f;
        PlayerData plrData = PlayerData.get(player);
        if (plrData.canUseUpgrade("waterSurfSpeedI")) {
            power = 1.65f;
        }else if (plrData.canUseUpgrade("waterSurfSpeedII")) {
            power = 1.5f;
        }
        
        serverSummonParticles((ServerLevel) player.level(),
                ParticleTypes.SPLASH, player, player.getRandom(),
                0d, 0.1d, 0d,
                0.1d, 4,
                0f, -0.5f, 0f, 0f);
        if(player.isInWater() && !player.isUnderWater()){
            //TODO maybe make an upgrade that makes it also work in the rain
            movePlayer(player,bender,power,0);

            serverSummonParticles((ServerLevel) player.level(),
                    ParticleTypes.CLOUD, player, player.getRandom(),
                    0, 0.1f, 0,
                    0.1f, 1,
                    0, -0.5f, 0, 0);
        } else if (player.isUnderWater()) {
            player.startFallFlying();
            movePlayer(player,bender,power,1);

            serverSummonParticles((ServerLevel) player.level(),
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

    private void movePlayer(Player player, Bender bender, float power, int yMult){
        Vector3f velocity = getEntityLookVector(player, 2)
                .subtract(player.getEyePosition()).multiply(1,yMult,1)
                .normalize().scale(power).toVector3f();
        player.setDeltaMovement(velocity.x, velocity.y, velocity.z);
        player.hurtMarked = true;
        player.move(MoverType.PLAYER, player.getDeltaMovement());
        bender.abilityData = true;
    }

}
