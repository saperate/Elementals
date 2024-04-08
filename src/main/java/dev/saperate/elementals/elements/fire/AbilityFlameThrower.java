package dev.saperate.elementals.elements.fire;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EggEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import static dev.saperate.elementals.utils.SapsUtils.*;

public class AbilityFlameThrower implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        bender.abilityData = true;
        bender.setCurrAbility(this);
    }

    @Override
    public void onLeftClick(Bender bender, boolean started) {

    }

    @Override
    public void onMiddleClick(Bender bender, boolean started) {

    }

    @Override
    public void onRightClick(Bender bender, boolean started) {
        if (started) {
            bender.abilityData = false;
        } else {
            bender.setCurrAbility(null);
        }
    }

    @Override
    public void onTick(Bender bender) {
        if(bender.abilityData == null){
            bender.setCurrAbility(null);
            return;
        }

        PlayerEntity player = bender.player;
        if(bender.abilityData.equals(true)){
            serverSummonParticles((ServerWorld) player.getWorld(),
                    PlayerData.get(player).canUseUpgrade("blueFire") ?
                            ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME,player,player.getRandom(),
                    0,0.1f,0,
                    0.1f,1,
                    0,0,0, 0);
        }else{
            Vector3f pos = getEntityLookVector(player,3).sub(player.getPos().toVector3f()).normalize(3);

            serverSummonParticles((ServerWorld) player.getWorld(),
                    PlayerData.get(player).canUseUpgrade("blueFire") ?
                            ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME,player,player.getRandom(),
                    pos.x - 0.5f, pos.y - 1.1f, pos.z - 0.5f,
                    0.1f,8,
                    0,0,0, 2);
        }
    }
}
