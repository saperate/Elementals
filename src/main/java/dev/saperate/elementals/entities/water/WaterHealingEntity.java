package dev.saperate.elementals.entities.water;

import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
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

public class WaterHealingEntity extends AbstractElementalsEntity {
    private static final TrackedData<Float> HEALING = DataTracker.registerData(WaterHealingEntity.class, TrackedDataHandlerRegistry.FLOAT);

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
        super.initDataTracker();
        this.getDataTracker().startTracking(HEALING, 2f);
    }

    @Override
    public void tick() {
        super.tick();

        if (random.nextBetween(0, 40) == 6) {
            summonParticles(this, random,
                    ParticleTypes.SPLASH,
                    0, 1);
            playSound(SoundEvents.ENTITY_PLAYER_SWIM, 0.25f, 0);
        }

        PlayerEntity owner = getOwner();

        if (owner != null && !isRemoved()) {
            moveEntity(owner);
        }
    }

    @Override
    public void onHitEntity(Entity entity) {
        entity.addVelocity(this.getVelocity().multiply(0.8f));
        healTarget(entity);
        discard();
    }

    @Override
    public void onTouchEntity(Entity entity) {
        if (age % 20 == 0) {
            healTarget(entity);
        }
    }

    public void healTarget(Entity entity) {
        if (entity instanceof LivingEntity living) {
            living.setHealth(living.getHealth() + getHealing());
        }
    }

    private void moveEntity(Entity owner) {
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

    @Override
    public void collidesWithGround() {
        discard();
    }

    @Override
    public void onRemoved() {
        summonParticles(this, random, ParticleTypes.SPLASH, 0, 10);
        this.getWorld().playSound(getX(), getY(), getZ(), SoundEvents.ENTITY_PLAYER_SPLASH, SoundCategory.BLOCKS, 0.25f, (1.0f + (this.getWorld().random.nextFloat() - this.getWorld().random.nextFloat()) * 0.2f) * 0.7f, false);

    }

    public void setHealing(float val) {
        this.dataTracker.set(HEALING, val);
    }

    public float getHealing() {
        return this.dataTracker.get(HEALING);
    }
}
