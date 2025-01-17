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
import static dev.saperate.elementals.entities.ElementalEntities.LIGHTNINGARC;
import static dev.saperate.elementals.entities.ElementalEntities.METALCABLE;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

public class MetalCableEntity extends AbstractElementalsEntity<LivingEntity> {

    private static final TrackedData<Integer> PARENT_ID = DataTracker.registerData(MetalCableEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> CHILD_ID = DataTracker.registerData(MetalCableEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final float chainDistance = 0.5f;
    public static final int MAX_CHAIN_LENGTH = 10;
    public int chainLength = 0;


    public MetalCableEntity(EntityType<MetalCableEntity> type, World world) {
        super(type, world, LivingEntity.class);
    }

    public MetalCableEntity(World world, LivingEntity owner, double x, double y, double z) {
        super(METALCABLE, world, LivingEntity.class);
        setOwner(owner);
        setPos(x, y, z);

        setControlled(true);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.getDataTracker().startTracking(PARENT_ID, 0);
        this.getDataTracker().startTracking(CHILD_ID, 0);
    }

    public void createChain(LivingEntity owner) {
        if (chainLength < MAX_CHAIN_LENGTH) {
            MetalCableEntity newArc = new MetalCableEntity(getWorld(), owner, getX(), getY(), getZ());
            newArc.setParent(this);
            setChild(newArc);
            newArc.setControlled(false);
            getWorld().spawnEntity(newArc);
            chainLength++;
            newArc.chainLength = chainLength;
            newArc.createChain(owner);
        }
    }

    @Override
    public void tick() {
        super.tick();

        LivingEntity owner = getOwner();
        if (owner == null && isRemoved()) {
            return;
        }

        MetalCableEntity parent = getParent();
        moveEntity(owner, parent);
    }


    private void moveEntity(Entity owner, Entity parent) {
        if (getChild() == null) {
            setPosition(owner.getPos().add(0,1,0));
            return;
        }

        if (getParent() == null) {
            owner.dismountVehicle();
            Vec3d direction = getOwner().getPos()
                    .add(0, 1, 0)
                    .subtract(getPos())
                    .multiply(getMovementSpeed() * 4);
            this.setVelocity(direction.x, direction.y, direction.z);

            double distanceToOwner = getOwner().getPos().distanceTo(getTail().getPos());
            if(distanceToOwner >= 18){
                Vec3d dirCenter = owner.getPos().subtract(getTail().getPos()).multiply(-1).normalize();
                Vec3d velocity = owner.getVelocity().multiply(1.05);

                Vec3d tangent = velocity.subtract(dirCenter.multiply(
                        ((velocity.dotProduct(dirCenter)) / dirCenter.dotProduct(dirCenter))));
                owner.setVelocity(tangent.add(dirCenter.multiply(distanceToOwner - 18)));
                owner.move(MovementType.PLAYER, owner.getVelocity());
                owner.fallDistance = 0;
            }
        } else {
            Vec3d finalPos = getPos();

            Vec3d directionToParent = parent.getPos().subtract(getPos());
            double distanceToParent = directionToParent.length();

            if (distanceToParent >= chainDistance) {
                finalPos = finalPos.add(directionToParent.normalize()
                        .multiply(distanceToParent - chainDistance));
            }

            Vec3d childPos = getChild().getPos();
            Vec3d directionToChild = childPos.subtract(getPos());
            double distanceToChild = directionToChild.length();

            if (distanceToChild >= chainDistance + 1) {
                finalPos = finalPos.add(directionToChild.normalize()
                        .multiply(2)
                        .multiply(distanceToChild - chainDistance));
            }

            if (finalPos.distanceTo(getPos()) >= 4){
                setPosition(finalPos.subtract(getPos()).multiply(0.1).add(getPos()));
            }else{
                moveEntityTowardsGoal(finalPos.toVector3f());
            }
        }

        this.move(MovementType.SELF, this.getVelocity());
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

    public MetalCableEntity getHead() {
        MetalCableEntity parent = getParent();
        if (parent == null) {
            return this;
        }
        return parent.getHead();
    }

    public MetalCableEntity getTail() {
        MetalCableEntity child = getChild();
        if (child == null) {
            return this;
        }
        return child.getTail();
    }


    /**
     * remove a specific link from the chain. it also kills its children
     */
    public void remove() {
        MetalCableEntity child = getChild();
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
        MetalCableEntity parent = getParent();
        if (parent != null) {
            getParent().setChild(null);
        }
        this.discard();
    }


    public MetalCableEntity getParent() {
        int parentId = this.getDataTracker().get(PARENT_ID);
        Entity parent = this.getWorld().getEntityById(parentId);
        return parent instanceof MetalCableEntity ? (MetalCableEntity) this.getWorld().getEntityById(parentId) : null;
    }

    public void setParent(MetalCableEntity parent) {
        this.getDataTracker().set(PARENT_ID, parent != null ? parent.getId() : 0);
    }

    public MetalCableEntity getChild() {
        int childId = this.getDataTracker().get(CHILD_ID);
        Entity child = this.getWorld().getEntityById(childId);
        return child instanceof MetalCableEntity ? (MetalCableEntity) this.getWorld().getEntityById(childId) : null;
    }

    public void setChild(MetalCableEntity child) {
        this.getDataTracker().set(CHILD_ID, child != null ? child.getId() : 0);
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
