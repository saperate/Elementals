package dev.saperate.elementals.entities.metal;

import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import dev.saperate.elementals.entities.fire.FireArcEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

import static dev.saperate.elementals.Elementals.LIGHTNING_PARTICLE_TYPE;
import static dev.saperate.elementals.entities.ElementalEntities.*;
import static dev.saperate.elementals.utils.SapsUtils.*;

public class MetalBindEntity extends AbstractElementalsEntity<LivingEntity> {
    private static final TrackedData<Boolean> FROZEN = DataTracker.registerData(MetalBindEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Float> DISTANCE = DataTracker.registerData(MetalBindEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Integer> PARENT_ID = DataTracker.registerData(MetalBindEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> CHILD_ID = DataTracker.registerData(MetalBindEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public int chainLength = 0;


    public MetalBindEntity(EntityType<MetalBindEntity> type, World world) {
        super(type, world, LivingEntity.class);
    }

    public MetalBindEntity(World world, LivingEntity owner, double x, double y, double z) {
        super(METALBIND, world, LivingEntity.class);
        setOwner(owner);
        setPos(x, y, z);

        setControlled(true);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.getDataTracker().startTracking(FROZEN, false);
        this.getDataTracker().startTracking(DISTANCE, 10f);
        this.getDataTracker().startTracking(PARENT_ID, 0);
        this.getDataTracker().startTracking(CHILD_ID, 0);
    }

    public void createChain(LivingEntity owner, int MAX_CHAIN_LENGTH) {
        if (chainLength < MAX_CHAIN_LENGTH) {
            MetalBindEntity newArc = new MetalBindEntity(getWorld(), owner, getX(), getY(), getZ());
            newArc.setDistance(getDistance());
            newArc.setParent(this);
            setChild(newArc);
            newArc.setControlled(false);
            getWorld().spawnEntity(newArc);
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
                this.move(MovementType.SELF, this.getVelocity());
            }
            return;
        }

        if (parent == null) {
            moveEntityTowardsGoal(owner.getEyePos().toVector3f(), getMovementSpeed());
            owner.dismountVehicle();
            keepOtherEntityNearEntity(getTail(), owner, getDistance());
        }
        if (!getFrozen()) {
            this.move(MovementType.SELF, this.getVelocity());
        }
    }

    


    @Override
    public void onRemoved() {
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
        if (getWorld().isClient) {
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
        int parentId = this.getDataTracker().get(PARENT_ID);
        Entity parent = this.getWorld().getEntityById(parentId);
        return parent instanceof MetalBindEntity ? (MetalBindEntity) this.getWorld().getEntityById(parentId) : null;
    }

    public void setParent(MetalBindEntity parent) {
        this.getDataTracker().set(PARENT_ID, parent != null ? parent.getId() : 0);
    }

    public MetalBindEntity getChild() {
        int childId = this.getDataTracker().get(CHILD_ID);
        Entity child = this.getWorld().getEntityById(childId);
        return child instanceof MetalBindEntity ? (MetalBindEntity) this.getWorld().getEntityById(childId) : null;
    }

    public void setChild(MetalBindEntity child) {
        this.getDataTracker().set(CHILD_ID, child != null ? child.getId() : 0);
    }

    public boolean getFrozen() {
        return this.getDataTracker().get(FROZEN);
    }

    public void setFrozen(boolean val) {
        this.getDataTracker().set(FROZEN, val);
    }

    public float getDistance() {
        return this.getDataTracker().get(DISTANCE);
    }

    public void setDistance(float val) {
        this.getDataTracker().set(DISTANCE, val);
    }

    @Override
    public boolean hasNoGravity() {
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
