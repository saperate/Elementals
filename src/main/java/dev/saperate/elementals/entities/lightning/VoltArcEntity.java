package dev.saperate.elementals.entities.lightning;

import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import dev.saperate.elementals.entities.fire.FireArcEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static dev.saperate.elementals.Elementals.LIGHTNING_PARTICLE_TYPE;
import static dev.saperate.elementals.effects.StunnedStatusEffect.STUNNED_EFFECT;
import static dev.saperate.elementals.entities.ElementalEntities.LIGHTNINGARC;
import static dev.saperate.elementals.entities.ElementalEntities.VOLTARC;
import static dev.saperate.elementals.utils.SapsUtils.*;

public class VoltArcEntity extends AbstractElementalsEntity<PlayerEntity> {

    private static final TrackedData<Integer> PARENT_ID = DataTracker.registerData(VoltArcEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> CHILD_ID = DataTracker.registerData(VoltArcEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final float chainDistance = 0.75f;
    public static final int MAX_CHAIN_LENGTH = 1;
    public int chainLength = 0;


    public VoltArcEntity(EntityType<VoltArcEntity> type, World world) {
        super(type, world, PlayerEntity.class);
    }

    public VoltArcEntity(World world, PlayerEntity owner, double x, double y, double z) {
        super(VOLTARC, world, PlayerEntity.class);
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
        VoltArcEntity parent = getTail();
        VoltArcEntity newArc = new VoltArcEntity(getWorld(), getOwner(), getX(), getY(), getZ());

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
        if(entity == getOwner() || getParent() != null || entity == getOwner()){
            return;
        }
        if(entity instanceof LivingEntity living){
            //TODO make a custom sound
            playSound(SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER,1,1);
            living.addStatusEffect(new StatusEffectInstance(STUNNED_EFFECT,200, 0, false,false,true));
            living.damage(this.getDamageSources().playerAttack(getOwner()),1);
            remove();
        }

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
                LIGHTNING_PARTICLE_TYPE,
                0.01f, 5);
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

    public VoltArcEntity getHead() {
        VoltArcEntity parent = getParent();
        if (parent == null) {
            return this;
        }
        return parent.getHead();
    }

    public VoltArcEntity getTail() {
        VoltArcEntity child = getChild();
        if (child == null) {
            return this;
        }
        return child.getTail();
    }

    /**
     * remove a specific link from the chain. it also kills its children
     */
    public void remove() {
        VoltArcEntity child = getChild();
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
        VoltArcEntity parent = getParent();
        if (parent != null) {
            getParent().setChild(null);
        }
        this.discard();
    }


    public VoltArcEntity getParent() {
        int parentId = this.getDataTracker().get(PARENT_ID);
        Entity parent = this.getWorld().getEntityById(parentId);
        return parent instanceof VoltArcEntity ? (VoltArcEntity) this.getWorld().getEntityById(parentId) : null;
    }

    public void setParent(VoltArcEntity parent) {
        this.getDataTracker().set(PARENT_ID, parent != null ? parent.getId() : 0);
    }

    public VoltArcEntity getChild() {
        int childId = this.getDataTracker().get(CHILD_ID);
        Entity child = this.getWorld().getEntityById(childId);
        return child instanceof VoltArcEntity ? (VoltArcEntity) this.getWorld().getEntityById(childId) : null;
    }

    public void setChild(VoltArcEntity child) {
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

    @Override
    public float touchGroundFrictionMultiplier() {
        return -1;
    }
}
