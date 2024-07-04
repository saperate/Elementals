package dev.saperate.elementals.entities.air;

import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

import static dev.saperate.elementals.Elementals.WIND_BURST_SOUND_EVENT;
import static dev.saperate.elementals.Elementals.WIND_SOUND_EVENT;
import static dev.saperate.elementals.entities.ElementalEntities.AIRSTREAM;
import static dev.saperate.elementals.entities.ElementalEntities.FIREARC;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

public class AirStreamEntity extends AbstractElementalsEntity<PlayerEntity> {
    private static final TrackedData<Integer> PARENT_ID = DataTracker.registerData(AirStreamEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> CHILD_ID = DataTracker.registerData(AirStreamEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final float chainDistance = 0.65f;
    private static final int MAX_CHAIN_LENGTH = 6;
    public int chainLength = 0;


    public AirStreamEntity(EntityType<AirStreamEntity> type, World world) {
        super(type, world, PlayerEntity.class);
    }

    public AirStreamEntity(World world, PlayerEntity owner) {
        this(world, owner, owner.getX(), owner.getY(), owner.getZ());
    }

    public AirStreamEntity(World world, PlayerEntity owner, double x, double y, double z) {
        super(AIRSTREAM, world, PlayerEntity.class);
        setOwner(owner);
        setPos(x, y, z);
        setNoGravity(false);
        setControlled(true);
    }

    public void createChain(PlayerEntity owner) {
        if (chainLength < MAX_CHAIN_LENGTH) {
            AirStreamEntity newArc = new AirStreamEntity(getWorld(), owner, getX(), getY(), getZ());
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
    protected void initDataTracker() {
        super.initDataTracker();
        this.getDataTracker().startTracking(PARENT_ID, 0);
        this.getDataTracker().startTracking(CHILD_ID, 0);
    }

    @Override
    public void tick() {
        super.tick();

        if (random.nextBetween(0, 40) == 6) {
            summonParticles(this, random,
                    ParticleTypes.POOF,
                    0, 1);
            if (getParent() == null) {
                playSound(WIND_SOUND_EVENT, 1, (1.0f + (this.getWorld().random.nextFloat() - this.getWorld().random.nextFloat()) * 0.2f) * 0.7f);
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
        if (getParent() != null) {
            return;
        }
        PlayerEntity owner = (PlayerEntity) getOwner();
        PlayerData plrData = PlayerData.get(owner);

        float damage = 2.5f;
        if (plrData.canUseUpgrade("airStreamMastery")) {
            damage = 6.5f;
        } else if (plrData.canUseUpgrade("airStreamDamageI")) {
            damage = 4.5f;
        }

        entity.damage(getDamageSources().playerAttack(owner), damage);
        entity.addVelocity(this.getVelocity().multiply(1f));
        entity.move(MovementType.SELF, entity.getVelocity());
        entity.velocityModified = true;
        remove();
        this.getWorld().playSound(getX(), getY(), getZ(), WIND_BURST_SOUND_EVENT, SoundCategory.BLOCKS, 1, (1.0f + (this.getWorld().random.nextFloat() - this.getWorld().random.nextFloat()) * 0.2f) * 0.7f, true);
    }

    @Override
    public void collidesWithGround() {
        if (getParent() != null) {
            return;
        }
        remove();
        this.getWorld().playSound(getX(), getY(), getZ(), WIND_BURST_SOUND_EVENT, SoundCategory.BLOCKS, 1, (1.0f + (this.getWorld().random.nextFloat() - this.getWorld().random.nextFloat()) * 0.2f) * 0.7f, true);
    }

    @Override
    public boolean hasNoGravity() {
        return super.hasNoGravity() || getParent() != null;
    }

    private void moveEntity(Entity owner, Entity parent) {

        if (getIsControlled()) {
            moveEntityTowardsGoal(getEntityLookVector(getOwner(), 3).add(0,0.5,0).toVector3f());
        } else {
            if (parent != null) {
                Vec3d direction = parent.getPos().subtract(getPos());
                double distance = direction.length();

                if (distance > chainDistance) {
                    direction = direction.normalize().multiply(distance - chainDistance).add(getPos());
                    setPos(direction.x, direction.y, direction.z);
                }

            }
        }


        this.move(MovementType.SELF, this.getVelocity());
    }


    @Override
    public void onRemoved() {
        summonParticles(this, random,
                ParticleTypes.POOF,
                0.1f, 10);
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
        int parentId = this.getDataTracker().get(PARENT_ID);
        return parentId != 0 ? (AirStreamEntity) this.getWorld().getEntityById(parentId) : null;
    }

    public void setParent(AirStreamEntity parent) {
        this.getDataTracker().set(PARENT_ID, parent != null ? parent.getId() : 0);
    }

    public AirStreamEntity getChild() {
        int childId = this.getDataTracker().get(CHILD_ID);
        return childId != 0 ? (AirStreamEntity) this.getWorld().getEntityById(childId) : null;
    }

    public void setChild(AirStreamEntity child) {
        this.getDataTracker().set(CHILD_ID, child != null ? child.getId() : 0);
    }

}
