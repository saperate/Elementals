package dev.saperate.elementals.entities.metal;

import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import static dev.saperate.elementals.entities.ElementalEntities.METALLANCE;

public class MetalLanceEntity extends AbstractElementalsEntity<LivingEntity> {
    public MetalLanceEntity(EntityType type, Level world) {
        super(type, world, LivingEntity.class);
    }

    public MetalLanceEntity(Level world, LivingEntity owner, double x, double y, double z) {
        super(METALLANCE.get(), world, LivingEntity.class);
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
        Vec3 lookPos;
        if (!getIsControlled()) {
            //TODO rotate with velocity


            move(MoverType.SELF,getDeltaMovement());
            return;
        }
        lookPos = SapsUtils.getEntityLookVector(owner, 1);
        float pitchCorrection = SapsUtils.isLookingForwards(lookPos
                .subtract(owner.position()).toVector3f()) ? -1 : 1;
        setRot(owner.getYRot(), owner.getXRot() * pitchCorrection);

        Vector3f goal = owner.position().toVector3f()
                .add(0,owner.getEyeHeight() + 0.6f,0);
        
        Vec3 perpendicularVec;

        float pitch = getXRot() * pitchCorrection;
        if(pitch > -80 && pitch < 70){
            perpendicularVec = new Vec3(0,1,0);
        } else if (pitch < 0) {
            perpendicularVec = new Vec3(0.25,0,0.25);
        }else{
            perpendicularVec = new Vec3(0.7,0,0.7);
        }

        goal = goal.add(lookPos.subtract(owner.position())
                .cross(perpendicularVec)
                .toVector3f().mul(0.6f));
        
        moveEntityTowardsGoal(goal);
        move(MoverType.SELF,getDeltaMovement());
    }

    @Override
    public void collidesWithGround() {
        remove(RemovalReason.KILLED);
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
    }

    @Override
    public void onClientRemoval() {
        if(getRemovalReason().equals(RemovalReason.DISCARDED)){
            this.level().playSound(this, getOnPos(),
                    SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS,
                    0.25f,
                    (1.0f + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2f) * 0.7f);
        }
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }
}
