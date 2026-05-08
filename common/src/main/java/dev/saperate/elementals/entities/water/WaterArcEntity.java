package dev.saperate.elementals.entities.water;

import dev.saperate.elementals.data.ElementalConfig;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import static dev.saperate.elementals.entities.ElementalEntities.WATERARC;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

public class WaterArcEntity extends AbstractElementalsEntity<Player> {
    private static final EntityDataAccessor<Integer> PARENT_ID = SynchedEntityData.defineId(WaterArcEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> CHILD_ID = SynchedEntityData.defineId(WaterArcEntity.class, EntityDataSerializers.INT);
    public static final float chainDistance = 0.9f;
    private static final int MAX_CHAIN_LENGTH = 4;
    public int chainLength = 0;


    public WaterArcEntity(EntityType<WaterArcEntity> type, Level world) {
        super(type, world, Player.class);
    }

    public WaterArcEntity(Level world, Player owner) {
        this(world, owner, owner.getX(), owner.getY(), owner.getZ());
    }

    public WaterArcEntity(Level world, Player owner, double x, double y, double z) {
        super(WATERARC, world, Player.class);
        setOwner(owner);
        setPos(x, y, z);
        setNoGravity(false);
        setControlled(true);
    }
    


    public void createChain(Player owner) {
        if (chainLength < MAX_CHAIN_LENGTH) {
            WaterArcEntity newArc = new WaterArcEntity(level(), owner, getX(), getY(), getZ());
            newArc.setParent(this);
            setChild(newArc);
            newArc.setControlled(false);
            level().addFreshEntity(newArc);
            chainLength++;
            newArc.chainLength = chainLength;
            newArc.createChain(owner);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(PARENT_ID, 0);
        builder.define(CHILD_ID, 0);
    }

    @Override
    public void tick() {
        super.tick();

        if (random.nextInt(0, 40) == 6) {
            summonParticles(this, random,
                    ParticleTypes.SPLASH,
                    0, 1);
            if (getParent() == null) {
                playSound(SoundEvents.PLAYER_SWIM, 0.25f, 0);
            }
        }

        LivingEntity owner = getOwner();
        if (owner == null || isRemoved()) {
            return;
        }

        moveEntity(owner, getParent());
    }

    @Override
    public void onHitEntity(Entity entity) {
        if(entity == getOwner() || getParent() != null){
            return;
        }
        PlayerData plrData = PlayerData.get(getOwner());

        float damage = 5;
        if (plrData.canUseUpgrade("waterArcMastery")) {
            damage = 9;
        } else if (plrData.canUseUpgrade("waterArcDamageI")) {
            damage = 7;
        }

        entity.hurt(this.damageSources().playerAttack((Player) getOwner()), damage * ElementalConfig.get().BENDING_DAMAGE_MULTIPLIER);
        entity.addDeltaMovement(this.getDeltaMovement().scale(0.2f));
        entity.hasImpulse = true;//TODO check if this works
        remove();
    }

    @Override
    public void collidesWithGround() {
        if (getParent() == null) {
            remove();
        }
    }

    private void moveEntity(Entity owner, Entity parent) {

        if (getIsControlled()) {
            moveEntityTowardsGoal(getEntityLookVector(getOwner(), 3).add(0,0.5,0).toVector3f());
        } else if (parent != null) {

            Vec3 direction = parent.position().subtract(position());
            double distance = direction.length();
            if (distance > 4) {
                direction = direction.normalize().scale(distance - chainDistance).add(position());
                setPos(direction.x, direction.y, direction.z);
            } else {
                //Constraint so that it stays roughly N blocks away from the parent
                if (distance > chainDistance) {
                    direction = direction.scale(distance - chainDistance).scale(0.1f);
                } else if (distance < chainDistance) {
                    direction = direction.scale(-0.025f);
                } else {
                    direction = direction.scale(0);
                }


                double damping = 0.1f + (0.3f - 0.1f) * (1 - Math.min(1, distance / chainDistance));
                direction = direction.scale(damping);

                this.addDeltaMovement(new Vec3(direction.x, direction.y, direction.z));

                this.addDeltaMovement(getDeltaMovement().scale(-0.1f));
            }
        }


        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    @Override
    public void onClientRemoval() {
        summonParticles(this, random, ParticleTypes.SPLASH, 0, 10);
        this.level().playSound(this,
                getOnPos(),
                SoundEvents.PLAYER_SPLASH, SoundSource.BLOCKS,
                0.25f,
                (1.0f + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2f) * 0.7f);

    }

    @Override
    public boolean isNoGravity() {
        return super.isNoGravity() || getParent() != null;
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

    public WaterArcEntity getHead() {
        WaterArcEntity parent = getParent();
        if (parent == null) {
            return this;
        }
        return parent.getHead();
    }

    /**
     * remove a specific link from the chain. it also kills its children
     */
    public void remove() {

        WaterArcEntity parent = getParent();
        if (parent != null) {
            getParent().setChild(null);
        }

        WaterArcEntity child = getChild();
        if (child == null) {
            this.discard();
            return;
        }
        child.remove();

        this.discard();
    }

    public WaterArcEntity getParent() {
        int parentId = this.getEntityData().get(PARENT_ID);
        return parentId != 0 ? (WaterArcEntity) this.level().getEntity(parentId) : null;
    }

    public void setParent(WaterArcEntity parent) {
        this.getEntityData().set(PARENT_ID, parent != null ? parent.getId() : 0);
    }

    public WaterArcEntity getChild() {
        int childId = this.getEntityData().get(CHILD_ID);
        return childId != 0 ? (WaterArcEntity) this.level().getEntity(childId) : null;
    }

    public void setChild(WaterArcEntity child) {
        this.getEntityData().set(CHILD_ID, child != null ? child.getId() : 0);
    }


}
