package dev.saperate.elementals.entities.fire;

import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

import java.util.List;

import static dev.saperate.elementals.entities.ElementalEntities.FIREBLOCK;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;
import static net.minecraft.entity.projectile.ProjectileUtil.getEntityCollision;

public class FireBlockEntity extends Entity {
    public static final int MAX_FLAME_SIZE = 5;
    private static final TrackedData<Float> FINAL_HEIGHT = DataTracker.registerData(FireBlockEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> HEIGHT = DataTracker.registerData(FireBlockEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Boolean> IS_BLUE = DataTracker.registerData(FireBlockEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public int lifeTime = 0;
    public float prevFlameSize = 0;
    public int heightAdjustSpeed = 10;//Smaller is faster


    public FireBlockEntity(EntityType<FireBlockEntity> type, World world) {
        super(type, world);
    }

    public FireBlockEntity(World world, LivingEntity owner) {
        this(world, owner, owner.getX(), owner.getY(), owner.getZ());
    }

    public FireBlockEntity(World world, LivingEntity owner, double x, double y, double z) {
        super(FIREBLOCK, world);
        setPos(x, y, z);
        lifeTime = 200;
        setFireHeight(MAX_FLAME_SIZE);
    }


    @Override
    public boolean damage(DamageSource source, float amount) {
        Entity entity = source.getSource();
        if (entity instanceof ProjectileEntity) {
            entity.discard();
        }
        return super.damage(source, amount);

    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(HEIGHT, 1f);
        this.getDataTracker().startTracking(FINAL_HEIGHT, 1.5f);
        this.getDataTracker().startTracking(IS_BLUE, false);
    }

    @Override
    public void tick() {
        super.tick();
        if (random.nextBetween(0, 20) == 6) {
            playSound(SoundEvents.BLOCK_FIRE_AMBIENT, 1, 0);
        }

        lifeTime--;
        if (lifeTime <= 0 && !getWorld().isClient) {
            discard();
            return;
        }
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
                getWorld().isClient ? getBoundingBox().expand(.25f) : getBoundingBox().offset(getPos()).expand(.25f),
                ProjectileEntity::isAlive);

        for (ProjectileEntity e : projectiles) {
            e.discard();
        }

        List<LivingEntity> hits = getWorld().getEntitiesByClass(LivingEntity.class, getWorld().isClient ? getBoundingBox() : getBoundingBox().offset(getPos()), LivingEntity::isAlive);

        for (LivingEntity entity : hits) {
            if (!entity.isFireImmune() && entity.getY() - getY() < h) {
                entity.setOnFireFor(8);
                entity.damage(getDamageSources().inFire(), isBlue() ? 2.5f : 1.5f);
            }
        }
    }

    @Override
    public void onDataTrackerUpdate(List<DataTracker.SerializedEntry<?>> dataEntries) {
        super.onDataTrackerUpdate(dataEntries);
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
        nbt.putInt("lifeTime", lifeTime);
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        if (nbt.contains("lifeTime")) {
            lifeTime = nbt.getInt("lifeTime");
        }
    }

    public int getLifeTime() {
        return lifeTime;
    }

    public float getFinalFireHeight() {
        return this.dataTracker.get(FINAL_HEIGHT);
    }

    public void setFinalFireHeight(float h) {
        this.getDataTracker().set(FINAL_HEIGHT, h);
    }
}
