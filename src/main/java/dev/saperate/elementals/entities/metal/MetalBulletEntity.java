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
        float radius = 2;
        double angle = ((2 * Math.PI) / getArraySize()) * getArrayId() + Math.toRadians(age * 20);

        double yaw = Math.abs(owner.getYaw() % 360);
        if (yaw > 180) {
            yaw = 360 - yaw;
        }
        yaw -= 90;
        Vec3d dirLook = getEntityLookVector(owner, 1).subtract(owner.getPos());
        if (dirLook.x < 0) {
            yaw *= -1;
        }

        moveEntityTowardsGoal(getEntityLookVector(owner, 3).toVector3f().add(0, 0.5f, 0)
                .add(new Vector3f().add(radius, 0, 0)
                        .mul((float) (Math.cos(angle) * MathHelper.linear(1, (float) yaw/90, 1 - Math.abs(owner.getPitch()/ 90))))
                )
                .add(new Vector3f().add(0, radius, 0)
                        .mul((float) Math.sin(angle) * (1 - Math.abs(owner.getPitch() / 90)))
                )
                .add(new Vector3f().add(0, 0, radius)
                        .mul((float) Math.sin(angle) * (owner.getPitch() / 90))
                )
                .add(new Vector3f().add(0, 0, radius)
                        .mul((float) (Math.cos(angle) * (1 - Math.abs(yaw / 90))))
                )
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
