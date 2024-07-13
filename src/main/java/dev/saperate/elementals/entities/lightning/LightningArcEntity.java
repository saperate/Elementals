package dev.saperate.elementals.entities.lightning;

import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import dev.saperate.elementals.entities.fire.FireArcEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static dev.saperate.elementals.entities.ElementalEntities.FIREARC;
import static dev.saperate.elementals.entities.ElementalEntities.LIGHTNINGARC;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

public class LightningArcEntity extends AbstractElementalsEntity<PlayerEntity> {

    private static final TrackedData<Integer> PARENT_ID = DataTracker.registerData(FireArcEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> CHILD_ID = DataTracker.registerData(FireArcEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final float chainDistance = 0.75f;
    public static final int MAX_CHAIN_LENGTH = 8;
    public int chainLength = 0;


    public LightningArcEntity(EntityType<LightningArcEntity> type, World world) {
        super(type, world, PlayerEntity.class);
    }

    public LightningArcEntity(World world, PlayerEntity owner, double x, double y, double z) {
        super(LIGHTNINGARC, world, PlayerEntity.class);
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


    public void makeChild(){
        LightningArcEntity parent = getTail();
        LightningArcEntity newArc = new LightningArcEntity(getWorld(), getOwner(), getX(), getY(), getZ());

        newArc.setParent(parent);
        parent.setChild(newArc);
        newArc.setControlled(false);
        getWorld().spawnEntity(newArc);

        chainLength++;
        newArc.chainLength = chainLength;
    }

    @Override
    public void tick() {
        super.tick();
        if (random.nextBetween(0, 20) == 6) {
            summonParticles(this, random,
                    ParticleTypes.GLOW,
                    0, 1);
            if (getParent() == null) {
                playSound(SoundEvents.BLOCK_FIRE_AMBIENT, 1, 0);
            }
        }

        super.tick();

        PlayerEntity owner = getOwner();
        if (owner == null && isRemoved()) {
            return;
        }

        moveEntity(owner, getParent());
    }

    @Override
    public void collidesWithGround() {
        if (getParent() == null) {
            remove();
        }
    }

    @Override
    public void onHitEntity(Entity entity) {
        if(entity == getOwner() || getParent() != null){
            return;
        }
        remove();
    }

    private void moveEntity(Entity owner, Entity parent) {

        if (getIsControlled()) {
            moveEntityTowardsGoal(getEntityLookVector(owner, 3).add(0,0.5,0).toVector3f());
        } else if (getParent() != null) {
            setVelocity(0,0,0);
            Vec3d direction = parent.getPos().subtract(getPos());
            double distance = direction.length();

            if (distance > chainDistance && (getChild() != null || chainLength == MAX_CHAIN_LENGTH)) {
                direction = direction.normalize().multiply(distance - chainDistance).add(getPos());
                setPos(direction.x, direction.y, direction.z);
            }
            if(getChild() == null && chainLength != MAX_CHAIN_LENGTH && distance > chainDistance * 1.25f){ //If we are at the tail of the arc
                makeChild();
            }
        }


        this.move(MovementType.SELF, this.getVelocity());
    }


    @Override
    public void onRemoved() {
        if(getIsControlled()){
            return;
        }
        summonParticles(this, random,
                ParticleTypes.GLOW,
                0.1f, 10);
        if(getParent() == null){
            this.getWorld().playSound(getX(), getY(), getZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.BLOCKS, 0.25f, (1.0f + (this.getWorld().random.nextFloat() - this.getWorld().random.nextFloat()) * 0.2f) * 0.7f, false);
        }
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

    public LightningArcEntity getHead() {
        LightningArcEntity parent = getParent();
        if (parent == null) {
            return this;
        }
        return parent.getHead();
    }

    public LightningArcEntity getTail() {
        LightningArcEntity child = getChild();
        if (child == null) {
            return this;
        }
        return child.getTail();
    }

    /**
     * remove a specific link from the chain. it also kills its children
     */
    public void remove() {
        LightningArcEntity child = getChild();
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
        LightningArcEntity parent = getParent();
        if (parent != null) {
            getParent().setChild(null);
        }
        this.discard();
    }


    public LightningArcEntity getParent() {
        int parentId = this.getDataTracker().get(PARENT_ID);
        Entity parent = this.getWorld().getEntityById(parentId);
        return parent instanceof LightningArcEntity ? (LightningArcEntity) this.getWorld().getEntityById(parentId) : null;
    }

    public void setParent(LightningArcEntity parent) {
        this.getDataTracker().set(PARENT_ID, parent != null ? parent.getId() : 0);
    }

    public LightningArcEntity getChild() {
        int childId = this.getDataTracker().get(CHILD_ID);
        Entity child = this.getWorld().getEntityById(childId);
        return child instanceof LightningArcEntity ? (LightningArcEntity) this.getWorld().getEntityById(childId) : null;
    }

    public void setChild(LightningArcEntity child) {
        this.getDataTracker().set(CHILD_ID, child != null ? child.getId() : 0);
    }

    @Override
    public boolean hasNoGravity() {
        return super.hasNoGravity() || getParent() != null;
    }

    @Override
    public boolean pushesEntitiesAway() {
        return false;
    }
}
