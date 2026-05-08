package dev.saperate.elementals.entities.lightning;

import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import static dev.saperate.elementals.Elementals.LIGHTNING_PARTICLE_TYPE;
import static dev.saperate.elementals.entities.ElementalEntities.LIGHTNINGARC;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

public class LightningArcEntity extends AbstractElementalsEntity<Player> {

    private static final EntityDataAccessor<Integer> PARENT_ID = SynchedEntityData.defineId(LightningArcEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> CHILD_ID = SynchedEntityData.defineId(LightningArcEntity.class, EntityDataSerializers.INT);
    public static final float chainDistance = 0.75f;
    public static final int MAX_CHAIN_LENGTH = 7;
    public int chainLength = 0;


    public LightningArcEntity(EntityType<LightningArcEntity> type, Level world) {
        super(type, world, Player.class);
    }

    public LightningArcEntity(Level world, Player owner, double x, double y, double z) {
        super(LIGHTNINGARC, world, Player.class);
        setOwner(owner);
        setPos(x, y, z);

        setControlled(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(PARENT_ID, 0);
        builder.define(CHILD_ID, 0);
    }


    public void makeChild(){
        LightningArcEntity parent = getTail();
        LightningArcEntity newArc = new LightningArcEntity(level(), getOwner(), getX(), getY(), getZ());

        newArc.setParent(parent);
        parent.setChild(newArc);
        newArc.setControlled(false);
        level().addFreshEntity(newArc);

        chainLength++;
        newArc.chainLength = chainLength;
    }

    @Override
    public void tick() {
        super.tick();

        if (random.nextInt(0, 20) == 6) {
            summonParticles(this, random,
                    LIGHTNING_PARTICLE_TYPE,
                    0, 1, 0);
            if (getParent() == null) {
                //playSound(SoundEvents.BLOCK_FIRE_AMBIENT, 1, 0);
            }
        }

        Player owner = getOwner();
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
            setDeltaMovement(0,0,0);
            Vec3 direction = parent.position().subtract(position());
            double distance = direction.length();

            if (distance > chainDistance && (getChild() != null || chainLength == MAX_CHAIN_LENGTH)) {
                direction = direction.normalize().scale(distance - chainDistance).add(position());
                setPos(direction.x, direction.y, direction.z);
            }
            if(getChild() == null && chainLength != MAX_CHAIN_LENGTH && distance > chainDistance * 1.25f){ //If we are at the tail of the arc
                makeChild();
            }
        }


        this.move(MoverType.SELF, this.getDeltaMovement());
    }


    @Override
    public void onClientRemoval() {
        if(getIsControlled()){
            return;
        }
        summonParticles(this, random,
                LIGHTNING_PARTICLE_TYPE,
                0.05f, 2);
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
        int parentId = this.getEntityData().get(PARENT_ID);
        Entity parent = this.level().getEntity(parentId);
        return parent instanceof LightningArcEntity ? (LightningArcEntity) this.level().getEntity(parentId) : null;
    }

    public void setParent(LightningArcEntity parent) {
        this.getEntityData().set(PARENT_ID, parent != null ? parent.getId() : 0);
    }

    public LightningArcEntity getChild() {
        int childId = this.getEntityData().get(CHILD_ID);
        Entity child = this.level().getEntity(childId);
        return child instanceof LightningArcEntity ? (LightningArcEntity) this.level().getEntity(childId) : null;
    }

    public void setChild(LightningArcEntity child) {
        this.getEntityData().set(CHILD_ID, child != null ? child.getId() : 0);
    }

    @Override
    public boolean isNoGravity() {
        return super.isNoGravity() || getParent() != null;
    }

    @Override
    public boolean pushesEntitiesAway() {
        return false;
    }
}
