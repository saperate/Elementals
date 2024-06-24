package dev.saperate.elementals.entities.water;

import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.List;

import static dev.saperate.elementals.entities.ElementalEntities.WATERHEALING;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

public class WaterHealingEntity extends ProjectileEntity {
    private static final TrackedData<Integer> OWNER_ID = DataTracker.registerData(WaterHealingEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Float> HEALING = DataTracker.registerData(WaterHealingEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Boolean> IS_CONTROLLED = DataTracker.registerData(WaterHealingEntity.class, TrackedDataHandlerRegistry.BOOLEAN);


    public WaterHealingEntity(EntityType<WaterHealingEntity> type, World world) {
        super(type, world);
    }

    public WaterHealingEntity(World world, LivingEntity owner) {
        super(WATERHEALING, world);
        setOwner(owner);
        setPos(owner.getX(), owner.getY(), owner.getZ());
    }

    public WaterHealingEntity(World world, LivingEntity owner, double x, double y, double z) {
        super(WATERHEALING, world);
        setOwner(owner);
        setPos(x, y, z);
        setControlled(true);
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(OWNER_ID, 0);
        this.getDataTracker().startTracking(IS_CONTROLLED, false);
        this.getDataTracker().startTracking(HEALING, 2f);
    }

    @Override
    public void tick() {
        super.tick();

        if (random.nextBetween(0, 40) == 6) {
            summonParticles(this, random,
                    ParticleTypes.SPLASH,
                    0, 1);
            playSound(SoundEvents.ENTITY_PLAYER_SWIM,0.25f,0);
        }

        BlockPos blockHit = SapsUtils.checkBlockCollision(this,0.25f, false);

        PlayerEntity owner = getOwner();
        if (owner == null) {
            this.setVelocity(this.getVelocity().add(0.0, -0.03, 0.0));
            this.move(MovementType.SELF, this.getVelocity());
            if (blockHit != null) {
                collidesWithGround();
            }
            return;
        }

        if (blockHit != null && !getIsControlled()) {
            collidesWithGround();
        }

        List<LivingEntity> hits = getWorld().getEntitiesByClass(LivingEntity.class,
                getBoundingBox().expand(0.25f),
                LivingEntity::isAlive);
        HitResult hit = ProjectileUtil.getCollision(this, entity -> entity instanceof LivingEntity);

        if (hit.getType() == HitResult.Type.ENTITY) {
            LivingEntity entity = (LivingEntity) ((EntityHitResult) hit).getEntity();
            onHitEntity(entity);
        }

        for (LivingEntity e : hits) {
            onHitEntity(e);
        }


        moveEntity(owner);
    }

    private void onHitEntity(LivingEntity entity) {
        if (age % 20 == 0) {
            entity.setHealth(entity.getHealth() + getHealing());
        }

        if (!getIsControlled()) {
            entity.addVelocity(this.getVelocity().multiply(0.8f));
            discard();
        }
    }

    private void moveEntity(Entity owner) {


        //gravity
        this.setVelocity(this.getVelocity().add(0.0, -0.02, 0.0));

        if (getIsControlled()) {
            controlEntity(owner);
        }


        this.move(MovementType.SELF, this.getVelocity());
    }

    private void controlEntity(Entity owner) {
        float distance = 3;
        if (owner.isSneaking()) {
            distance = 6;
        }
        Vector3f direction = getEntityLookVector(owner, distance)
                .subtract(0, 0.25f, 0)
                .subtract(getPos()).toVector3f();
        direction.mul(0.25f);

        if (direction.length() < 0.6f) {
            this.setVelocity(0, 0, 0);
        }


        this.addVelocity(direction.x, direction.y, direction.z);
    }

    public void collidesWithGround() {
        //getWorld().setBlockState(getBlockPos(), Blocks.WATER.getDefaultState());
        discard();
    }

    @Override
    public void onRemoved() {
        summonParticles(this, random, ParticleTypes.SPLASH, 0, 10);
        this.getWorld().playSound(getX(), getY(), getZ(), SoundEvents.ENTITY_PLAYER_SPLASH, SoundCategory.BLOCKS, 0.25f, (1.0f + (this.getWorld().random.nextFloat() - this.getWorld().random.nextFloat()) * 0.2f) * 0.7f, false);

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

    public PlayerEntity getOwner() {
        Entity owner = this.getWorld().getEntityById(this.getDataTracker().get(OWNER_ID));
        return (owner instanceof PlayerEntity) ? (PlayerEntity) owner : null;
    }

    public void setOwner(LivingEntity owner) {
        this.getDataTracker().set(OWNER_ID, owner.getId());
    }

    public void setHealing(float val) {
        this.dataTracker.set(HEALING,val);
    }

    public float getHealing() {
        return this.dataTracker.get(HEALING);
    }
}
