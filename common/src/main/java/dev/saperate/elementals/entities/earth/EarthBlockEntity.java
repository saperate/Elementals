package dev.saperate.elementals.entities.earth;

import dev.saperate.elementals.data.ElementalConfig;
import dev.saperate.elementals.entities.ElementalEntities;
import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.data.SynchedEntityData;
import net.minecraft.entity.data.EntityDataAccessor;
import net.minecraft.entity.data.EntityDataSerializers;
import net.minecraft.entity.player.Player;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.sound.SoundSource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3;
import net.minecraft.world.Level;
import org.joml.Vector3f;

import java.util.List;

import static dev.saperate.elementals.Elementals.BENDING_GRIEFING;
import static dev.saperate.elementals.entities.ElementalEntities.EARTHBLOCK;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

public class EarthBlockEntity extends AbstractElementalsEntity<Player> {
    private static final EntityDataAccessor<Integer> MODEL_SHAPE_ID = SynchedEntityData.defineId(EarthBlockEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<BlockState> BLOCK_STATE = SynchedEntityData.defineId(EarthBlockEntity.class, EntityDataSerializers.BLOCK_STATE);
    private static final EntityDataAccessor<Vector3f> TARGET_POSITION = SynchedEntityData.defineId(EarthBlockEntity.class, EntityDataSerializers.VECTOR3F);
    private static final EntityDataAccessor<Boolean> USES_OFFSET = SynchedEntityData.defineId(EarthBlockEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_COLLIDABLE = SynchedEntityData.defineId(EarthBlockEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> MOVEMENT_SPEED = SynchedEntityData.defineId(EarthBlockEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DAMAGE = SynchedEntityData.defineId(EarthBlockEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> SHIFT_FREEZE = SynchedEntityData.defineId(EarthBlockEntity.class, EntityDataSerializers.BOOLEAN);

    private boolean drops = true, damageOnTouch = false, dropOnLifeTime = false;


    public EarthBlockEntity(EntityType<EarthBlockEntity> type, Level world) {
        super(type, world, Player.class);
    }

    public EarthBlockEntity(Level world, Player owner) {
        super(EARTHBLOCK, world, Player.class);
        setOwner(owner);
        setPos(owner.getX(), owner.getY(), owner.getZ());
    }

    public EarthBlockEntity(Level world, Player owner, double x, double y, double z) {
        super(EARTHBLOCK, world, Player.class);
        setOwner(owner);
        setPos(x, y, z);
        setControlled(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(MODEL_SHAPE_ID, 0);
        builder.define(BLOCK_STATE, Blocks.AIR.getDefaultState());
        builder.define(TARGET_POSITION, new Vector3f(0, -1000, 0));
        builder.define(USES_OFFSET, false);
        builder.define(IS_COLLIDABLE, true);
        builder.define(MOVEMENT_SPEED, 0.1f);
        builder.define(DAMAGE, 2f);
        builder.define(SHIFT_FREEZE, true);
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
        if (!(owner != null && owner.isCrouching() && isShiftToFreeze())) {
            moveEntity(owner);
        }

    }

    public void moveEntity(Entity owner) {
        if (getIsControlled()) {
            controlEntity(owner);
        }

        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    private void controlEntity(Entity owner) {
        if (owner == null) {
            setControlled(false);
            return;
        }
        Vector3f target = getTargetPosition();
        Vector3f direction = (target.y == -1000 || usesOffset() ?
                getEntityLookVector(owner, 3) : new Vec3(target.x, target.y, target.z))
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
    public boolean isPickable() {
        return true;
    }

    @Override
    public void collidesWithGround() {
        if (!(getModelShapeId() == 1) && drops && level().getGameRules().getBoolean(BENDING_GRIEFING)) {
            level().setBlockState(
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
    public void onClientRemoval() {
        super.onClientRemoval();
        this.level().playSound(getX(), getY(), getZ(), SoundEvents.BLOCK_STONE_BREAK, SoundSource.BLOCKS, .5f, (1.0f + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2f) * 0.7f, false);
        //TODO make this drop the block's item or place the block
    }

    @Override
    public void onTouchEntity(Entity entity) {
        LivingEntity owner = getOwner();
        if (!entity.equals(owner)) {
            entity.hurt(this.damageSources().playerAttack((Player) owner), getDamage() * ElementalConfig.get().BENDING_DAMAGE_MULTIPLIER);
        } else {
            entity.fallDistance = 0;
        }
        entity.setDeltaMovement(this.getDeltaMovement().multiply(1.2f));
        entity.velocityModified = true;
        entity.move(MoverType.SELF, entity.getDeltaMovement());
    }

    @Override
    public void onHitEntity(Entity entity) {
        entity.fallDistance = 0;
        entity.hurt(this.damageSources().playerAttack((Player) getOwner()), getDamage() * ElementalConfig.get().BENDING_DAMAGE_MULTIPLIER);
        entity.addDeltaMovement(this.getDeltaMovement().multiply(0.5));
        entity.move(MoverType.SELF, entity.getDeltaMovement());
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
        return this.getEntityData().get(BLOCK_STATE);
    }

    public void setBlockState(BlockState state) {
        this.getEntityData().set(BLOCK_STATE, state);
    }

    public void setModelShapeId(int val) {
        this.getEntityData().set(MODEL_SHAPE_ID, val);
    }

    public int getModelShapeId() {
        return this.getEntityData().get(MODEL_SHAPE_ID);
    }

    public Vector3f getTargetPosition() {
        return this.getEntityData().get(TARGET_POSITION);
    }

    public void setTargetPosition(Vector3f pos) {
        this.getEntityData().set(TARGET_POSITION, pos);
    }

    public void setUseOffset(boolean val) {
        this.getEntityData().set(USES_OFFSET, val);
    }

    public boolean usesOffset() {
        return this.getEntityData().get(USES_OFFSET);
    }

    public void setCollidable(boolean val) {
        this.getEntityData().set(IS_COLLIDABLE, val);
    }

    public boolean isEntityCollidable() {
        return this.getEntityData().get(IS_COLLIDABLE);
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

    public void setShiftToFreeze(boolean val) {
        this.getEntityData().set(SHIFT_FREEZE, val);
    }

    public boolean isShiftToFreeze() {
        return this.getEntityData().get(SHIFT_FREEZE);
    }

    @Override
    public float getMovementSpeed() {
        return getEntityData().get(MOVEMENT_SPEED);
    }

    public void setMovementSpeed(float speed) {
        getEntityData().set(MOVEMENT_SPEED, speed);
    }

    public boolean dropsOnEndOfLife() {
        return dropOnLifeTime;
    }

    public void setDropOnEndOfLife(boolean val) {
        this.dropOnLifeTime = val;
    }

    public float getDamage() {
        return getEntityData().get(DAMAGE);
    }

    public void setDamage(float dmg) {
        getEntityData().set(DAMAGE, dmg);
    }

    @Override
    public float collisionSensitivity() {
        return 0.01f;
    }
}
