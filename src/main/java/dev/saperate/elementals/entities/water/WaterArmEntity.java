package dev.saperate.elementals.entities.water;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.BlockState;
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
import org.joml.Vector3f;

import static dev.saperate.elementals.utils.SapsUtils.*;

public class WaterArmEntity extends ProjectileEntity {
    private static final TrackedData<Integer> PARENT_ID = DataTracker.registerData(WaterArmEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> CHILD_ID = DataTracker.registerData(WaterArmEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> OWNER_ID = DataTracker.registerData(WaterArmEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> IS_CONTROLLED = DataTracker.registerData(WaterArmEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public static final EntityType<WaterArmEntity> WATERARM = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "water_arm"),
            FabricEntityTypeBuilder.<WaterArmEntity>create(SpawnGroup.MISC, WaterArmEntity::new)
                    .dimensions(EntityDimensions.fixed(0.25f, 0.25f)).build());
    public static final float chainDistance = 1;
    private static final int MAX_CHAIN_LENGTH = 4;
    public int chainLength = 0;


    public WaterArmEntity(EntityType<WaterArmEntity> type, World world) {
        super(type, world);
    }

    public WaterArmEntity(World world, LivingEntity owner) {
        this(world, owner, owner.getX(), owner.getY(), owner.getZ());
    }

    public WaterArmEntity(World world, LivingEntity owner, double x, double y, double z) {
        super(WATERARM, world);
        setOwner(owner);
        setPos(x, y, z);
        setNoGravity(false);
        setControlled(true);
    }

    public void createChain(LivingEntity owner) {
        if (chainLength < MAX_CHAIN_LENGTH) {
            WaterArmEntity newArc = new WaterArmEntity(getWorld(), owner, getX(), getY(), getZ());
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
        WaterArmEntity parent = getParent();
        if (!getIsControlled() && parent == null) {
            HitResult hit = ProjectileUtil.getCollision(this, entity -> entity instanceof LivingEntity);
            if (hit.getType() == HitResult.Type.ENTITY) {
                LivingEntity entity = (LivingEntity) ((EntityHitResult) hit).getEntity();
                entity.damage(this.getDamageSources().playerAttack((PlayerEntity) owner), 4);
                entity.addVelocity(this.getVelocity().multiply(0.2f));
                WaterArmEntity child = getChild();
                if (child != null) {
                    this.getChild().setParent(null);
                }
                discard();
            }
            if (!getWorld().isClient) {
                BlockPos blockDown = getBlockPos().down();
                BlockState blockState = getWorld().getBlockState(blockDown);

                if (!blockState.isAir() && getY() - getBlockPos().getY() == 0) {
                    WaterArmEntity child = getChild();
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

        //gravity
        if (!hasNoGravity() && parent == null) {
            this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
        }


        if (!hasNoGravity()) {
            if (getIsControlled()) {//Head always goes to the center of the player
                controlEntity(owner);
                HitResult hit = raycastFull(owner, 5, true, entity -> !(entity instanceof WaterArmEntity));


                forwardIk(hit.getPos());

            }
        }


        this.move(MovementType.SELF, this.getVelocity().add(owner.getVelocity().multiply(1, 0, 1)));
    }

    public Vec3d forwardIk(Vec3d goal) {
        WaterArmEntity child = getChild();
        if (child != null) {
            goal = child.forwardIk(goal);
            if (getParent() == null) {
                return null;
            }
        }

        Vec3d newGoal = getPos();
        goTowardsGoal(goal);

        return newGoal;
    }


    private void controlEntity(Entity owner) {
        Vector3f direction = owner.getPos().add(0, (owner.getEyeY() - owner.getY()) / 2, 0)
                .subtract(getPos()).toVector3f();
        direction.mul(0.125f);

        if (direction.length() < 0.4f) {
            this.setVelocity(0, 0, 0);
        }


        this.addVelocity(direction.x, direction.y, direction.z);
    }

    private void goTowardsGoal(Vec3d goal) {
        Vec3d direction = goal.subtract(getPos());
        double distance = direction.length();

        //Constraint so that it stays roughly N blocks away from the parent
        if (distance > chainDistance) {
            direction = direction.multiply(distance - chainDistance).multiply(0.075f);
        } else if (distance < chainDistance) {
            direction = direction.multiply(-0.03f);
        } else {
            direction = direction.multiply(0);
        }


        double damping = 0.1f + (0.3f) * (1 - Math.min(1, distance / chainDistance));
        direction = direction.multiply(damping);

        this.addVelocity(direction.x, direction.y, direction.z);

        this.addVelocity(getVelocity().multiply(-0.1f));
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

    public WaterArmEntity getHead() {
        WaterArmEntity parent = getParent();
        if (parent == null) {
            return this;
        }
        return parent.getHead();
    }

    /**
     * remove a specific link from the chain. it also kills its children
     */
    public void remove() {
        WaterArmEntity child = getChild();
        if (child == null) {
            getParent().setChild(null);
            this.discard();
            return;
        }
        child.remove();
        WaterArmEntity parent = getParent();
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

    public WaterArmEntity getParent() {
        int parentId = this.getDataTracker().get(PARENT_ID);
        return parentId != 0 ? (WaterArmEntity) this.getWorld().getEntityById(parentId) : null;
    }

    public void setParent(WaterArmEntity parent) {
        this.getDataTracker().set(PARENT_ID, parent != null ? parent.getId() : 0);
    }

    public WaterArmEntity getChild() {
        int childId = this.getDataTracker().get(CHILD_ID);
        return childId != 0 ? (WaterArmEntity) this.getWorld().getEntityById(childId) : null;
    }

    public void setChild(WaterArmEntity child) {
        this.getDataTracker().set(CHILD_ID, child != null ? child.getId() : 0);
    }
}
