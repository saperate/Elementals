package dev.saperate.elementals.entities.metal;

import dev.saperate.elementals.Elementals;
import dev.saperate.elementals.data.ElementalConfig;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import dev.saperate.elementals.utils.MathHelper;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static dev.saperate.elementals.Elementals.*;
import static dev.saperate.elementals.entities.ElementalEntities.AIRBULLET;
import static dev.saperate.elementals.entities.ElementalEntities.METALBULLET;
import static dev.saperate.elementals.utils.SapsUtils.*;

public class MetalBulletEntity extends AbstractElementalsEntity<PlayerEntity> {
    private static final TrackedData<Integer> ARRAY_ID = DataTracker.registerData(MetalBulletEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> ARRAY_SIZE = DataTracker.registerData(MetalBulletEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Float> DAMAGE_MULTIPLIER = DataTracker.registerData(MetalBulletEntity.class, TrackedDataHandlerRegistry.FLOAT);

    public MetalBulletEntity(EntityType<MetalBulletEntity> type, World world) {
        super(type, world, PlayerEntity.class);
    }
    public MetalBulletEntity(World world, PlayerEntity owner, double x, double y, double z) {
        super(METALBULLET, world, PlayerEntity.class);
        setOwner(owner);
        setPos(x, y, z);
        setControlled(true);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.getDataTracker().startTracking(ARRAY_ID, 0);
        this.getDataTracker().startTracking(ARRAY_SIZE, 1);
        this.getDataTracker().startTracking(DAMAGE_MULTIPLIER,1f);
    }

    @Override
    public void tick() {
        super.tick();
        LivingEntity owner = getOwner();
        if (owner == null) {
            return;
        }

        moveEntity(owner);
    }


    private void moveEntity(Entity owner) {
        if (getIsControlled()) {
            controlEntity(owner);
        }

        this.move(MovementType.SELF, this.getVelocity());
    }

    private void controlEntity(Entity owner) {
        double angle = (float) (((2 * Math.PI ) / getArraySize() * getArrayId()) //base angle difference
                + age * 0.025f//offset
        );
        Vector3f vDir = new Vector3f(
                (float) Math.cos(angle) * 0.75f,
                (float) Math.sin(angle) * 0.75f,
                0//Forwards
        );

        Vec3d lookPos = getEntityLookVector(owner, 2).add(0,0.5f,0);
        float pitchCorrection = SapsUtils.isLookingForwards(lookPos.subtract(owner.getPos()).toVector3f()) ? -1 : 1;
        Quaternionf rotation = new Quaternionf()
                .rotationXYZ(
                        (float) Math.toRadians(owner.getPitch() * pitchCorrection),
                        (float) Math.toRadians(-owner.getYaw()),
                        0//Roll
                );

        moveEntityTowardsGoal(
                vDir.rotate(rotation).add(lookPos.toVector3f())
        );
    }

    @Override
    public void collidesWithGround() {
        discard();
    }

    @Override
    public void onHitEntity(Entity entity) {
        PlayerEntity owner = (PlayerEntity) getOwner();
        PlayerData plrData = PlayerData.get(owner);

        float damage = 2.5f;
        if (plrData.canUseUpgrade("airBulletsMastery")) {
            damage = 1;
        } else if (plrData.canUseUpgrade("airBulletsDamageI")) {
            damage = 1.75f;
        }
        entity.damage(this.getDamageSources().playerAttack(owner), damage * ElementalConfig.get().BENDING_DAMAGE_MULTIPLIER);
        entity.timeUntilRegen = 10;
        if (!getIsControlled()) {
            entity.addVelocity(this.getVelocity().multiply(0));
            discard();
        }
    }

    @Override
    public void onRemoved() {
        summonParticles(this, random, METAL_SHARD_PARTICLE_TYPE, 
                0.1f, 5);
        this.getWorld().playSound(getX(), getY(), getZ(),
                METAL_BREAK_SOUND_EVENT, SoundCategory.BLOCKS,
                    .15f, (2.5f + (this.getWorld().random.nextFloat() - this.getWorld().random.nextFloat()) * 0.2f) * 0.3f, 
                true);
    }

    public void setArrayId(int val) {
        this.getDataTracker().set(ARRAY_ID, val);
    }

    public int getArrayId() {
        return this.getDataTracker().get(ARRAY_ID);
    }

    /**
     * @param val The amount of entities in the same batch + 1
     */
    public void setArraySize(int val) {
        this.getDataTracker().set(ARRAY_SIZE, val);
    }

    public int getArraySize() {
        return this.getDataTracker().get(ARRAY_SIZE);
    }

    public void setDamageMultiplier(float val) {
        this.getDataTracker().set(DAMAGE_MULTIPLIER, val);
    }

    public float getDamageMultiplier() {
        return this.getDataTracker().get(DAMAGE_MULTIPLIER);
    }

    @Override
    public boolean pushesEntitiesAway() {
        return false;
    }
}
