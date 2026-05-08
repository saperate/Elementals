package dev.saperate.elementals.entities.metal;

import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import dev.saperate.elementals.entities.fire.FireArcEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.data.SynchedEntityData;
import net.minecraft.entity.data.EntityDataAccessor;
import net.minecraft.entity.data.EntityDataSerializers;
import net.minecraft.entity.player.Player;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3;
import net.minecraft.world.Level;
import org.joml.Vector3f;

import static dev.saperate.elementals.Elementals.LIGHTNING_PARTICLE_TYPE;
import static dev.saperate.elementals.entities.ElementalEntities.*;
import static dev.saperate.elementals.utils.SapsUtils.*;

public class MetalBindEntity extends AbstractElementalsEntity<LivingEntity> {
    private static final EntityDataAccessor<Boolean> FROZEN = SynchedEntityData.defineId(MetalBindEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> DISTANCE = SynchedEntityData.defineId(MetalBindEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> PARENT_ID = SynchedEntityData.defineId(MetalBindEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> CHILD_ID = SynchedEntityData.defineId(MetalBindEntity.class, EntityDataSerializers.INT);
    public int chainLength = 0;


    public MetalBindEntity(EntityType<MetalBindEntity> type, Level world) {
        super(type, world, LivingEntity.class);
    }

    public MetalBindEntity(Level world, LivingEntity owner, double x, double y, double z) {
        super(METALBIND, world, LivingEntity.class);
        setOwner(owner);
        setPos(x, y, z);

        setControlled(true);
    }
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(FROZEN, false);
        builder.define(DISTANCE, 10f);
        builder.define(PARENT_ID, 0);
        builder.define(CHILD_ID, 0);
    }

    public void createChain(LivingEntity owner, int MAX_CHAIN_LENGTH) {
        if (chainLength < MAX_CHAIN_LENGTH) {
            MetalBindEntity newArc = new MetalBindEntity(level(), owner, getX(), getY(), getZ());
            newArc.setDistance(getDistance());
            newArc.setParent(this);
            setChild(newArc);
            newArc.setControlled(false);
            level().addFreshEntity(newArc);
            chainLength++;
            newArc.chainLength = chainLength;
            newArc.createChain(owner, MAX_CHAIN_LENGTH);
        }
    }

    @Override
    public void tick() {
        super.tick();

        LivingEntity owner = getOwner();
        if (owner == null && isRemoved()) {
            return;
        }

        MetalBindEntity parent = getParent();
        moveEntity(owner, parent);
    }


    private void moveEntity(Entity owner, Entity parent) {
        if (getChild() == null) {
            moveEntityTowardsGoal(owner.getEyePos().toVector3f(), getMovementSpeed());
            keepOtherEntityNearEntity(getHead(), owner, getDistance() + 3);
            if (!getFrozen()) {
                this.move(MoverType.SELF, this.getDeltaMovement());
            }
            return;
        }

        if (parent == null) {
            moveEntityTowardsGoal(owner.getEyePos().toVector3f(), getMovementSpeed());
            owner.dismountVehicle();
            keepOtherEntityNearEntity(getTail(), owner, getDistance());
        }
        if (!getFrozen()) {
            this.move(MoverType.SELF, this.getDeltaMovement());
        }
    }

    


    @Override
    public void onClientRemoval() {
        if (getIsControlled()) {
            return;
        }
        summonParticles(this, random,
                ParticleTypes.ASH,
                0.01f, 10);
    }

    /**
     * Safely despawns the arc along with all of its children
     */
    public void despawn() {
        if (level().isClientSide) {
            return;
        }
        getHead().remove();
    }

    public MetalBindEntity getHead() {
        MetalBindEntity parent = getParent();
        if (parent == null) {
            return this;
        }
        return parent.getHead();
    }

    public MetalBindEntity getTail() {
        MetalBindEntity child = getChild();
        if (child == null) {
            return this;
        }
        return child.getTail();
    }


    /**
     * remove a specific link from the chain. it also kills its children
     */
    public void remove() {
        MetalBindEntity child = getChild();
        if (child == null) {
            if (getParent() == null) {
                this.discard();
                return;
            }
            getParent().setChild(null);
            this.discard();
            return;
        }
        child.remove();
        MetalBindEntity parent = getParent();
        if (parent != null) {
            getParent().setChild(null);
        }
        this.discard();
    }


    public MetalBindEntity getParent() {
        int parentId = this.getEntityData().get(PARENT_ID);
        Entity parent = this.level().getEntity(parentId);
        return parent instanceof MetalBindEntity ? (MetalBindEntity) this.level().getEntity(parentId) : null;
    }

    public void setParent(MetalBindEntity parent) {
        this.getEntityData().set(PARENT_ID, parent != null ? parent.getId() : 0);
    }

    public MetalBindEntity getChild() {
        int childId = this.getEntityData().get(CHILD_ID);
        Entity child = this.level().getEntity(childId);
        return child instanceof MetalBindEntity ? (MetalBindEntity) this.level().getEntity(childId) : null;
    }

    public void setChild(MetalBindEntity child) {
        this.getEntityData().set(CHILD_ID, child != null ? child.getId() : 0);
    }

    public boolean getFrozen() {
        return this.getEntityData().get(FROZEN);
    }

    public void setFrozen(boolean val) {
        this.getEntityData().set(FROZEN, val);
    }

    public float getDistance() {
        return this.getEntityData().get(DISTANCE);
    }

    public void setDistance(float val) {
        this.getEntityData().set(DISTANCE, val);
    }

    @Override
    public boolean isNoGravity() {
        return getParent() == null;
    }

    @Override
    public boolean pushesEntitiesAway() {
        return false;
    }

    @Override
    public boolean discardsOnNullOwner() {
        return true;
    }
}
