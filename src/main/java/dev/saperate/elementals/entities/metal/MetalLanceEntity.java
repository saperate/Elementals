package dev.saperate.elementals.entities.metal;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import dev.saperate.elementals.misc.FireExplosion;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.joml.Vector3f;

import static dev.saperate.elementals.entities.ElementalEntities.METALLANCE;

public class MetalLanceEntity extends AbstractElementalsEntity<LivingEntity> {
    public MetalLanceEntity(EntityType type, World world) {
        super(type, world, LivingEntity.class);
    }

    public MetalLanceEntity(World world, LivingEntity owner, double x, double y, double z) {
        super(METALLANCE, world, LivingEntity.class);
        setOwner(owner);
        setPos(x, y, z);
        setControlled(true);
    }

    @Override
    public void tick() {
        super.tick();
        LivingEntity owner = getOwner();

        if (owner == null) {
            return;
        }
        Vec3d lookPos;

        if (!getIsControlled()) {
            //TODO rotate with velocity

            move(MovementType.SELF,getVelocity());
            return;
        }
        lookPos = SapsUtils.getEntityLookVector(owner, 1);
        float pitchCorrection = SapsUtils.isLookingForwards(lookPos
                .subtract(owner.getPos()).toVector3f()) ? -1 : 1;
        setRotation(owner.getBodyYaw(), owner.getPitch() * pitchCorrection);

        Vector3f goal = owner.getPos().toVector3f()
                .add(0,owner.getHeight() + 0.6f,0);
        
        Vec3d perpendicularVec;

        float pitch = getPitch() * pitchCorrection;
        if(pitch > -80 && pitch < 70){
            perpendicularVec = new Vec3d(0,1,0);
        } else if (pitch < 0) {
            perpendicularVec = new Vec3d(0.25,0,0.25);
        }else{
            perpendicularVec = new Vec3d(0.7,0,0.7);
        }

        goal = goal.add(lookPos.subtract(owner.getPos())
                .crossProduct(perpendicularVec)
                .toVector3f().mul(0.6f));
        
        moveEntityTowardsGoal(goal);
        move(MovementType.SELF,getVelocity());
    }

    @Override
    public void collidesWithGround() {
       remove(RemovalReason.KILLED);
    }

    @Override
    public void onRemoved() {
        if(getRemovalReason().equals(RemovalReason.DISCARDED)){
            //TODO add sound
        }
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }
}
