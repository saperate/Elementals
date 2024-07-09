package dev.saperate.elementals.entities.earth;

import dev.saperate.elementals.entities.ElementalEntities;
import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
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
import net.minecraft.particle.BlockStateParticleEffect;
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

import java.util.List;

import static dev.saperate.elementals.entities.ElementalEntities.EARTHBLOCK;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

public class EarthBlockEntity extends AbstractElementalsEntity<PlayerEntity> {
    private static final TrackedData<Integer> MODEL_SHAPE_ID = DataTracker.registerData(EarthBlockEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<BlockState> BLOCK_STATE = DataTracker.registerData(EarthBlockEntity.class, TrackedDataHandlerRegistry.BLOCK_STATE);
    private static final TrackedData<Vector3f> TARGET_POSITION = DataTracker.registerData(EarthBlockEntity.class, TrackedDataHandlerRegistry.VECTOR3F);
    private static final TrackedData<Boolean> USES_OFFSET = DataTracker.registerData(EarthBlockEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IS_COLLIDABLE = DataTracker.registerData(EarthBlockEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Float> MOVEMENT_SPEED = DataTracker.registerData(EarthBlockEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> DAMAGE = DataTracker.registerData(EarthBlockEntity.class, TrackedDataHandlerRegistry.FLOAT);

    private boolean drops = true, damageOnTouch = false, shiftToFreeze = true, dropOnLifeTime = false;


    public EarthBlockEntity(EntityType<EarthBlockEntity> type, World world) {
        super(type, world, PlayerEntity.class);
    }

    public EarthBlockEntity(World world, PlayerEntity owner) {
        super(EARTHBLOCK, world, PlayerEntity.class);
        setOwner(owner);
        setPos(owner.getX(), owner.getY(), owner.getZ());
    }

    public EarthBlockEntity(World world, PlayerEntity owner, double x, double y, double z) {
        super(EARTHBLOCK, world, PlayerEntity.class);
        setOwner(owner);
        setPos(x, y, z);
        setControlled(true);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
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
        if (random.nextBetween(0, 20) == 6) {
            summonParticles(this, random,
                    new BlockStateParticleEffect(ParticleTypes.BLOCK, getBlockState()),
                    0, 1);
        }

        LivingEntity owner = getOwner();
        if (!(owner != null && owner.isSneaking() && shiftToFreeze)) {
            moveEntity(owner);
        }

    }

    public void moveEntity(Entity owner) {
        if (getIsControlled()) {
            controlEntity(owner);
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

        moveEntityTowardsGoal(direction);
    }

    @Override
    public boolean isCollidable() {
        return !(getModelShapeId() == 1) && isEntityCollidable();
    }

    @Override
    public boolean canHit() {
        return true;
    }

    @Override
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
    public float touchGroundFrictionMultiplier() {
        return !(getModelShapeId() == 1) ? -0.5f : -0.1f;
    }

    @Override
    public void onRemoved() {
        super.onRemoved();
        this.getWorld().playSound(getX(), getY(), getZ(), SoundEvents.BLOCK_STONE_BREAK, SoundCategory.BLOCKS, .5f, (1.0f + (this.getWorld().random.nextFloat() - this.getWorld().random.nextFloat()) * 0.2f) * 0.7f, false);
        //TODO make this drop the block's item or place the block
    }

    @Override
    public void onTouchEntity(Entity entity) {
        LivingEntity owner = getOwner();
        if (!entity.equals(owner)) {
            entity.damage(this.getDamageSources().playerAttack((PlayerEntity) owner), getDamage());
        }
        entity.setVelocity(this.getVelocity().multiply(1.2f));
        entity.velocityModified = true;
        entity.move(MovementType.SELF, entity.getVelocity());
    }

    @Override
    public void onHitEntity(Entity entity) {
        entity.damage(this.getDamageSources().playerAttack((PlayerEntity) getOwner()), getDamage());
        entity.setVelocity(this.getVelocity().multiply(1.2f));
        entity.move(MovementType.SELF, entity.getVelocity());
        entity.velocityModified = true;
        discard();
    }

    @Override
    public void onLifeTimeEnd() {
        if (dropsOnEndOfLife()) {
            setControlled(false);
        } else {
            discard();
        }
    }

    @Override
    public float projectileDeflectionRange() {
        return 0.25f;
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

    public void setMaxLifeTime(int maxLifeTime) {
        this.maxLifeTime = maxLifeTime;
    }

    @Override
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

    @Override
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

    @Override
    public float collisionSensitivity() {
        return 0.05f;
    }
}
