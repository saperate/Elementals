package dev.saperate.elementals.elements.fire;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.EggEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.util.List;

import static dev.saperate.elementals.utils.SapsUtils.*;

public class AbilityFlameThrower implements Ability {
    public static final Box boundingBox = new Box(new Vec3d(-1,-1,-1), new Vec3d(1,1,1));
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
            onRemove(bender);
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
            Vector3f pos = getEntityLookVector(player,3).subtract(player.getPos()).normalize().multiply(3).toVector3f();


            serverSummonParticles((ServerWorld) player.getWorld(),
                    PlayerData.get(player).canUseUpgrade("blueFire") ?
                            ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME,player,player.getRandom(),
                    pos.x - 1,
                    pos.y - 1.6f,
                    pos.z - 1,
                    0.05f + player.getMovementSpeed(), 8,
                    0,0,0, 2);

            List<Entity> hits = player.getWorld().getEntitiesByClass(Entity.class,
                     boundingBox.expand(12).offset(player.getPos()),
                    Entity::isAlive);

            for (Entity e : hits){
                if(e.equals(player)){
                    continue;
                }
                Vector3f dir = player.getPos().subtract(e.getPos()).toVector3f();
                if(dir.length() > 6){
                    continue;
                }
                dir = dir.normalize();
                float dot = -pos.normalize().dot(dir);


                if(Math.cos(dot) <= 0.75){
                    if (!e.isFireImmune()) {
                        e.setOnFireFor(8);
                        e.damage(e.getDamageSources().inFire(), PlayerData.get(player).canUseUpgrade("blueFire") ? 1.5f : 1.25f);
                    }
                }
            }
        }
    }

    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
    }
}
