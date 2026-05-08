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
import net.minecraft.entity.MoverType;
import net.minecraft.entity.data.SynchedEntityData;
import net.minecraft.entity.data.EntityDataAccessor;
import net.minecraft.entity.data.EntityDataSerializers;
import net.minecraft.entity.player.Player;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundSource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.Vec3;
import net.minecraft.world.Level;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static dev.saperate.elementals.Elementals.*;
import static dev.saperate.elementals.entities.ElementalEntities.AIRBULLET;
import static dev.saperate.elementals.entities.ElementalEntities.METALBULLET;
import static dev.saperate.elementals.utils.SapsUtils.*;

public class MetalBulletEntity extends AbstractElementalsEntity<Player> {
    private static final EntityDataAccessor<Integer> ARRAY_ID = SynchedEntityData.defineId(MetalBulletEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ARRAY_SIZE = SynchedEntityData.defineId(MetalBulletEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DAMAGE_MULTIPLIER = SynchedEntityData.defineId(MetalBulletEntity.class, EntityDataSerializers.FLOAT);

    public MetalBulletEntity(EntityType<MetalBulletEntity> type, Level world) {
        super(type, world, Player.class);
    }
    public MetalBulletEntity(Level world, Player owner, double x, double y, double z) {
        super(METALBULLET, world, Player.class);
        setOwner(owner);
        setPos(x, y, z);
        setControlled(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ARRAY_ID, 0);
        builder.define(ARRAY_SIZE, 1);
        builder.define(DAMAGE_MULTIPLIER,1f);
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

        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    private void controlEntity(Entity owner) {
        double angle = (float) (((2 * Math.PI ) / getArraySize() * getArrayId()) //base angle difference
                + tickCount * 0.025f//offset
        );
        Vector3f vDir = new Vector3f(
                (float) Math.cos(angle) * 0.75f,
                (float) Math.sin(angle) * 0.75f,
                0//Forwards
        );

        Vec3 lookPos = getEntityLookVector(owner, 2).add(0,0.5f,0);
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
        Player owner = (Player) getOwner();
        PlayerData plrData = PlayerData.get(owner);

        float damage = 3;
        if (plrData.canUseUpgrade("metalBulletDamageI")) {
            damage = 6;
        }
        entity.hurt(this.damageSources().playerAttack(owner), damage 
                * getDamageMultiplier() //Used with scattershot, otherwise should be 1
                * ElementalConfig.get().BENDING_DAMAGE_MULTIPLIER);
        entity.timeUntilRegen = 10;
        if (!getIsControlled()) {
            entity.addDeltaMovement(this.getDeltaMovement().multiply(0));
            discard();
        }
    }

    @Override
    public void onClientRemoval() {
        summonParticles(this, random, METAL_SHARD_PARTICLE_TYPE, 
                0.1f, 5);
        this.level().playSound(getX(), getY(), getZ(),
                METAL_BREAK_SOUND_EVENT, SoundSource.BLOCKS,
                .15f, (2.5f + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2f) * 0.3f, 
                true);
    }

    public void setArrayId(int val) {
        this.getEntityData().set(ARRAY_ID, val);
    }

    public int getArrayId() {
        return this.getEntityData().get(ARRAY_ID);
    }

    /**
     * @param val The amount of entities in the same batch + 1
     */
    public void setArraySize(int val) {
        this.getEntityData().set(ARRAY_SIZE, val);
    }

    public int getArraySize() {
        return this.getEntityData().get(ARRAY_SIZE);
    }

    public void setDamageMultiplier(float val) {
        this.getEntityData().set(DAMAGE_MULTIPLIER, val);
    }

    public float getDamageMultiplier() {
        return this.getEntityData().get(DAMAGE_MULTIPLIER);
    }

    @Override
    public boolean pushesEntitiesAway() {
        return false;
    }
}
