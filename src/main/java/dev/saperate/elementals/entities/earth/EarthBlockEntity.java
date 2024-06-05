package dev.saperate.elementals.entities.earth;

import dev.saperate.elementals.utils.SapsUtils;
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
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.List;

import static dev.saperate.elementals.entities.ElementalEntities.EARTHBLOCK;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

public class EarthBlockEntity extends ProjectileEntity {
    private static final TrackedData<Integer> OWNER_ID = DataTracker.registerData(EarthBlockEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> IS_CONTROLLED = DataTracker.registerData(EarthBlockEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> MODEL_SHAPE_ID = DataTracker.registerData(EarthBlockEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<BlockState> BLOCK_STATE = DataTracker.registerData(EarthBlockEntity.class, TrackedDataHandlerRegistry.BLOCK_STATE);
    private static final TrackedData<Vector3f> TARGET_POSITION = DataTracker.registerData(EarthBlockEntity.class, TrackedDataHandlerRegistry.VECTOR3F);
    private static final TrackedData<Boolean> USES_OFFSET = DataTracker.registerData(EarthBlockEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IS_COLLIDABLE = DataTracker.registerData(EarthBlockEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Float> MOVEMENT_SPEED = DataTracker.registerData(EarthBlockEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> DAMAGE = DataTracker.registerData(EarthBlockEntity.class, TrackedDataHandlerRegistry.FLOAT);

    private boolean drops = true, damageOnTouch = false, shiftToFreeze = true, dropOnLifeTime = false;
    private int lifeTime = -1;



    public EarthBlockEntity(EntityType<EarthBlockEntity> type, World world) {
        super(type, world);
    }

    public EarthBlockEntity(World world, LivingEntity owner) {
        super(EARTHBLOCK, world);
        setOwner(owner);
        setPos(owner.getX(), owner.getY(), owner.getZ());
    }

    public EarthBlockEntity(World world, LivingEntity owner, double x, double y, double z) {
        super(EARTHBLOCK, world);
        setOwner(owner);
        setPos(x, y, z);
        setControlled(true);
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(OWNER_ID, 0);
        this.getDataTracker().startTracking(IS_CONTROLLED, false);
        this.getDataTracker().startTracking(MODEL_SHAPE_ID, 0);
        this.getDataTracker().startTracking(BLOCK_STATE, Blocks.AIR.getDefaultState());
        this.getDataTracker().startTracking(TARGET_POSITION, new Vector3f(0, -50, 0));
        this.getDataTracker().startTracking(USES_OFFSET, false);
        this.getDataTracker().startTracking(IS_COLLIDABLE, true);
        this.getDataTracker().startTracking(MOVEMENT_SPEED, 0.1f);
        this.getDataTracker().startTracking(DAMAGE, 2f);
    }

    @Override
    public void tick() {
        super.tick();
        Entity owner = getOwner();
        if (lifeTime != -1) {
            if (lifeTime <= 0) {
                if (dropOnLifeTime) {
                    setControlled(false);
                } else {
                    discard();
                }
            }
            lifeTime--;
        }

        if (owner == null) {
            this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
            this.move(MovementType.SELF, this.getVelocity());
            if (SapsUtils.checkBlockCollision(this, 0.05f, false) != null) {
                collidesWithGround();
            }
            return;
        }

        List<ProjectileEntity> projectiles = getWorld().getEntitiesByClass(ProjectileEntity.class,
                getWorld().isClient ? getBoundingBox().expand(.25f) : getBoundingBox().offset(getPos()).expand(.25f),
                ProjectileEntity::isAlive);

        for (ProjectileEntity e : projectiles) {
            if (!(e instanceof EarthBlockEntity)) {
                e.discard();
            }
        }

        if (damageOnTouch) {
            List<LivingEntity> entities = getWorld().getEntitiesByClass(LivingEntity.class,
                    getBoundingBox().expand(0.25f),
                    LivingEntity::isAlive);

            for (LivingEntity e : entities) {
                if(!e.equals(owner)){
                    e.damage(this.getDamageSources().playerAttack((PlayerEntity) owner), getDamage());
                }
                e.setVelocity(this.getVelocity().multiply(1.2f));
                e.velocityModified = true;
                e.move(MovementType.SELF,e.getVelocity());
            }
        }

        if (!getIsControlled()) {
            HitResult hit = ProjectileUtil.getCollision(this, entity -> entity instanceof LivingEntity);
            if (hit.getType() == HitResult.Type.ENTITY) {
                LivingEntity entity = (LivingEntity) ((EntityHitResult) hit).getEntity();
                entity.damage(this.getDamageSources().playerAttack((PlayerEntity) owner), getDamage());
                entity.setVelocity(this.getVelocity().multiply(1.2f));
                entity.move(MovementType.SELF,entity.getVelocity());
                entity.velocityModified = true;
                discard();
            }
        }

        this.getWorld().getEntitiesByType(TypeFilter.instanceOf(PlayerEntity.class), this.getBoundingBox(), EntityPredicates.canBePushedBy(this)).forEach(this::pushAway);
        if (!(owner.isSneaking() && shiftToFreeze)) {
            moveEntity(owner);
        }

    }

    private void moveEntity(Entity owner) {


        //gravity
        this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));

        if (getIsControlled()) {
            controlEntity(owner);
        } else if (!getWorld().isClient && SapsUtils.checkBlockCollision(this, 0.05f, false) != null) {
            if (getVelocity().lengthSquared() > 0.3) {
                setVelocity(getVelocity().add(getVelocity().multiply(!(getModelShapeId() == 1) ? -0.5f : -0.1f)));
            } else {
                collidesWithGround();
            }
        }

        this.move(MovementType.SELF, this.getVelocity());
    }

    private void controlEntity(Entity owner) {
        Vector3f target = getTargetPosition();
        Vector3f direction = (target.y == -50 || usesOffset() ?
                getEntityLookVector(owner, 3) : new Vec3d(target.x, target.y, target.z))
                .toVector3f();

        if (usesOffset()) {
            direction.add(target);
        }
        direction = direction.sub(0, 0.5f, 0)
                .sub(getPos().toVector3f())
                .mul(getMovementSpeed());

        if (direction.length() < 0.4f) {
            this.setVelocity(0, 0, 0);
        } else if (getVelocity().length() > 1) {
            this.setVelocity(0, 0, 0);
        }


        this.addVelocity(direction.x, direction.y, direction.z);
    }

    @Override
    public boolean isCollidable() {
        return !(getModelShapeId() == 1) && isEntityCollidable();
    }

    @Override
    public boolean canHit() {
        return true;
    }

    public void collidesWithGround() {
        if (!(getModelShapeId() == 1) && drops) {
            getWorld().setBlockState(
                    new BlockPos(
                            getBlockX(),
                            (int) Math.round(getY()),
                            getBlockZ()
                    ),
                    getBlockState());
        }
        discard();
    }

    @Override
    public void onRemoved() {
        super.onRemoved();
        //TODO make this drop the block's item or place the block
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

    public BlockState getBlockState() {
        return this.getDataTracker().get(BLOCK_STATE);
    }

    public void setBlockState(BlockState state) {
        this.getDataTracker().set(BLOCK_STATE, state);
    }

    public void setModelShapeId(int val) {
        this.getDataTracker().set(MODEL_SHAPE_ID, val);
    }

    public int getModelShapeId() {
        return this.getDataTracker().get(MODEL_SHAPE_ID);
    }

    public Vector3f getTargetPosition() {
        return this.getDataTracker().get(TARGET_POSITION);
    }

    public void setTargetPosition(Vector3f pos) {
        this.getDataTracker().set(TARGET_POSITION, pos);
    }

    public void setUseOffset(boolean val) {
        this.getDataTracker().set(USES_OFFSET, val);
    }

    public boolean usesOffset() {
        return this.getDataTracker().get(USES_OFFSET);
    }

    public void setCollidable(boolean val) {
        this.getDataTracker().set(IS_COLLIDABLE, val);
    }

    public boolean isEntityCollidable() {
        return this.getDataTracker().get(IS_COLLIDABLE);
    }

    public void setDrops(boolean val) {
        drops = val;
    }

    public boolean getDrops() {
        return drops;
    }

    public int getLifeTime() {
        return lifeTime;
    }

    public void setLifeTime(int lifeTime) {
        this.lifeTime = lifeTime;
    }

    public boolean damagesOnTouch() {
        return damageOnTouch;
    }

    public void setDamageOnTouch(boolean damageOnTouch) {
        this.damageOnTouch = damageOnTouch;
    }

    public boolean isShiftToFreeze() {
        return shiftToFreeze;
    }

    public void setShiftToFreeze(boolean shiftToFreeze) {
        this.shiftToFreeze = shiftToFreeze;
    }

    public float getMovementSpeed() {
        return getDataTracker().get(MOVEMENT_SPEED);
    }

    public void setMovementSpeed(float speed) {
        getDataTracker().set(MOVEMENT_SPEED, speed);
    }

    public boolean dropsOnEndOfLife() {
        return dropOnLifeTime;
    }

    public void setDropOnEndOfLife(boolean val) {
        this.dropOnLifeTime = val;
    }

    public float getDamage() {
        return getDataTracker().get(DAMAGE);
    }

    public void setDamage(float dmg) {
        getDataTracker().set(DAMAGE, dmg);
    }

    protected void pushAway(Entity entity) {
        entity.pushAwayFrom(this);
    }
}
