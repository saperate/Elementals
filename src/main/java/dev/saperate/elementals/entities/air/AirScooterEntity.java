package dev.saperate.elementals.entities.air;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.FireExplosion;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.joml.Vector3f;

import static dev.saperate.elementals.Elementals.WIND_BURST_SOUND_EVENT;
import static dev.saperate.elementals.Elementals.WIND_SOUND_EVENT;
import static dev.saperate.elementals.entities.ElementalEntities.AIRBALL;
import static dev.saperate.elementals.entities.ElementalEntities.AIRSCOOTER;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

public class AirScooterEntity extends Entity {
    private static final TrackedData<Integer> OWNER_ID = DataTracker.registerData(AirScooterEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Float> SPEED = DataTracker.registerData(AirScooterEntity.class, TrackedDataHandlerRegistry.FLOAT);

    public AirScooterEntity(EntityType<AirScooterEntity> type, World world) {
        super(type, world);
    }

    public AirScooterEntity(World world, PlayerEntity owner) {
        this(world, owner, owner.getX(), owner.getY(), owner.getZ());
    }

    public AirScooterEntity(World world, PlayerEntity owner, double x, double y, double z) {
        super(AIRSCOOTER, world);
        setPos(x, y, z);
        setOwner(owner);
        setStepHeight(1.1f);
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(OWNER_ID, 0);
        this.getDataTracker().startTracking(SPEED, 0.5f);
    }

    @Override
    public void tick() {
        if (random.nextBetween(0, 40) == 6) {
            playSound(WIND_SOUND_EVENT, 1, (1.0f + (this.getWorld().random.nextFloat() - this.getWorld().random.nextFloat()) * 0.2f) * 0.7f);
        }
        if (isOnGround()) {
            playStepSound(getBlockPos().down(), getWorld().getBlockState(getBlockPos().down()));
        }

        summonParticles(this, random,
                ParticleTypes.POOF,
                0, 1);
        moveEntity();
    }

    private void moveEntity() {
        PlayerEntity player = getOwner();
        if (player == null || isOnFire() || player.isSneaking()) {
            discard();
            return;
        }
        if (!player.equals(getFirstPassenger())) {
            player.startRiding(this);
        }
        player.fallDistance = 0;

        int dx = (int) Math.round(-Math.sin(Math.toRadians(player.getYaw())));
        int dz = (int) Math.round(Math.cos(Math.toRadians(player.getYaw())));

        this.addVelocity(dx, 0, dz);
        this.setVelocity(getVelocity().normalize().multiply(getSpeed()));
        if (!isSubmergedInWater()) {
            //gravity
            this.setVelocity(this.getVelocity().add(0.0, -0.5, 0.0));
        } else {
            this.setVelocity(this.getVelocity().multiply(1, 0, 1));
        }


        this.move(MovementType.SELF, this.getVelocity());
    }


    @Override
    public void onRemoved() {
        summonParticles(this, random,
                ParticleTypes.POOF,
                0.25f, 25);
        this.getWorld().playSound(getX(), getY(), getZ(), WIND_BURST_SOUND_EVENT, SoundCategory.BLOCKS, 1, (1.0f + (this.getWorld().random.nextFloat() - this.getWorld().random.nextFloat()) * 0.2f) * 0.7f, true);
        if(!getWorld().isClient && getOwner() != null){
            Bender.getBender(getOwner()).currAbility.onRemove(Bender.getBender(getOwner()));
        }
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }

    public PlayerEntity getOwner() {
        Entity owner = this.getWorld().getEntityById(this.getDataTracker().get(OWNER_ID));
        return (owner instanceof PlayerEntity) ? (PlayerEntity) owner : null;
    }

    public void setOwner(PlayerEntity owner) {
        this.getDataTracker().set(OWNER_ID, owner.getId());
    }

    public void setSpeed(float speed) {
        this.dataTracker.set(SPEED, speed);
    }

    public float getSpeed() {
        return this.dataTracker.get(SPEED);
    }
}
