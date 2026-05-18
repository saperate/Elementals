package dev.saperate.elementals.entities.air;

import dev.saperate.elementals.data.ElementalConfig;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import static dev.saperate.elementals.entities.ElementalEntities.AIRSTREAM;
import static dev.saperate.elementals.misc.ElementalsSounds.WIND_BURST_SOUND_EVENT;
import static dev.saperate.elementals.misc.ElementalsSounds.WIND_SOUND_EVENT;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

public class AirStreamEntity extends AbstractElementalsEntity<Player> {
    private static final EntityDataAccessor<Integer> PARENT_ID = SynchedEntityData.defineId(AirStreamEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> CHILD_ID = SynchedEntityData.defineId(AirStreamEntity.class, EntityDataSerializers.INT);
    public static final float chainDistance = 0.65f;
    private static final int MAX_CHAIN_LENGTH = 6;
    public int chainLength = 0;


    public AirStreamEntity(EntityType<AirStreamEntity> type, Level world) {
        super(type, world, Player.class);
    }

    public AirStreamEntity(Level world, Player owner) {
        this(world, owner, owner.getX(), owner.getY(), owner.getZ());
    }

    public AirStreamEntity(Level world, Player owner, double x, double y, double z) {
        super(AIRSTREAM.get(), world, Player.class);
        setOwner(owner);
        setPos(x, y, z);
        setNoGravity(false);
        setControlled(true);
    }

    public void createChain(Player owner) {
        if (chainLength < MAX_CHAIN_LENGTH) {
            AirStreamEntity newArc = new AirStreamEntity(level(), owner, getX(), getY(), getZ());
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
                    ParticleTypes.POOF,
                    0, 1);
            if (getParent() == null) {
                playSound(WIND_SOUND_EVENT, 1, (1.0f + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2f) * 0.7f);
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
        Player owner = getOwner();
        PlayerData plrData = PlayerData.get(owner);

        float damage = 2.5f;
        if (plrData.canUseUpgrade("airStreamMastery")) {
            damage = 4.5f;
        } else if (plrData.canUseUpgrade("airStreamDamageI")) {
            damage = 3.5f;
        }

        entity.hurt(damageSources().playerAttack(owner), damage * ElementalConfig.get().BENDING_DAMAGE_MULTIPLIER);
        entity.addDeltaMovement(this.getDeltaMovement().scale(1.2f));
        entity.move(MoverType.SELF, entity.getDeltaMovement());
        entity.hurtMarked = true;
        remove();
        this.level().playSound(this, getOnPos(), WIND_BURST_SOUND_EVENT, SoundSource.BLOCKS, 1, (1.0f + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2f) * 0.7f);
    }

    @Override
    public void collidesWithGround() {
        if (getParent() != null) {
            return;
        }
        remove();
        this.level().playSound(this, getOnPos(), WIND_BURST_SOUND_EVENT, SoundSource.BLOCKS, 1, (1.0f + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2f) * 0.7f);
    }

    @Override
    public boolean isNoGravity() {
        return super.isNoGravity() || getParent() != null;
    }

    private void moveEntity(Entity owner, Entity parent) {

        if (getIsControlled()) {
            moveEntityTowardsGoal(getEntityLookVector(getOwner(), 3).add(0,0.5,0).toVector3f());
        } else {
            if (parent != null) {
                Vec3 direction = parent.position().subtract(position());
                double distance = direction.length();

                if (distance > chainDistance) {
                    direction = direction.normalize().scale(distance - chainDistance).add(position());
                    setPos(direction.x, direction.y, direction.z);
                }

            }
        }


        this.move(MoverType.SELF, this.getDeltaMovement());
    }


    @Override
    public void onClientRemoval() {
        summonParticles(this, random,
                ParticleTypes.POOF,
                0.1f, 10);
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

    public AirStreamEntity getHead() {
        AirStreamEntity parent = getParent();
        if (parent == null) {
            return this;
        }
        return parent.getHead();
    }

    /**
     * remove a specific link from the chain. it also kills its children
     */
    public void remove() {
        AirStreamEntity child = getChild();
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
        AirStreamEntity parent = getParent();
        if (parent != null) {
            getParent().setChild(null);
        }
        this.discard();
    }

    public AirStreamEntity getParent() {
        int parentId = this.getEntityData().get(PARENT_ID);
        return parentId != 0 ? (AirStreamEntity) this.level().getEntity(parentId) : null;
    }

    public void setParent(AirStreamEntity parent) {
        this.getEntityData().set(PARENT_ID, parent != null ? parent.getId() : 0);
    }

    public AirStreamEntity getChild() {
        int childId = this.getEntityData().get(CHILD_ID);
        return childId != 0 ? (AirStreamEntity) this.level().getEntity(childId) : null;
    }

    public void setChild(AirStreamEntity child) {
        this.getEntityData().set(CHILD_ID, child != null ? child.getId() : 0);
    }

}
