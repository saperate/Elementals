package dev.saperate.elementals.entities.lightning;

import dev.saperate.elementals.effects.ElementalsStatusEffects;
import dev.saperate.elementals.data.ElementalConfig;
import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import dev.saperate.elementals.entities.fire.FireArcEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.SynchedEntityData;
import net.minecraft.entity.data.EntityDataAccessor;
import net.minecraft.entity.data.EntityDataSerializers;
import net.minecraft.entity.effect.MobEffectInstance;
import net.minecraft.entity.player.Player;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3;
import net.minecraft.world.Level;

import static dev.saperate.elementals.Elementals.LIGHTNING_PARTICLE_TYPE;
import static dev.saperate.elementals.entities.ElementalEntities.LIGHTNINGARC;
import static dev.saperate.elementals.entities.ElementalEntities.VOLTARC;
import static dev.saperate.elementals.utils.SapsUtils.*;

public class VoltArcEntity extends AbstractElementalsEntity<Player> {

    private static final EntityDataAccessor<Integer> PARENT_ID = SynchedEntityData.defineId(VoltArcEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> CHILD_ID = SynchedEntityData.defineId(VoltArcEntity.class, EntityDataSerializers.INT);
    public static final float chainDistance = 0.75f;
    public static final int MAX_CHAIN_LENGTH = 1;
    public int chainLength = 0;

    public int duration = 200;


    public VoltArcEntity(EntityType<VoltArcEntity> type, Level world) {
        super(type, world, Player.class);
    }

    public VoltArcEntity(Level world, Player owner, double x, double y, double z) {
        super(VOLTARC, world, Player.class);
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
        VoltArcEntity parent = getTail();
        VoltArcEntity newArc = new VoltArcEntity(level(), getOwner(), getX(), getY(), getZ());

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
        if(entity == getOwner() || getParent() != null || entity == getOwner()){
            return;
        }
        if(entity instanceof LivingEntity living){
            //TODO make a custom sound
            playSound(SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER,1,1);
            living.addEffect(new MobEffectInstance(ElementalsStatusEffects.STUNNED,duration, 0, false,false,true));
            living.hurt(this.damageSources().playerAttack(getOwner()),1 * ElementalConfig.get().BENDING_DAMAGE_MULTIPLIER);
            remove();
        }

    }

    private void moveEntity(Entity owner, Entity parent) {

        if (getIsControlled()) {
            moveEntityTowardsGoal(getEntityLookVector(owner, 3).add(0,0.5,0).toVector3f());
        } else if (getParent() != null) {
            setDeltaMovement(0,0,0);
            Vec3 direction = parent.getPos().subtract(getPos());
            double distance = direction.length();

            if (distance > chainDistance && (getChild() != null || chainLength == MAX_CHAIN_LENGTH)) {
                direction = direction.normalize().multiply(distance - chainDistance).add(getPos());
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
                0.01f, 5);
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
        int parentId = this.getEntityData().get(PARENT_ID);
        Entity parent = this.level().getEntity(parentId);
        return parent instanceof VoltArcEntity ? (VoltArcEntity) this.level().getEntity(parentId) : null;
    }

    public void setParent(VoltArcEntity parent) {
        this.getEntityData().set(PARENT_ID, parent != null ? parent.getId() : 0);
    }

    public VoltArcEntity getChild() {
        int childId = this.getEntityData().get(CHILD_ID);
        Entity child = this.level().getEntity(childId);
        return child instanceof VoltArcEntity ? (VoltArcEntity) this.level().getEntity(childId) : null;
    }

    public void setChild(VoltArcEntity child) {
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

    @Override
    public float touchGroundFrictionMultiplier() {
        return -1;
    }
}
