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
    public static final Box boundingBox = new Box(new Vec3d(-3,-3,-3), new Vec3d(3,3,3));
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
                    pos.x - 1, pos.y - 1.1f, pos.z - 1,
                    0.1f,8,
                    0,0,0, 2);

            List<Entity> hits = player.getWorld().getEntitiesByClass(Entity.class,
                     boundingBox.offset(player.getPos()),
                    Entity::isAlive);

            for (Entity e : hits){
                if(e.equals(player)){
                    return;
                }
                Vector3f dir = player.getPos().subtract(e.getPos()).toVector3f().normalize();
                float dot = pos.normalize().dot(dir);
                System.out.println(dot);

                double angle = 180 - Math.toDegrees(Math.acos(dot));

                if(angle <= 45){
                    if (!e.isFireImmune()) {
                        e.setOnFireFor(8);
                        e.damage(e.getDamageSources().inFire(), PlayerData.get(player).canUseUpgrade("blueFire") ? 1.5f : 1.25f);
                    }
                }
            }
        }
    }
}
