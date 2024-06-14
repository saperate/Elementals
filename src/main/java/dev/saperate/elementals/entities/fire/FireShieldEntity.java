package dev.saperate.elementals.entities.fire;

import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

import static dev.saperate.elementals.entities.ElementalEntities.FIRESHIELD;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

public class FireShieldEntity extends Entity {
    public static final int MAX_FLAME_SIZE = 3;
    private static final TrackedData<Float> FINAL_HEIGHT = DataTracker.registerData(FireShieldEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> HEIGHT = DataTracker.registerData(FireShieldEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Boolean> IS_BLUE = DataTracker.registerData(FireShieldEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> OWNER_ID = DataTracker.registerData(FireShieldEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public float prevFlameSize = 0;
    public int heightAdjustSpeed = 10;//Smaller is faster

    public FireShieldEntity(EntityType<FireShieldEntity> type, World world) {
        super(type, world);
    }

    public FireShieldEntity(World world, LivingEntity owner) {
        this(world, owner, owner.getX(), owner.getY(), owner.getZ());
    }

    public FireShieldEntity(World world, LivingEntity owner, double x, double y, double z) {
        super(FIRESHIELD, world);
        setPos(x, y, z);
        setFireHeight(MAX_FLAME_SIZE);
        setOwner(owner);
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(HEIGHT, 0.1f);
        this.getDataTracker().startTracking(FINAL_HEIGHT, 3f);
        this.getDataTracker().startTracking(IS_BLUE, false);
        this.getDataTracker().startTracking(OWNER_ID, 0);
    }

    @Override
    public void tick() {
        super.tick();

        if (random.nextBetween(0, 20) == 6) {
            playSound(SoundEvents.BLOCK_FIRE_AMBIENT, 1, 0);
        }

        Entity owner = getOwner();
        if (owner == null) {
            discard();
            return;
        }

        setPos(owner.getX(), owner.getY(), owner.getZ());


        float diff = (getFireHeight() - prevFlameSize) / heightAdjustSpeed;
        prevFlameSize += diff;
        if (prevFlameSize > MAX_FLAME_SIZE - 1) {
            setFireHeight(getFinalFireHeight());
            heightAdjustSpeed = 5;
        }
        float h = getFireHeight();

        if (getWorld().getBlockState(getBlockPos()).getBlock().equals(Blocks.WATER)
                || getWorld().getBlockState(getBlockPos().down()).isAir()) {
            this.discard();
        }

        List<ProjectileEntity> projectiles = getWorld().getEntitiesByClass(ProjectileEntity.class,
                getWorld().isClient ? getBoundingBox().expand(1.5f) : getBoundingBox().expand(1.5f).offset(getPos()),
                ProjectileEntity::isAlive);

        for (ProjectileEntity e : projectiles) {
            e.discard();
        }

        List<LivingEntity> hits = getWorld().getEntitiesByClass(LivingEntity.class,
                getWorld().isClient ? getBoundingBox().expand(1) : getBoundingBox().expand(1).offset(getPos()),
                LivingEntity::isAlive);

        for (LivingEntity entity : hits) {
            if (entity.getY() - getY() < h + 1
                    && Math.abs(entity.getPos().subtract(getPos()).length()) > 2) {
                if (!entity.isFireImmune()) {
                    entity.setOnFireFor(8);
                    entity.damage(getDamageSources().inFire(), isBlue() ? 2.5f : 1.5f);//1.5f for normal, 2.5f for blue
                }

                Vec3d direction = entity.getPos().add(0, 1.5f, 0).subtract(getPos()).multiply(0.1f);
                entity.setVelocity(getVelocity().add(direction));
            }
        }


    }


    @Override
    public boolean isCollidable() {
        return true;
    }


    public float getFireHeight() {
        return this.dataTracker.get(HEIGHT);
    }

    public void setFireHeight(float h) {
        this.getDataTracker().set(HEIGHT, h);
    }

    public boolean isBlue() {
        return this.dataTracker.get(IS_BLUE);
    }

    public void setIsBlue(boolean val) {
        this.getDataTracker().set(IS_BLUE, val);
    }


    @Override
    public boolean canHit() {
        return true;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }


    public float getFinalFireHeight() {
        return this.dataTracker.get(FINAL_HEIGHT);
    }

    public void setFinalFireHeight(float h) {
        this.getDataTracker().set(FINAL_HEIGHT, h);
    }

    public LivingEntity getOwner() {
        Entity owner = this.getWorld().getEntityById(this.getDataTracker().get(OWNER_ID));
        return (owner instanceof LivingEntity) ? (LivingEntity) owner : null;
    }

    public void setOwner(LivingEntity owner) {
        this.getDataTracker().set(OWNER_ID, owner.getId());
    }
}
