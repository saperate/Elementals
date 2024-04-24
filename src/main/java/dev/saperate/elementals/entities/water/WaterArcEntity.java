package dev.saperate.elementals.entities.water;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3d;
import org.joml.Vector3f;

import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

public class WaterArcEntity extends ProjectileEntity {
    private static final TrackedData<Integer> PARENT_ID = DataTracker.registerData(WaterArcEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> CHILD_ID = DataTracker.registerData(WaterArcEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> OWNER_ID = DataTracker.registerData(WaterArcEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> IS_CONTROLLED = DataTracker.registerData(WaterArcEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public static final EntityType<WaterArcEntity> WATERARC = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "water_arc"),
            FabricEntityTypeBuilder.<WaterArcEntity>create(SpawnGroup.MISC, WaterArcEntity::new)
                    .dimensions(EntityDimensions.fixed(0.25f, 0.25f)).build());
    public static final float chainDistance = 0.9f;
    private static final int MAX_CHAIN_LENGTH = 4;
    public int chainLength = 0;


    public WaterArcEntity(EntityType<WaterArcEntity> type, World world) {
        super(type, world);
    }

    public WaterArcEntity(World world, LivingEntity owner) {
        this(world, owner, owner.getX(), owner.getY(), owner.getZ());
    }

    public WaterArcEntity(World world, LivingEntity owner, double x, double y, double z) {
        super(WATERARC, world);
        setOwner(owner);
        setPos(x, y, z);
        setNoGravity(false);
        setControlled(true);
    }

    public void createChain(LivingEntity owner) {
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
        this.getDataTracker().startTracking(PARENT_ID, 0);
        this.getDataTracker().startTracking(CHILD_ID, 0);
        this.getDataTracker().startTracking(OWNER_ID, 0);
        this.getDataTracker().startTracking(IS_CONTROLLED, false);
    }

    @Override
    public void tick() {
        super.tick();
        Entity owner = getOwner();
        if (owner == null) {
            this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
            this.move(MovementType.SELF, this.getVelocity());
            return;
        }
        WaterArcEntity parent = getParent();
        if (!getIsControlled() && parent == null) {
            HitResult hit = ProjectileUtil.getCollision(this, entity -> entity instanceof LivingEntity);
            if (hit.getType() == HitResult.Type.ENTITY) {
                LivingEntity entity = (LivingEntity) ((EntityHitResult) hit).getEntity();
                entity.damage(this.getDamageSources().playerAttack((PlayerEntity) owner), 4);
                entity.addVelocity(this.getVelocity().multiply(0.2f));
                WaterArcEntity child = getChild();
                if (child != null) {
                    this.getChild().setParent(null);
                }
                discard();
            }
            if (!getWorld().isClient) {
                BlockPos blockDown = getBlockPos().down();
                BlockState blockState = getWorld().getBlockState(blockDown);

                if (!blockState.isAir() && getY() - getBlockPos().getY() == 0) {
                    WaterArcEntity child = getChild();
                    if (child != null) {
                        this.getChild().setParent(null);
                    }
                    discard();
                }
            }
        }


        moveEntity(owner, parent);
    }

    private void moveEntity(Entity owner, Entity parent) {
        this.getWorld().getEntitiesByType(TypeFilter.instanceOf(PlayerEntity.class), this.getBoundingBox(), EntityPredicates.canBePushedBy(this)).forEach(this::pushAway);

        //gravity
        if (!hasNoGravity() && parent == null) {
            this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
        }


        if (!hasNoGravity()) {
            if (getIsControlled()) {
                controlEntity(owner);
            } else {
                if (parent != null) {
                    Vec3d direction = parent.getPos().subtract(getPos());
                    double distance = direction.length();
                    if (distance > 2) {
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
            }
        }


        this.move(MovementType.SELF, this.getVelocity());
    }

    private void controlEntity(Entity owner) {
        Vector3f direction = getEntityLookVector(owner, 3)
                .subtract(getPos()).toVector3f();
        direction.mul(0.125f);

        if (direction.length() < 0.4f) {
            this.setVelocity(0, 0, 0);
        }


        this.addVelocity(direction.x, direction.y, direction.z);
    }


    @Override
    public void onRemoved() {
        summonParticles(this, random, ParticleTypes.SPLASH, 10, 100);
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
        WaterArcEntity child = getChild();
        if (child == null) {
            getParent().setChild(null);
            this.discard();
            return;
        }
        child.remove();
        WaterArcEntity parent = getParent();
        if (parent != null) {
            getParent().setChild(null);
        }
        this.discard();
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        if (getOwner() != null) {
            super.writeCustomDataToNbt(nbt);
            nbt.putInt("OwnerID", this.getOwner().getId());
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        int ownerId = nbt.getInt("OwnerID");
        this.getDataTracker().set(OWNER_ID, ownerId);
    }

    public void setControlled(boolean val) {
        this.getDataTracker().set(IS_CONTROLLED, val);
    }

    public boolean getIsControlled() {
        return this.getDataTracker().get(IS_CONTROLLED);
    }

    public LivingEntity getOwner() {
        Entity owner = this.getWorld().getEntityById(this.getDataTracker().get(OWNER_ID));
        return (owner instanceof LivingEntity) ? (LivingEntity) owner : null;
    }

    public void setOwner(LivingEntity owner) {
        this.getDataTracker().set(OWNER_ID, owner.getId());
    }

    protected void pushAway(Entity entity) {
        entity.pushAwayFrom(this);
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
