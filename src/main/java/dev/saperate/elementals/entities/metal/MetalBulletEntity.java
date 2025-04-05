package dev.saperate.elementals.entities.metal;

import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import dev.saperate.elementals.utils.MathHelper;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Matrix3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static dev.saperate.elementals.Elementals.WIND_BURST_SOUND_EVENT;
import static dev.saperate.elementals.Elementals.WIND_SOUND_EVENT;
import static dev.saperate.elementals.entities.ElementalEntities.AIRBULLET;
import static dev.saperate.elementals.entities.ElementalEntities.METALBULLET;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

public class MetalBulletEntity extends AbstractElementalsEntity<PlayerEntity> {
    private static final TrackedData<Integer> ARRAY_ID = DataTracker.registerData(MetalBulletEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> ARRAY_SIZE = DataTracker.registerData(MetalBulletEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public MetalBulletEntity(EntityType<MetalBulletEntity> type, World world) {
        super(type, world, PlayerEntity.class);
    }

    public MetalBulletEntity(World world, PlayerEntity owner) {
        super(METALBULLET, world, PlayerEntity.class);
        setOwner(owner);
        setPos(owner.getX(), owner.getY(), owner.getZ());
    }

    public MetalBulletEntity(World world, PlayerEntity owner, double x, double y, double z) {
        super(METALBULLET, world, PlayerEntity.class);
        setOwner(owner);
        setPos(x, y, z);
        setControlled(true);
        setNoGravity(true);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(ARRAY_ID, 0);
        builder.add(ARRAY_SIZE, 1);
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
        float radius = 1.15f;
        Vec3d lookPos = getEntityLookVector(owner, 3);
        Vec3d dirLook = lookPos.normalize().subtract(owner.getPos());

        //Pitch
        float cosAngleX = (float) Math.cos(Math.toRadians(owner.getPitch()));
        float sinAngleX = (float) Math.sin(Math.toRadians(owner.getPitch()));

        //Roll
        float cosAngleY = (float) Math.cos(Math.toRadians(90));
        float sinAngleY = (float) Math.sin(Math.toRadians(90));

        //Yaw
        float cosAngleZ = (float) Math.cos(Math.toRadians(90));
        float sinAngleZ = (float) Math.sin(Math.toRadians(90));

        //https://en.wikipedia.org/wiki/Rotation_matrix#General_3D_rotations
        Matrix3f rot = new Matrix3f(
                cosAngleY * cosAngleZ, sinAngleX * sinAngleY * cosAngleZ - cosAngleX * sinAngleZ, cosAngleX * sinAngleY * cosAngleZ + sinAngleX * sinAngleZ,
                cosAngleY * sinAngleZ, sinAngleX * sinAngleY * cosAngleZ + cosAngleX * sinAngleZ, cosAngleX * sinAngleY * cosAngleZ - sinAngleX * sinAngleZ,
                -sinAngleY, sinAngleX * cosAngleY, cosAngleX * cosAngleY
        );

        float po = (float) ((2 * Math.PI) / getArraySize() * getArrayId());
        Vector3f v = new Vector3f((float) Math.cos(po) * radius, 0, (float) Math.sin(po) * radius);
        moveEntityTowardsGoal(
                v.mul(rot).add(lookPos.toVector3f())
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

        float damage = 1;
        if (plrData.canUseUpgrade("airBulletsMastery")) {
            damage = 4;
        } else if (plrData.canUseUpgrade("airBulletsDamageI")) {
            damage = 2;
        }
        entity.damage(this.getDamageSources().playerAttack(owner), damage);
        if (!getIsControlled()) {
            entity.addVelocity(this.getVelocity().multiply(1.2f));
            discard();
        }
    }

    @Override
    public void onRemoved() {
        //TODO make shard particles
        summonParticles(this, random, ParticleTypes.POOF, 0.01f, 10);
        this.getWorld().playSound(getX(), getY(), getZ(), WIND_BURST_SOUND_EVENT, SoundCategory.BLOCKS, 0.1f, (1.0f + (this.getWorld().random.nextFloat() - this.getWorld().random.nextFloat()) * 0.2f) * 0.7f, true);

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

}
