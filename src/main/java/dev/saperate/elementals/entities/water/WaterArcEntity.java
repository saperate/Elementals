package dev.saperate.elementals.entities.water;

import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

import static dev.saperate.elementals.entities.ElementalEntities.WATERARC;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

public class WaterArcEntity extends AbstractElementalsEntity<PlayerEntity> {
    private static final TrackedData<Integer> PARENT_ID = DataTracker.registerData(WaterArcEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> CHILD_ID = DataTracker.registerData(WaterArcEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final float chainDistance = 0.9f;
    private static final int MAX_CHAIN_LENGTH = 4;
    public int chainLength = 0;


    public WaterArcEntity(EntityType<WaterArcEntity> type, World world) {
        super(type, world, PlayerEntity.class);
    }

    public WaterArcEntity(World world, PlayerEntity owner) {
        this(world, owner, owner.getX(), owner.getY(), owner.getZ());
    }

    public WaterArcEntity(World world, PlayerEntity owner, double x, double y, double z) {
        super(WATERARC, world, PlayerEntity.class);
        setOwner(owner);
        setPos(x, y, z);
        setNoGravity(false);
        setControlled(true);
    }

    public void createChain(PlayerEntity owner) {
        if (chainLength < MAX_CHAIN_LENGTH) {
            WaterArcEntity newArc = new WaterArcEntity(getWorld(), owner, getX(), getY(), getZ());
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
                    ParticleTypes.SPLASH,
                    0, 1);
            if (getParent() == null) {
                playSound(SoundEvents.ENTITY_PLAYER_SWIM, 0.25f, 0);
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

        int damage = 4;
        if (plrData.canUseUpgrade("waterArcMastery")) {
            damage = 8;
        } else if (plrData.canUseUpgrade("waterArcDamageI")) {
            damage = 6;
        }

        entity.damage(this.getDamageSources().playerAttack((PlayerEntity) getOwner()), damage);
        entity.addVelocity(this.getVelocity().multiply(0.2f));
        entity.velocityModified = true;
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

            Vec3d direction = parent.getPos().subtract(getPos());
            double distance = direction.length();
            if (distance > 4) {
                direction = direction.normalize().multiply(distance - chainDistance).add(getPos());
                setPos(direction.x, direction.y, direction.z);
            } else {
                //Constraint so that it stays roughly N blocks away from the parent
                if (distance > chainDistance) {
                    direction = direction.multiply(distance - chainDistance).multiply(0.1f);
                } else if (distance < chainDistance) {
                    direction = direction.multiply(-0.025f);
                } else {
                    direction = direction.multiply(0);
                }


                double damping = 0.1f + (0.3f - 0.1f) * (1 - Math.min(1, distance / chainDistance));
                direction = direction.multiply(damping);

                this.addVelocity(direction.x, direction.y, direction.z);

                this.addVelocity(getVelocity().multiply(-0.1f));
            }
        }


        this.move(MovementType.SELF, this.getVelocity());
    }


    @Override
    public void onRemoved() {
        summonParticles(this, random, ParticleTypes.SPLASH, 0, 10);
        this.getWorld().playSound(getX(), getY(), getZ(), SoundEvents.ENTITY_PLAYER_SPLASH, SoundCategory.BLOCKS, 0.25f, (1.0f + (this.getWorld().random.nextFloat() - this.getWorld().random.nextFloat()) * 0.2f) * 0.7f, false);

    }

    @Override
    public boolean hasNoGravity() {
        return super.hasNoGravity() || getParent() != null;
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
        int parentId = this.getDataTracker().get(PARENT_ID);
        return parentId != 0 ? (WaterArcEntity) this.getWorld().getEntityById(parentId) : null;
    }

    public void setParent(WaterArcEntity parent) {
        this.getDataTracker().set(PARENT_ID, parent != null ? parent.getId() : 0);
    }

    public WaterArcEntity getChild() {
        int childId = this.getDataTracker().get(CHILD_ID);
        return childId != 0 ? (WaterArcEntity) this.getWorld().getEntityById(childId) : null;
    }

    public void setChild(WaterArcEntity child) {
        this.getDataTracker().set(CHILD_ID, child != null ? child.getId() : 0);
    }


}
