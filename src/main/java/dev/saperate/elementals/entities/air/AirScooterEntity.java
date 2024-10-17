package dev.saperate.elementals.entities.air;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;

import static dev.saperate.elementals.Elementals.WIND_BURST_SOUND_EVENT;
import static dev.saperate.elementals.Elementals.WIND_SOUND_EVENT;
import static dev.saperate.elementals.entities.ElementalEntities.AIRSCOOTER;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

public class AirScooterEntity extends AbstractElementalsEntity<PlayerEntity> {
    private static final TrackedData<Float> SPEED = DataTracker.registerData(AirScooterEntity.class, TrackedDataHandlerRegistry.FLOAT);

    public AirScooterEntity(EntityType<AirScooterEntity> type, World world) {
        super(type, world, PlayerEntity.class);
    }

    public AirScooterEntity(World world, PlayerEntity owner) {
        this(world, owner, owner.getX(), owner.getY(), owner.getZ());
    }

    public AirScooterEntity(World world, PlayerEntity owner, double x, double y, double z) {
        super(AIRSCOOTER, world, PlayerEntity.class);
        setPos(x, y, z);
        setOwner(owner);
        setStepHeight(1.1f);
        setNoGravity(true);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.getDataTracker().startTracking(SPEED, 0.5f);
    }

    @Override
    public void tick() {
        super.tick();
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
        LivingEntity player = getOwner();

        if(player == null || player.isRemoved()){
            return;
        }

        if (isOnFire() || player.isSneaking()) {
            player.setSneaking(false);
            discard();
            return;
        }
        if (!player.equals(getFirstPassenger())) {
            player.startRiding(this);
        }
        player.fallDistance = 0;
        float dx = (float) (-Math.sin(Math.toRadians(player.getYaw())));
        float dz = (float) (Math.cos(Math.toRadians(player.getYaw())));

        float gravity = touchingWater ? 0 : -0.4f;
        this.addVelocity(dx, gravity, dz);
        this.setVelocity(getVelocity().normalize().multiply(getSpeed() * (touchingWater ? 0.65f : 1f)));

        this.move(MovementType.SELF, this.getVelocity());
    }


    @Override
    public void onRemoved() {
        summonParticles(this, random,
                ParticleTypes.POOF,
                0.25f, 25);
        this.getWorld().playSound(getX(), getY(), getZ(), WIND_BURST_SOUND_EVENT, SoundCategory.BLOCKS, 1, (1.0f + (this.getWorld().random.nextFloat() - this.getWorld().random.nextFloat()) * 0.2f) * 0.7f, true);
        if(!getWorld().isClient && getOwner() != null){
            PlayerEntity owner = (PlayerEntity) getOwner();
            Bender.getBender((ServerPlayerEntity) owner).currAbility.onRemove(Bender.getBender((ServerPlayerEntity) owner));
        }
    }


    public void setSpeed(float speed) {
        this.dataTracker.set(SPEED, speed);
    }

    public float getSpeed() {
        return this.dataTracker.get(SPEED);
    }

    @Override
    public boolean discardsOnNullOwner() {
        return true;
    }
}
